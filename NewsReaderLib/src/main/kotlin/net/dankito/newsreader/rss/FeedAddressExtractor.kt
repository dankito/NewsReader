package net.dankito.newsreader.rss

import net.dankito.newsreader.AsyncResult
import net.dankito.newsreader.summary.ExtractorBase
import net.dankito.newsreader.util.web.IWebClient
import net.dankito.newsreader.util.web.OkHttpWebClient
import net.dankito.newsreader.util.web.RequestParameters
import net.dankito.newsreader.util.web.responses.WebClientResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URL
import kotlin.concurrent.thread


class FeedAddressExtractor {

    companion object {
        private val RSS_FEED_TYPE = "application/rss+xml"
        private val ATOM_FEED_TYPE = "application/atom+xml"
    }

    private val webClient : IWebClient = OkHttpWebClient() // TODO: inject


    fun extractFeedAddressesAsync(url: String, callback: (AsyncResult<List<FeedAddress>>) -> Unit) {
        thread {
            try {
                callback(AsyncResult(true, result = extractFeedAddresses(url)))
            } catch(e: Exception) { callback(AsyncResult(false, e)) }
        }
    }

    private fun extractFeedAddresses(url: String) : List<FeedAddress> {
        val parameters = RequestParameters(url)
        parameters.userAgent = ExtractorBase.DEFAULT_USER_AGENT
        parameters.connectionTimeoutMillis = ExtractorBase.DEFAULT_CONNECTION_TIMEOUT_MILLIS
        parameters.countConnectionRetries = 2

        webClient.get(parameters)?.let { response ->
            if(response.isSuccessful) {
                return extractFeedAddresses(response, url)
            }
        }

        return listOf()
    }

    private fun extractFeedAddresses(response: WebClientResponse, url: String): List<FeedAddress> {
        val document = Jsoup.parse(response.body, url)

        return extractFeedAddresses(document, url)
    }

    fun extractFeedAddresses(document: Document, siteUrl: String): List<FeedAddress> {
        return document.head().select("link[rel=\"alternate\"]")
                .map { tryToMapLinkElementToFeedAddresses(it, siteUrl) }.filterNotNull()
    }

    private fun tryToMapLinkElementToFeedAddresses(linkElement: Element, siteUrl: String): FeedAddress? {
        val typeString = linkElement.attr("type")

        val type = if(typeString == ATOM_FEED_TYPE) {
            FeedType.Atom
        }
        else if(typeString == RSS_FEED_TYPE) {
            FeedType.Rss
        }
        else {
            return null // not a feed link element
        }

        val url = makeLinkAbsolute(linkElement.attr("href"), siteUrl)
        val title = linkElement.attr("title")

        return FeedAddress(url, title, type)
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