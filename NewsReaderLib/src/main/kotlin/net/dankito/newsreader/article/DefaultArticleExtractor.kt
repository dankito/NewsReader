package net.dankito.newsreader.article

import net.dankito.newsreader.AsyncResult
import net.dankito.newsreader.model.Article
import net.dankito.newsreader.model.ArticleSummaryItem
import net.dankito.newsreader.summary.ArticleExtractorBase
import org.jsoup.nodes.Document
import kotlin.concurrent.thread


class DefaultArticleExtractor : ArticleExtractorBase(), IArticleExtractor {

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