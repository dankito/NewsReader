package net.dankito.newsreader.article

import org.junit.Test

class PostillonArticleExtractorTest : ArticleExtractorTestBase() {

    override fun createArticleExtractor(): IArticleExtractor {
        return PostillonArticleExtractor()
    }


    @Test
    fun extractSicheresHerkunftslandArticle() {
        getAndTestArticle("http://www.der-postillon.com/2017/06/sicheres-herkunftsland.html",
                "Sicheres Herkunftsland: De Maizière verbringt Sommerurlaub in Afghanistan",
                null,
                "https://3.bp.blogspot.com/-2gQ8ePjWfyA/WS_1d8WGKQI/AAAAAAAAo-c/lNQCiBTIS98h7jkVWhfsFwwV5Cqn1TkygCLcB/s1600/Urlaub.jpg")
    }

    @Test
    fun extractSonntagsfrageArticle() {
        getAndTestArticle("http://www.der-postillon.com/2017/05/sonntagsfrage-obama-kirchentag.html",
                "Sonntagsfrage: Was sagen Sie zu Obamas Auftritt am evangelischen Kirchentag?",
                null)
    }

}