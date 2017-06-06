package net.dankito.newsreader.summary.config


interface ConfigChangedListener {

    fun configChanged(config: ArticleSummaryExtractorConfig)

}