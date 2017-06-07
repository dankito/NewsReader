package net.dankito.newsreader.android.dialogs

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ListView
import kotlinx.android.synthetic.main.dialog_add_article_summary_extractor.view.*
import net.dankito.newsreader.R
import net.dankito.newsreader.android.activities.ArticleSummaryActivity
import net.dankito.newsreader.android.adapter.FoundFeedAddressesAdapter
import net.dankito.newsreader.model.FeedArticleSummary
import net.dankito.newsreader.rss.FeedAddress
import net.dankito.newsreader.rss.FeedAddressExtractor
import net.dankito.newsreader.rss.FeedReader
import net.dankito.newsreader.serialization.ISerializer
import net.dankito.newsreader.serialization.JacksonJsonSerializer
import net.dankito.newsreader.summary.config.ArticleSummaryExtractorConfigManager


class AddArticleSummaryExtractorDialog(val extractorsConfigManager: ArticleSummaryExtractorConfigManager) : DialogFragment() {

    companion object {
        val TAG = "ADD_ARTICLE_SUMMARY_EXTRACTOR_DIALOG"
    }


    private val feedReader = FeedReader() // TODO: inject

    private val feedAddressExtractor = FeedAddressExtractor() // TODO: inject

    private val serializer: ISerializer = JacksonJsonSerializer() // TODO: inject

    private val feedAddressesAdapter = FoundFeedAddressesAdapter()

    private var lstFeedSearchResults: ListView? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.dialog_add_article_summary_extractor, container)

        view?.let { view ->
            view.btnCheckFeedOrWebsiteUrl?.setOnClickListener { checkFeedOrWebsiteUrl(view.edtxtFeedOrWebsiteUrl.text.toString()) }

            this.lstFeedSearchResults = view.lstFeedSearchResults
            view.lstFeedSearchResults.adapter = feedAddressesAdapter
            view.lstFeedSearchResults.setOnItemClickListener { _, _, position, _ -> foundFeedAddressSelected(position) }
        }

        return view
    }

    private fun foundFeedAddressSelected(position: Int) {
        val feedAddress = feedAddressesAdapter.getItem(position)

        feedReader.readFeedAsync(feedAddress.url) {
            it.result?.let { feedAdded(feedAddress.url, it) }
            it.error?.let { showError(feedAddress.url, it) }
        }
    }

    private fun checkFeedOrWebsiteUrl(feedOrWebsiteUrl: String) {
        feedReader.readFeedAsync(feedOrWebsiteUrl) {
            if(it.result != null) {
                feedAdded(feedOrWebsiteUrl, it.result as FeedArticleSummary)
            }
            else {
                feedAddressExtractor.extractFeedAddressesAsync(feedOrWebsiteUrl) { asyncResult ->
                    if(asyncResult.result != null) {
                        val feedAddresses = asyncResult.result as List<FeedAddress>;
                        if(feedAddresses.size == 0) {
                            showNoFeedAddressesFoundError(feedOrWebsiteUrl)
                        }
                        else {
                            showFoundFeedAddresses(feedAddresses)
                        }
                    }
                    else {
                        asyncResult.error?.let { showError(feedOrWebsiteUrl, it) }
                    }
                }
            }
        }
    }

    private fun feedAdded(feedUrl: String, summary: FeedArticleSummary) {
        activity.runOnUiThread {
            val askExtractorNameDialog = AskExtractorNameDialog()

            askExtractorNameDialog.askForName(activity, summary.title ?: "", false) { didSelectName, selectedName ->
                val selectedExtractorName = if(didSelectName) selectedName ?: "" else summary.title ?: ""

                feedAdded(feedUrl, summary, selectedExtractorName)
            }
        }
    }

    private fun feedAdded(feedUrl: String, summary: FeedArticleSummary, selectedExtractorName: String) {
        summary.title = selectedExtractorName

        extractorsConfigManager.addFeed(feedUrl, summary)

        showArticleSummaryActivity(feedUrl, summary)

        dismiss()
    }

    private fun showArticleSummaryActivity(feedUrl: String, summary: FeedArticleSummary) {
        val intent = Intent(activity, ArticleSummaryActivity::class.java)

        intent.putExtra(ArticleSummaryActivity.EXTRACTOR_URL_INTENT_EXTRA_NAME, feedUrl)
        intent.putExtra(ArticleSummaryActivity.LAST_LOADED_SUMMARY_INTENT_EXTRA_NAME, serializer.serializeObject(summary))

        startActivity(intent)
    }

    private fun showFoundFeedAddresses(result: List<FeedAddress>) {
        activity.runOnUiThread {
            feedAddressesAdapter.setItems(result)

            lstFeedSearchResults?.visibility = VISIBLE
        }
    }

    private fun showNoFeedAddressesFoundError(feedOrWebsiteUrl: String) {
        showErrorThreadSafe(getString(R.string.error_no_rss_or_atom_feed_found_for_url, feedOrWebsiteUrl))
    }

    private fun showError(feedOrWebsiteUrl: String, error: Exception) {
        showErrorThreadSafe(getString(R.string.error_cannot_read_feed_or_extract_feed_addresses_from_url, feedOrWebsiteUrl, error.localizedMessage))
    }

    private fun showErrorThreadSafe(error: String) {
        activity.runOnUiThread { showError(error) }
    }

    private fun showError(error: String) {
        var builder = AlertDialog.Builder(activity)

        builder.setMessage(error)

        builder.setNegativeButton(android.R.string.ok, null)

        builder.create().show()
    }

}