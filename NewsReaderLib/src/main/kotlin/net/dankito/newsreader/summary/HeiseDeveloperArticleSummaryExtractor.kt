package net.dankito.newsreader.summary

import net.dankito.newsreader.model.ArticleSummaryItem
import org.jsoup.nodes.Element


class HeiseDeveloperArticleSummaryExtractor : HeiseNewsAndDeveloperArticleSummaryExtractorBase(), IArticleSummaryExtractor {


    override fun getName(): String {
        return "Heise Developer"
    }

    override fun getBaseUrl() : String {
        return "https://www.heise.de/developer/"
    }


    override fun extractDachzeile(contentUrlElement: Element, article: ArticleSummaryItem) {
        // don't extract .dachzeile for Heise Developer as it only contains 'Topmeldungen' or 'Topartikel'
    }

}