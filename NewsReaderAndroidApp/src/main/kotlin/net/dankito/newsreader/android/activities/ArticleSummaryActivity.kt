package net.dankito.newsreader.android.activities

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_article_summary.*
import net.dankito.newsreader.R
import net.dankito.newsreader.android.adapter.ArticleSummaryAdapter
import net.dankito.newsreader.android.util.AndroidFileStorageService
import net.dankito.newsreader.article.ArticleExtractors
import net.dankito.newsreader.model.Article
import net.dankito.newsreader.model.ArticleSummary
import net.dankito.newsreader.model.ArticleSummaryItem
import net.dankito.newsreader.serialization.ISerializer
import net.dankito.newsreader.serialization.JacksonJsonSerializer
import net.dankito.newsreader.summary.config.ArticleSummaryExtractorConfig
import net.dankito.newsreader.summary.config.ArticleSummaryExtractorConfigManager
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

class ArticleSummaryActivity : AppCompatActivity() {

    companion object {
        const val EXTRACTOR_URL_INTENT_EXTRA_NAME = "EXTRACTOR_URL"
        const val LAST_LOADED_SUMMARY_INTENT_EXTRA_NAME = "LAST_LOADED_SUMMARY"

        private val log = LoggerFactory.getLogger(ArticleSummaryActivity::class.java)
    }


    private lateinit var extractorsConfigManager: ArticleSummaryExtractorConfigManager // TODO: inject

    private val articleExtractors = ArticleExtractors() // TODO: inject

    private val serializer: ISerializer = JacksonJsonSerializer() // TODO: inject

    private var extractorConfig: ArticleSummaryExtractorConfig? = null

    private val adapter = ArticleSummaryAdapter()

    private var lastLoadedSummary: ArticleSummary? = null

    private var mnLoadMore: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extractorsConfigManager = ArticleSummaryExtractorConfigManager(AndroidFileStorageService(this))

        setupUI()

        savedInstanceState?.let { restoreState(it) }

        restoreState(intent)
    }

    private fun restoreState(intent: Intent) {
        restoreState(intent.getStringExtra(EXTRACTOR_URL_INTENT_EXTRA_NAME), intent.getStringExtra(LAST_LOADED_SUMMARY_INTENT_EXTRA_NAME))
    }

    private fun restoreState(savedInstanceState: Bundle) {
        restoreState(savedInstanceState.getString(EXTRACTOR_URL_INTENT_EXTRA_NAME), savedInstanceState.getString(LAST_LOADED_SUMMARY_INTENT_EXTRA_NAME))
    }

    private fun restoreState(extractorClass: String?, serializedLastLoadedSummary: String?) {
        extractorClass?.let { initializeArticlesSummaryExtractor(it) }

        if(serializedLastLoadedSummary != null) {
            val summary = serializer.deserializeObject(serializedLastLoadedSummary, ArticleSummary::class.java)
            showArticleSummary(summary, false) // TODO: this is wrong as it only shows last loaded items but not all loaded items
        }
        else {
            extractArticlesSummary()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putString(EXTRACTOR_URL_INTENT_EXTRA_NAME, extractorConfig?.url)

        outState?.putString(LAST_LOADED_SUMMARY_INTENT_EXTRA_NAME, null) // fallback
        lastLoadedSummary?.let { outState?.putString(LAST_LOADED_SUMMARY_INTENT_EXTRA_NAME, serializer.serializeObject(it)) } // if not null
    }


    private fun setupUI() {
        setContentView(R.layout.activity_article_summary)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lstArticleSummaryItems.adapter = adapter
        lstArticleSummaryItems.setOnItemClickListener { _, _, position, _ -> articleClicked(adapter.getItem(position)) }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_article_summary_menu, menu)

        mnLoadMore = menu?.findItem(R.id.mnLoadMore)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        if(id == R.id.mnReload) {
            extractArticlesSummary()
            return true
        }
        else if(id == R.id.mnLoadMore) {
            loadMoreItems()
            return true
        }
        else {
            return super.onOptionsItemSelected(item)
        }
    }


    private fun initializeArticlesSummaryExtractor(extractorClassName: String) {
        try {
            extractorsConfigManager.getConfig(extractorClassName)?.let { config ->
                this.extractorConfig = config

                supportActionBar?.title = config.name

                if (config.iconUrl != null) {
                    showExtractorIcon(config)
                }
            }
        } catch(e: Exception) { }
    }

    private fun showExtractorIcon(config: ArticleSummaryExtractorConfig) {
        thread {
            try {
                val icon = Picasso.with(this).load(config.iconUrl).get()
                runOnUiThread {
                    supportActionBar?.setIcon(BitmapDrawable(icon))
                }
            } catch(e: Exception) { log.error("Could not load icon from url " + config.iconUrl, e) }
        }.start()
    }

    private fun extractArticlesSummary() {
        extractorConfig?.extractor?.extractSummaryAsync {
            it.result?.let { showArticleSummaryThreadSafe(it, false) }
        }
    }

    private fun loadMoreItems() {
        extractorConfig?.extractor?.loadMoreItems {
            it.result?.let { showArticleSummaryThreadSafe(it, true)  }
        }
    }

    private fun showArticleSummaryThreadSafe(summary: ArticleSummary, hasLoadedMoreItems: Boolean) {
        runOnUiThread { showArticleSummary(summary, hasLoadedMoreItems) }
    }

    private fun showArticleSummary(summary: ArticleSummary, hasLoadedMoreItems: Boolean) {
        this.lastLoadedSummary = summary

        mnLoadMore?.isVisible = summary.canLoadMoreItems

        if(hasLoadedMoreItems) {
            adapter.moreItemsHaveBeenLoaded(summary)
        }
        else {
            adapter.setArticleSummary(summary)
        }
    }

    private fun articleClicked(item: ArticleSummaryItem) {
        articleExtractors.getExtractorForItem(item)?.let { extractor ->
            extractor.extractArticleAsync(item) { asyncResult ->
                asyncResult.result?.let { showArticle(it) }
            }
        }
    }

    private fun showArticle(article: Article) {
        val serializedArticle = serializer.serializeObject(article)

        val viewArticleIntent = Intent(this, ViewArticleActivity::class.java)

        viewArticleIntent.putExtra(ViewArticleActivity.ARTICLE_INTENT_EXTRA_NAME, serializedArticle);

        startActivity(viewArticleIntent)
    }

}
