package net.dankito.newsreader.summary


class HeiseNewsArticleSummaryExtractor : HeiseNewsAndDeveloperArticleSummaryExtractorBase(), IArticleSummaryExtractor {

    override fun getName(): String {
        return "Heise News"
    }

    override fun getBaseUrl(): String {
        return "https://www.heise.de/"
    }

}