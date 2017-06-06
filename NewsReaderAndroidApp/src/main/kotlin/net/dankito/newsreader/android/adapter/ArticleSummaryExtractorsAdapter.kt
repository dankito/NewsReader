package net.dankito.newsreader.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_article_summary_extractor.view.*
import net.dankito.newsreader.R
import net.dankito.newsreader.summary.config.ArticleSummaryExtractorConfig


class ArticleSummaryExtractorsAdapter(extractors: List<ArticleSummaryExtractorConfig>) : ListAdapter<ArticleSummaryExtractorConfig>(extractors) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val extractorConfig = getItem(position)

        var view = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.list_item_article_summary_extractor, parent, false)

        view.imgPreviewImage.setImageBitmap(null)
        extractorConfig.iconUrl?.let { Picasso.with(view.context).load(it).into(view.imgPreviewImage) }

        view.txtExtractorName.text = extractorConfig.name

        view.tag = extractorConfig

        return view
    }

}