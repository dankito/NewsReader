package net.dankito.newsreader.summary

import net.dankito.newsreader.util.web.IWebClient
import net.dankito.newsreader.util.web.OkHttpWebClient
import net.dankito.newsreader.util.web.RequestParameters
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


abstract class ExtractorBase {

    companion object {
        const val DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0"

        const val DEFAULT_CONNECTION_TIMEOUT_MILLIS = 2000
    }


    private val webClient : IWebClient = OkHttpWebClient()


    protected fun requestUrl(url: String): Document {
        val parameters = RequestParameters(url)
        parameters.userAgent = DEFAULT_USER_AGENT
        parameters.connectionTimeoutMillis = DEFAULT_CONNECTION_TIMEOUT_MILLIS
        parameters.countConnectionRetries = 2

        webClient.get(parameters).let { response ->
            if(response.isSuccessful) {
                return Jsoup.parse(response.body, url)
            }
            else {
                throw Exception(response.error)
            }
        }
    }


    protected fun makeLinkAbsolute(url: String): String {
        var absoluteUrl = url

        if(url.startsWith("//")) {
            if(siteUsesHttps()) {
                absoluteUrl = "https:" + url
            }
            else {
                absoluteUrl = "http" + url;
            }
        }
        else if(url.startsWith("/")) {
            absoluteUrl = getLinkBaseUrl() + url
        }

        return absoluteUrl
    }


    abstract protected fun siteUsesHttps() : Boolean

    abstract protected fun getLinkBaseUrl() : String

}