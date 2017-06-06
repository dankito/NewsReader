package net.dankito.newsreader.summary

import net.dankito.newsreader.model.ArticleSummary
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat

class HeiseDeveloperArticleSummaryExtractorTest : ArticleSummaryExtractorTestBase() {

    override fun createArticleSummaryExtractor(): IArticleSummaryExtractor {
        return HeiseDeveloperArticleSummaryExtractor()
    }

    override fun testCanLoadMoreItems(summary: ArticleSummary) {
        assertThat(summary.canLoadMoreItems, `is`(false))
        assertThat(summary.nextItemsUrl, CoreMatchers.nullValue())
    }

}