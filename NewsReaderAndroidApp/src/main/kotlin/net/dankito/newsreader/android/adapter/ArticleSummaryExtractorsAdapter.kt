package net.dankito.newsreader.android.adapter

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.list_item_article_summary_extractor.view.*
import net.dankito.newsreader.R
import net.dankito.newsreader.android.util.AndroidFileStorageService
import net.dankito.newsreader.icon.ImageCache
import net.dankito.newsreader.summary.config.ArticleSummaryExtractorConfig
import java.io.File


class ArticleSummaryExtractorsAdapter(context: Context, extractors: List<ArticleSummaryExtractorConfig>) : ListAdapter<ArticleSummaryExtractorConfig>(extractors) {


    val fileStorageService = AndroidFileStorageService(context)

    val imageCache = ImageCache(fileStorageService) // TODO: inject


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val extractorConfig = getItem(position)

        var view = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.list_item_article_summary_extractor, parent, false)

        showExtractorIcon(view, extractorConfig)

        view.txtExtractorName.text = extractorConfig.name

        view.tag = extractorConfig

        return view
    }

    private fun showExtractorIcon(view: View, extractorConfig: ArticleSummaryExtractorConfig) {
        val imageView = view.imgPreviewImage

        imageView.tag = extractorConfig.iconUrl
        imageView.setImageBitmap(null)

        extractorConfig.iconUrl?.let { iconUrl ->
            imageCache.getCachedForRetrieveIconForUrlAsync(iconUrl) { result ->
                result.result?.let { iconPath ->
                    if(iconUrl == imageView.tag) { // check if icon in imgPreviewImage still for the same iconUrl should be displayed
                        showIcon(imageView, iconPath, view)
                    }
                }
            }

        }
    }

    private fun showIcon(imageView: ImageView, iconPath: File, view: View) {
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            imageView.setImageURI(Uri.fromFile(iconPath))
        } else {
            (view.context as Activity).runOnUiThread {
                imageView.setImageURI(Uri.fromFile(iconPath))
            }
        }
    }

}