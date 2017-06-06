package net.dankito.newsreader.summary

import net.dankito.newsreader.article.HeiseNewsAndDeveloperArticleExtractor
import net.dankito.newsreader.model.ArticleSummary
import net.dankito.newsreader.model.ArticleSummaryItem
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


abstract class HeiseNewsAndDeveloperArticleSummaryExtractorBase : ArticleSummaryExtractorBase(), IArticleSummaryExtractor {

    override fun parseHtmlToArticleSummary(document: Document, forLoadingMoreItems: Boolean) : ArticleSummary {
        val summary = ArticleSummary(extractArticles(document))

        determineHasMore(summary, document)

        return summary
    }

    private fun determineHasMore(summary: ArticleSummary, document: Document) {
        var weitereMeldungenElements = document.body().select(".itemlist-nav a") // frontpage

        if(weitereMeldungenElements.size == 0) { // starting with page 2 the 'Weitere Meldungen' link changes
            weitereMeldungenElements = document.body().select("a.seite_weiter")
        }

        summary.canLoadMoreItems = weitereMeldungenElements.size == 1
        summary.nextItemsUrl = weitereMeldungenElements.first()?.let { makeLinkAbsolute(it.attr("href")) }
    }

    private fun extractArticles(document: Document): List<ArticleSummaryItem> {
        val articles = mutableListOf<ArticleSummaryItem>()

        articles.addAll(extractTopArticles(document))
        articles.addAll(extractIndexItems(document))

        return articles
    }

    private fun extractTopArticles(document: Document): Collection<ArticleSummaryItem> {
        val topArticleElements = document.select(".multiple")

        return topArticleElements.filterNotNull().map { parseTopArticle(it) }.filterNotNull()
    }

    private fun parseTopArticle(topArticleElement: Element): ArticleSummaryItem? {
        topArticleElement.select("a.the_content_url").first().let { contentUrlElement ->
            val article = ArticleSummaryItem(makeLinkAbsolute(contentUrlElement.attr("href")), contentUrlElement.attr("title"), HeiseNewsAndDeveloperArticleExtractor::class.java)

            extractDachzeile(contentUrlElement, article)

            topArticleElement.select(".img_clip img").first().let {
                article.previewImageUrl = makeLinkAbsolute(it.attr("src"))
            }

            topArticleElement.select("p").first().let { article.summary = it.text() }

            return article
        }

        return null
    }

    open protected fun extractDachzeile(contentUrlElement: Element, article: ArticleSummaryItem) {
        contentUrlElement.select(".dachzeile").first().let {
            if (it.text().isNullOrEmpty() == false) {
                article.title = it.text() + " - " + article.title
            }
        }
    }

    private fun extractIndexItems(document: Document): Collection<ArticleSummaryItem> {
        val indexItems = document.select(".indexlist_item")

        return indexItems.filterNotNull().map { parseIndexItem(it) }.filterNotNull()
    }

    private fun parseIndexItem(item: Element): ArticleSummaryItem? {
        item.select("header a").first().let { headerElement ->
            val article = ArticleSummaryItem(makeLinkAbsolute(headerElement.attr("href") ?: ""), headerElement.text() ?: "", HeiseNewsAndDeveloperArticleExtractor::class.java)

            item.select(".indexlist_text").first().let { textElement ->
                article.summary = textElement.text()

                textElement.select("img").let { article.previewImageUrl = makeLinkAbsolute(it.attr("src")) }
            }

            return article
        }

        return null
    }


    override fun siteUsesHttps(): Boolean {
        return true
    }

    override fun getLinkBaseUrl(): String {
        return "https://www.heise.de"
    }

}