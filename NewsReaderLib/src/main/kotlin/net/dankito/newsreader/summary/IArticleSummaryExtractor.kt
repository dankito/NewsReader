package net.dankito.newsreader.summary

import net.dankito.newsreader.AsyncResult
import net.dankito.newsreader.model.ArticleSummary


interface IArticleSummaryExtractor {

    fun extractSummaryAsync(callback: (AsyncResult<out ArticleSummary>) -> Unit)

    fun loadMoreItems(callback: (AsyncResult<ArticleSummary>) -> Unit)

}