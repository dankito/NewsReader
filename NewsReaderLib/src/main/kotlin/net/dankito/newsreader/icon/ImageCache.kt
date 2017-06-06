package net.dankito.newsreader.icon

import net.dankito.newsreader.AsyncResult
import net.dankito.newsreader.serialization.ISerializer
import net.dankito.newsreader.serialization.JacksonJsonSerializer
import net.dankito.newsreader.summary.config.ArticleSummaryExtractorConfigManager
import net.dankito.newsreader.util.IFileStorageService
import net.dankito.newsreader.util.web.IWebClient
import net.dankito.newsreader.util.web.OkHttpWebClient
import net.dankito.newsreader.util.web.RequestParameters
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URI


class ImageCache(val fileStorageService: IFileStorageService) {

    companion object {
        private val CACHE_FILE_NAME = "ImageCache.json"

        private val log = LoggerFactory.getLogger(ArticleSummaryExtractorConfigManager::class.java)
    }


    private var imageCache: MutableMap<String, File> = hashMapOf()

    private val webClient: IWebClient = OkHttpWebClient() // TODO: inject

    private val serializer: ISerializer = JacksonJsonSerializer() // TODO: inject


    init {
        readImageCache()
    }


    private fun readImageCache() {
        try {
            fileStorageService.readFromTextFile(CACHE_FILE_NAME)?.let { fileContent ->
                imageCache = serializer.deserializeObject(fileContent, HashMap::class.java, String::class.java, File::class.java) as
                        HashMap<String, File>
            }
        } catch(e: Exception) {
            log.error("Could not deserialize ImageCache", e)
        }
    }

    private fun saveImageCache() {
        try {
            val serializedCache = serializer.serializeObject(imageCache)
            fileStorageService.writeToTextFile(serializedCache, CACHE_FILE_NAME)
        } catch(e: Exception) {
            log.error("Could not save ImageCache", e)
        }
    }

    private fun addImageToCache(url: String, file: File) {
        imageCache.put(url, file)

        saveImageCache()
    }


    fun getCachedForRetrieveIconForUrlAsync(url: String, callback: (AsyncResult<File>) -> Unit) {
        try {
            val cachedImage = imageCache[url]

            if (cachedImage != null && cachedImage.exists()) {
                callback(AsyncResult(true, result = cachedImage))
            }
            else {
                retrieveAndCacheImage(url, callback)
            }
        } catch(e: Exception) {
            log.error("Could not retrieve image for url " + url, e)
            callback(AsyncResult(false, e))
        }
    }

    private fun retrieveAndCacheImage(url: String, callback: (AsyncResult<File>) -> Unit) {
        val file = getUniqueFilenameFromUrl(url)
        val fileStream = fileStorageService.createFileOutputStream(file.name)

        val parameters = RequestParameters(url)
        parameters.setHasStringResponse(false)
        parameters.setDownloadProgressListener { progress, downloadedChunk -> fileStream.write(downloadedChunk) }

        webClient.getAsync(parameters) { response ->
            fileStream.flush()
            fileStream.close()

            if(response.isSuccessful) {
                addImageToCache(url, file)
                callback(AsyncResult(true, result = file))
            }
            else {
                callback(AsyncResult(false, Exception(response.body)))
            }
        }
    }

    private fun getUniqueFilenameFromUrl(url: String): File {
        val uri = URI(url)

        return fileStorageService.getFileInDataFolder(uri.host + "_" + uri.path.replace('/', '_'))
    }

}