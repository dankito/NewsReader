package net.dankito.newsreader.article

import net.dankito.newsreader.AsyncResult
import net.dankito.newsreader.model.Article
import net.dankito.newsreader.model.ArticleSummaryItem


interface IArticleExtractor {

    fun extractArticleAsync(item : ArticleSummaryItem, callback: (AsyncResult<Article>) -> Unit)

    fun extractArticleAsync(url : String, callback: (AsyncResult<Article>) -> Unit)

}