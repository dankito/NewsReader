package net.dankito.newsreader.article

import net.dankito.newsreader.model.ArticleSummaryItem


class ArticleExtractors {

    private val defaultExtractor = DefaultArticleExtractor()

    private val implementedExtractors = LinkedHashMap<Class<out IArticleExtractor>, IArticleExtractor>()

    init {
        implementedExtractors.put(HeiseNewsAndDeveloperArticleExtractor::class.java, HeiseNewsAndDeveloperArticleExtractor())
        implementedExtractors.put(GuardianArticleExtractor::class.java, GuardianArticleExtractor())
        implementedExtractors.put(PostillonArticleExtractor::class.java, PostillonArticleExtractor())
    }


    fun getExtractorForItem(item: ArticleSummaryItem) : IArticleExtractor? {
        item.articleExtractorClass?.let { return getExtractorForClass(it) }

        return getExtractorForUrl(item.url)
    }

    fun getExtractorForUrl(url: String) : IArticleExtractor? {
        // TODO: implement
        return defaultExtractor
    }

    fun getExtractorForClass(extractorClass: Class<out IArticleExtractor>) = implementedExtractors[extractorClass]

}