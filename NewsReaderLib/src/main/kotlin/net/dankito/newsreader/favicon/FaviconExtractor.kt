package net.dankito.newsreader.favicon

import net.dankito.newsreader.AsyncResult
import net.dankito.newsreader.summary.ExtractorBase
import net.dankito.newsreader.util.web.IWebClient
import net.dankito.newsreader.util.web.OkHttpWebClient
import net.dankito.newsreader.util.web.RequestParameters
import net.dankito.newsreader.util.web.responses.WebClientResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import java.net.URL
import kotlin.concurrent.thread


class FaviconExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(FaviconExtractor::class.java)
    }


    private val webClient : IWebClient = OkHttpWebClient() // TODO: inject


    fun extractFaviconsAsync(url: String, callback: (AsyncResult<List<Favicon>>) -> Unit) {
        thread {
            try {
                callback(AsyncResult(true, result = extractFavicons(url)))
            } catch(e: Exception) {
                log.error("Could not get favicons for " + url, e)
                callback(AsyncResult(false, e))
            }
        }
    }

    private fun extractFavicons(url: String) : List<Favicon> {
        val parameters = RequestParameters(url)
        parameters.userAgent = ExtractorBase.DEFAULT_USER_AGENT
        parameters.connectionTimeoutMillis = ExtractorBase.DEFAULT_CONNECTION_TIMEOUT_MILLIS
        parameters.countConnectionRetries = 2

        webClient.get(parameters)?.let { response ->
            if(response.isSuccessful) {
                return extractFavicons(response, url)
            }
        }

        return listOf()
    }

    private fun extractFavicons(response: WebClientResponse, url: String): List<Favicon> {
        val document = Jsoup.parse(response.body, url)

        return extractFavicons(document, url)
    }

    fun extractFavicons(document: Document, url: String): List<Favicon> {
        val extractedFavicons = document.head().select("link, meta").map { mapElementToFavicon(it, url) }.filterNotNull().toMutableList()

        if(extractedFavicons.isEmpty()) {
            tryToFindDefaultFavicon(url, extractedFavicons)
        }

        return extractedFavicons
    }

    private fun tryToFindDefaultFavicon(url: String, extractedFavicons: MutableList<Favicon>) {
        val urlInstance = URL(url)
        val defaultFaviconUrl = urlInstance.protocol + "://" + urlInstance.host + "/favicon.ico"
        webClient.get(RequestParameters(defaultFaviconUrl, false)).let { response ->
            if (response.isSuccessful) {
                extractedFavicons.add(Favicon(defaultFaviconUrl, FaviconType.ShortcutIcon))
            }
        }
    }

    /**
     * Possible formats are documented here https://stackoverflow.com/questions/21991044/how-to-get-high-resolution-website-logo-favicon-for-a-given-url#answer-22007642
     * and here https://en.wikipedia.org/wiki/Favicon
     */
    private fun mapElementToFavicon(linkOrMetaElement: Element, siteUrl: String): Favicon? {
        if(linkOrMetaElement.nodeName() == "link") {
            return mapLinkElementToFavicon(linkOrMetaElement, siteUrl)
        }
        else if(linkOrMetaElement.nodeName() == "meta") {
            return mapMetaElementToFavicon(linkOrMetaElement, siteUrl)
        }

        return null
    }

    private fun mapLinkElementToFavicon(linkElement: Element, siteUrl: String): Favicon? {
        if(linkElement.hasAttr("rel")) {
            val relValue = linkElement.attr("rel")

            if(relValue == "icon") {
                return createFavicon(linkElement.attr("href"), siteUrl, FaviconType.Icon, linkElement.attr("sizes"), linkElement.attr("type"))
            }
            else if(relValue.startsWith("apple-touch-icon")) {
                val iconType = if(relValue.endsWith("-precomposed")) FaviconType.AppleTouchPrecomposed else FaviconType.AppleTouch
                return createFavicon(linkElement.attr("href"), siteUrl, iconType, linkElement.attr("sizes"), linkElement.attr("type"))
            }
            else if(relValue == "shortcut icon") {
                return createFavicon(linkElement.attr("href"), siteUrl, FaviconType.ShortcutIcon, linkElement.attr("sizes"), linkElement.attr("type"))
            }
        }

        return null
    }

    private fun mapMetaElementToFavicon(metaElement: Element, siteUrl: String): Favicon? {
        if(isOpenGraphImageDeclaration(metaElement)) {
            return Favicon(makeLinkAbsolute(metaElement.attr("content"), siteUrl), FaviconType.OpenGraphImage)
        }
        else if(isMsTileMetaElement(metaElement)) {
            return Favicon(makeLinkAbsolute(metaElement.attr("content"), siteUrl), FaviconType.MsTileImage)
        }

        return null
    }

    private fun isOpenGraphImageDeclaration(metaElement: Element) = metaElement.hasAttr("property") && metaElement.attr("property") == "og:image" && metaElement.hasAttr("content")

    private fun isMsTileMetaElement(metaElement: Element) = metaElement.hasAttr("name") && metaElement.attr("name") == "msapplication-TileImage" && metaElement.hasAttr("content")


    private fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, sizeString: String?, type: String?): Favicon? {
        if(url != null) {
            val favicon = Favicon(makeLinkAbsolute(url, siteUrl), iconType, type = type)

            if (sizeString != null) {
                favicon.setSizeFromString(sizeString)
            }

            return favicon
        }

        return null
    }


    private fun makeLinkAbsolute(url: String, siteUrl: String): String {
        var absoluteUrl = url

        if(url.startsWith("//")) {
            if(siteUrl.startsWith("https:")) {
                absoluteUrl = "https:" + url
            }
            else {
                absoluteUrl = "http" + url;
            }
        }
        else if(url.startsWith("/")) {
            var urlInstance = URL(URL(siteUrl), url)
            absoluteUrl = urlInstance.toExternalForm()
        }

        return absoluteUrl
    }

}