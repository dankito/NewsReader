package net.dankito.newsreader.article

import org.junit.Test

class GuardianArticleExtractorTest : ArticleExtractorTestBase() {

    override fun createArticleExtractor(): IArticleExtractor {
        return GuardianArticleExtractor()
    }


    @Test
    fun extractBilderbergTrumpArticle() {
        getAndTestArticle("https://www.theguardian.com/us-news/2017/jun/01/bilderberg-trump-administration-secret-meeting",
                "Bilderberg 2017: secret meeting of global leaders could prove a problem for Trump",
                "The annual gathering of government and industry elites will include a ‘progress report’ on the Trump administration. Will it get a passing grade?")
    }

}