package net.dankito.newsreader.article

import net.dankito.newsreader.model.Article
import net.dankito.newsreader.summary.ArticleExtractorBase
import org.jsoup.nodes.Comment
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.text.SimpleDateFormat
import java.util.*


class HeiseNewsAndDeveloperArticleExtractor : ArticleExtractorBase(), IArticleExtractor {

    private val DateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")


    override fun parseHtmlToArticle(document: Document, url: String): Article? {
        document.body().select("article").first().let { article ->
            article.select("header").first().let { header ->
                header.select(".article__heading").first()?.text()?.let { title ->
                    return parseArticle(header, article, url, title)
                }
            }
        }

        return null
    }

    private fun parseArticle(header: Element, article: Element, url: String, title: String) : Article? {
        article.select(".meldung_wrapper").first()?.let { articleElement ->
            val abstract = articleElement.select(".meldung_anrisstext").first()?.text()
            val previewImageUrl = makeLinkAbsolute(articleElement.select(".aufmacherbild img").first()?.attr("src") ?: "")
            val publishingDate = extractPublishingDate(header)

            val content = extractContent(article)

            return Article(url, title, content, abstract, publishingDate, previewImageUrl)
        }

        return null
    }

    private fun extractContent(articleElement: Element): String {
        return articleElement.select(".meldung_wrapper").first().children().filter { element ->
            element.hasClass("meldung_anrisstext") == false && containsOnlyComment(element) == false
        }.joinToString(separator = "") { getContentElementHtml(it) }
    }

    private fun containsOnlyComment(element: Element) : Boolean {
        return element.childNodeSize() == 3 && element.childNode(1) is Comment && element.childNode(0) is TextNode && element.childNode(2) is TextNode
    }

    private fun getContentElementHtml(element: Element) : String {
        makeLinksAbsolute(element)
        return element.outerHtml()
    }

    private fun extractPublishingDate(header: Element): Date? {
        header.select(".publish-info time").first().let {
            return DateTimeFormat.parse(it.attr("datetime"))
        }
    }


    override fun siteUsesHttps(): Boolean {
        return true
    }

    override fun getLinkBaseUrl(): String {
        return "https://www.heise.de"
    }

}