package net.dankito.newsreader.summary


class ImplementedArticleSummaryExtractors {

    private val extractors = LinkedHashMap<Class<out IImplementedArticleSummaryExtractor>, IImplementedArticleSummaryExtractor>()

    init {
        extractors.put(HeiseNewsArticleSummaryExtractor::class.java, HeiseNewsArticleSummaryExtractor())
        extractors.put(HeiseDeveloperArticleSummaryExtractor::class.java, HeiseDeveloperArticleSummaryExtractor())
        extractors.put(GuardianArticleSummaryExtractor::class.java, GuardianArticleSummaryExtractor())
        extractors.put(PostillonArticleSummaryExtractor::class.java, PostillonArticleSummaryExtractor())
    }


    fun getImplementedExtractors() = ArrayList<IImplementedArticleSummaryExtractor>(extractors.values)

}