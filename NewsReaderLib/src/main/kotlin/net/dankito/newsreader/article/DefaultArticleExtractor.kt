package net.dankito.newsreader.article

import net.dankito.newsreader.AsyncResult
import net.dankito.newsreader.model.Article
import net.dankito.newsreader.model.ArticleSummaryItem
import net.dankito.newsreader.summary.ArticleExtractorBase
import net.dankito.newsreader.util.web.CookieHandling
import net.dankito.newsreader.util.web.RequestParameters
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread


class DefaultArticleExtractor : ArticleExtractorBase(), IArticleExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(DefaultArticleExtractor::class.java)
    }


    override fun extractArticleAsync(item: ArticleSummaryItem, callback: (AsyncResult<Article>) -> Unit) {
        thread {
            try {
                val article = extractArticle(item);
                if(article != null) {
                    callback(AsyncResult(true, result = article))
                }
                else {
                    callback(AsyncResult(false))
                }
            } catch(e: Exception) {
                log.error("Could not extract Article", e)
                callback(AsyncResult(false, e))
            }
        }
    }

    private fun extractArticle(item: ArticleSummaryItem) : Article? {
        extractContent(item)?.let { content ->
            return Article(item.url, item.title, content, item.summary, item.publishedDate, item.previewImageUrl)
        }

        return null
    }

    private fun extractContent(item: ArticleSummaryItem) : String? {
        requestUrl(item.url)?.let { document ->
            var content = document.body().toString()
            ArticleTextExtractor.extractContent(document, item.summary)?.let { content = it }

            return content
        }

        return null
    }

    override fun createParametersForUrl(url: String): RequestParameters {
        val parameters = super.createParametersForUrl(url)

        parameters.cookieHandling = CookieHandling.ACCEPT_ALL_ONLY_FOR_THIS_CALL // some site like New York Times require that cookies are enabled

        return parameters
    }

    override fun parseHtmlToArticle(document: Document, url: String): Article? {
        return null // will not be called in this case
    }

    override fun siteUsesHttps(): Boolean {
        return false // TODO
    }

    override fun getLinkBaseUrl(): String {
        return "" // TODO
    }
}