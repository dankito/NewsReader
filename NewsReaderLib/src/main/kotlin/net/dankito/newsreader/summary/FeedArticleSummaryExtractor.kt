package net.dankito.newsreader.summary

import net.dankito.newsreader.AsyncResult
import net.dankito.newsreader.model.ArticleSummary
import net.dankito.newsreader.rss.FeedReader


class FeedArticleSummaryExtractor(val feedUrl : String) : IArticleSummaryExtractor {

    private val feedReader = FeedReader() // TODO: inject


    override fun extractSummaryAsync(callback: (AsyncResult<out ArticleSummary>) -> Unit) {
        feedReader.readFeedAsync(feedUrl) {
            callback(it)
        }
    }

    override fun loadMoreItems(callback: (AsyncResult<ArticleSummary>) -> Unit) {
    }
}