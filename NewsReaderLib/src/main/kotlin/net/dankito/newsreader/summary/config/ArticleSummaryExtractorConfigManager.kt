package net.dankito.newsreader.summary.config

import net.dankito.newsreader.favicon.FaviconExtractor
import net.dankito.newsreader.favicon.FaviconSorter
import net.dankito.newsreader.model.FeedArticleSummary
import net.dankito.newsreader.serialization.ISerializer
import net.dankito.newsreader.serialization.JacksonJsonSerializer
import net.dankito.newsreader.summary.FeedArticleSummaryExtractor
import net.dankito.newsreader.summary.ImplementedArticleSummaryExtractors
import net.dankito.newsreader.util.IFileStorageService
import org.slf4j.LoggerFactory


class ArticleSummaryExtractorConfigManager(val fileStorageService: IFileStorageService) {

    companion object {
        private val FILE_NAME = "ArticleSummaryExtractorConfigurations.json"

        private val log = LoggerFactory.getLogger(ArticleSummaryExtractorConfigManager::class.java)
    }


    private var configurations: MutableMap<String, ArticleSummaryExtractorConfig> = linkedMapOf()

    private val faviconExtractor = FaviconExtractor() // TODO: inject

    private val faviconSorter = FaviconSorter() // TODO: inject

    private val serializer: ISerializer = JacksonJsonSerializer() // TODO: inject

    private val listeners = mutableListOf<ConfigChangedListener>()


    init {
        readPersistedConfigs()

        initImplementedExtractors()

        initAddedExtractors()
    }

    private fun readPersistedConfigs() {
        try {
            fileStorageService.readFromTextFile(FILE_NAME)?.let { fileContent ->
                configurations = serializer.deserializeObject(fileContent, LinkedHashMap::class.java, String::class.java, ArticleSummaryExtractorConfig::class.java) as
                        LinkedHashMap<String, ArticleSummaryExtractorConfig>

                configurations.forEach { _, config ->
                    if(config.iconUrl == null) {
                        loadIconAsync(config)
                    }
                }
            }
        } catch(e: Exception) {
            log.error("Could not deserialize ArticleSummaryExtractorConfigs", e)
        }
    }

    private fun initImplementedExtractors() {
        ImplementedArticleSummaryExtractors().getImplementedExtractors().forEach { implementedExtractor ->
            var config = configurations.get(implementedExtractor.getBaseUrl())

            if (config == null) {
                config = ArticleSummaryExtractorConfig(implementedExtractor, implementedExtractor.getBaseUrl(), implementedExtractor.getName())
                addConfig(config)
            } else {
                config.extractor = implementedExtractor
            }
        }
    }

    private fun initAddedExtractors() {
        configurations.forEach { _, config ->
            if(config.extractor == null) {
                config.extractor = FeedArticleSummaryExtractor(config.url)
            }
        }
    }

    private fun loadIconAsync(config: ArticleSummaryExtractorConfig) {
        loadIconAsync(config.url) { bestIconUrl ->
            bestIconUrl?.let {
                config.iconUrl = it
                saveConfig(config)
            }
        }
    }

    private fun loadIconAsync(url: String, callback: (String?) -> Unit)  {
        faviconExtractor.extractFaviconsAsync(url) {
            if(it.result != null) {
                callback(faviconSorter.getBestIcon(it.result)?.url)
            }
            else {
                callback(null)
            }
        }
    }


    fun getConfigs() : List<ArticleSummaryExtractorConfig> {
        return configurations.values.toList()
    }

    fun getConfig(id: String) : ArticleSummaryExtractorConfig? {
        return configurations[id]
    }


    fun addFeed(feedUrl: String, summary: FeedArticleSummary) {
        val extractor = FeedArticleSummaryExtractor(feedUrl)

        getIconForFeed(summary) {
            val config = ArticleSummaryExtractorConfig(extractor, feedUrl, summary.title ?: "", it)

            addConfig(config)
        }
    }

    private fun getIconForFeed(summary: FeedArticleSummary, callback: (iconUrl: String?) -> Unit) {
        summary.imageUrl?.let { iconUrl ->
            if(faviconSorter.hasMinSize(iconUrl)) {
                return callback(iconUrl)
            }
        }

        val siteUrl = summary.siteUrl
        if(siteUrl != null) {
            loadIconAsync(siteUrl) { iconUrl ->
                callback(iconUrl)
            }
        }
        else {
            callback(null)
        }
    }

    private fun addConfig(config: ArticleSummaryExtractorConfig) {
        configurations.put(config.url, config)

        saveConfig(config)

        if(config.iconUrl == null) {
            loadIconAsync(config)
        }
    }

    private fun saveConfig(config: ArticleSummaryExtractorConfig) {
        try {
            val serializedConfigurations = serializer.serializeObject(configurations)

            fileStorageService.writeToTextFile(serializedConfigurations, FILE_NAME)
        } catch(e: Exception) {
            log.error("Could not write configurations to " + FILE_NAME, e)
        }

        callListeners(config)
    }


    fun addListener(listener: ConfigChangedListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ConfigChangedListener) {
        listeners.remove(listener)
    }

    private fun callListeners(config: ArticleSummaryExtractorConfig) {
        listeners.forEach { it.configChanged(config) }
    }

}