package net.dankito.newsreader.article

import net.dankito.newsreader.model.Article
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

abstract class ArticleExtractorTestBase {

    private val underTest: IArticleExtractor

    init {
        underTest = createArticleExtractor()
    }

    abstract fun createArticleExtractor(): IArticleExtractor


    protected fun getAndTestArticle(url: String, title: String, abstract: String?, previewImageUrl: String? = null) {
        val article = getArticle(url)

        testArticle(article, url, title, abstract, previewImageUrl)
    }

    protected fun getArticle(url: String) : Article? {
        var article: Article? = null
        val countDownLatch = CountDownLatch(1)

        underTest.extractArticleAsync(url) {
            article = it.result

            countDownLatch.countDown()
        }

        countDownLatch.await(20, TimeUnit.SECONDS)

        return article
    }

    protected fun testArticle(article: Article?, url: String, title: String, abstract: String?, previewImageUrl: String? = null) {
        assertThat(article, notNullValue())

        article?.let { article ->
            assertThat(article.url, `is`(url))
            assertThat(article.title, `is`(title))
            assertThat(article.abstract, `is`(abstract))
            assertThat(article.content.isNullOrBlank(), `is`(false))
            assertThat(article.previewImageUrl, notNullValue())
            previewImageUrl?.let { assertThat(article.previewImageUrl, `is`(previewImageUrl)) }
            assertThat(article.publishingDate, notNullValue())
        }
    }

}