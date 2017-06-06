package net.dankito.newsreader.summary

import net.dankito.newsreader.AsyncResult
import net.dankito.newsreader.model.ArticleSummary
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread


abstract class ArticleSummaryExtractorBase : ExtractorBase(), IImplementedArticleSummaryExtractor {

    private val log = LoggerFactory.getLogger(ArticleSummaryExtractorBase::class.java)


    private var loadNextItemsUrl: String? = null


    override fun extractSummaryAsync(callback: (AsyncResult<out ArticleSummary>) -> Unit) {
        extractSummaryAsync(getBaseUrl(), false, callback)
    }

    override fun loadMoreItems(callback: (AsyncResult<ArticleSummary>) -> Unit) {
        val loadNextItemsUrl = this.loadNextItemsUrl

        if(loadNextItemsUrl == null) {
            callback(AsyncResult(false)) // TODO: add error
        }
        else {
            extractSummaryAsync(loadNextItemsUrl, true, callback)
        }
    }

    private fun extractSummaryAsync(url: String, isForLoadingMoreItems: Boolean, callback: (AsyncResult<ArticleSummary>) -> Unit) {
        thread {
            try {
                callback(AsyncResult(true, result = extractSummary(url, isForLoadingMoreItems)))
            } catch(e: Exception) {
                log.error("Could not get article summary for " + url, e)
                callback(AsyncResult(false, e))
            }
        }
    }

    private fun extractSummary(url: String, isForLoadingMoreItems: Boolean): ArticleSummary {
        requestUrl(url).let { document ->
            val summary = parseHtmlToArticleSummary(document, isForLoadingMoreItems)

            loadNextItemsUrl = summary.nextItemsUrl

            return summary
        }
    }

    abstract protected fun parseHtmlToArticleSummary(document: Document, forLoadingMoreItems: Boolean): ArticleSummary

}