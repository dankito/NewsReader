package net.dankito.newsreader.summary

import net.dankito.newsreader.article.PostillonArticleExtractor
import net.dankito.newsreader.model.ArticleSummary
import net.dankito.newsreader.model.ArticleSummaryItem
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


class PostillonArticleSummaryExtractor : ArticleSummaryExtractorBase() {

    override fun getName(): String {
        return "Der Postillon"
    }

    override fun getBaseUrl(): String {
        return "http://www.der-postillon.com"
    }


    override fun parseHtmlToArticleSummary(document: Document, isForLoadingMoreItems: Boolean) : ArticleSummary {
        val summary = ArticleSummary(extractArticles(document, isForLoadingMoreItems))

        determineHasMore(summary, document)

        return summary
    }

    private fun determineHasMore(summary: ArticleSummary, document: Document) {
        document.body().select("#Blog1_blog-pager-older-link").first()?.let {
            summary.nextItemsUrl = makeLinkAbsolute(it.attr("href"))
            summary.canLoadMoreItems = summary.nextItemsUrl != null
        }

    }

    private fun extractArticles(document: Document, isForLoadingMoreItems: Boolean): List<ArticleSummaryItem> {
        val articles = mutableListOf<ArticleSummaryItem>()

        articles.addAll(extractPosts(document))

        if(isForLoadingMoreItems == false) {
            articles.addAll(extractArchiveArticles(document))
        }

        return articles
    }

    private fun extractPosts(document: Document): Collection<ArticleSummaryItem> {
        val postElements = document.select(".post")

        return postElements.filterNotNull().map { parsePostArticle(it) }.filterNotNull()
    }

    private fun parsePostArticle(postElement: Element): ArticleSummaryItem? {
        postElement.select(".post-title > a").first()?.let { titleAnchor ->
            val article = ArticleSummaryItem(makeLinkAbsolute(titleAnchor.attr("href")), titleAnchor.text(), PostillonArticleExtractor::class.java)

            postElement.select(".post-body").first()?.let { contentElement ->
                article.summary = contentElement.text()

                contentElement.select("img").first()?.let { article.previewImageUrl = it.attr("src") }
            }

            return article
        }

        return null
    }

    private fun extractArchiveArticles(document: Document): Collection<ArticleSummaryItem> {
        val postElements = document.select(".archiv-artikel a")

        return postElements.filterNotNull().map { parseArchiveArticle(it) }.filterNotNull()
    }

    private fun parseArchiveArticle(archiveArticleElement: Element): ArticleSummaryItem? {
        archiveArticleElement.select(".text-wrapper").first()?.let {
            val article = ArticleSummaryItem(makeLinkAbsolute(archiveArticleElement.attr("href")), it.text(), PostillonArticleExtractor::class.java)

            archiveArticleElement.select(".img-wrapper img").first()?.let { article.previewImageUrl = it.attr("src") }

            return article
        }

        return null
    }


    override fun siteUsesHttps(): Boolean {
        return false
    }

    override fun getLinkBaseUrl(): String {
        return getBaseUrl()
    }

}