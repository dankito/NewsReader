package net.dankito.newsreader.favicon

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class FaviconExtractorTest {

    private val underTest : FaviconExtractor = FaviconExtractor();


    @Test
    fun extractWikipediaFavicons() {
        var extractedIcons = getFaviconsForUrl("https://www.wikipedia.org/")


        testExtractedFavicons(extractedIcons, 2)
    }

    @Test
    fun extractTheGuardianFavicons() {
        var extractedIcons = getFaviconsForUrl("https://www.theguardian.com")


        testExtractedFavicons(extractedIcons, 9)
    }

    @Test
    fun extractNewYorkTimesFavicons() {
        var extractedIcons = getFaviconsForUrl("https://www.nytimes.com/")


        testExtractedFavicons(extractedIcons, 5)
    }

    @Test
    fun extractZeitFavicons() {
        var extractedIcons = getFaviconsForUrl("http://www.zeit.de/")


        testExtractedFavicons(extractedIcons, 3)
    }

    @Test
    fun extractHeiseFavicons() {
        var extractedIcons = getFaviconsForUrl("https://www.heise.de")


        testExtractedFavicons(extractedIcons, 6)
    }

    @Test
    fun extractDerPostillonFavicons() {
        var extractedIcons = getFaviconsForUrl("http://www.der-postillon.com")


        testExtractedFavicons(extractedIcons, 2)
    }


    private fun getFaviconsForUrl(url: String): MutableList<Favicon> {
        var extractedIcons = mutableListOf<Favicon>()
        var countDownLatch = CountDownLatch(1)

        underTest.extractFaviconsAsync(url) {
            it.result?.let { extractedIcons.addAll(it) }

            countDownLatch.countDown()
        }

        countDownLatch.await(20, TimeUnit.SECONDS)
        return extractedIcons
    }

    private fun testExtractedFavicons(extractedIcons: MutableList<Favicon>, countIconsToBe: Int) {
        assertThat(extractedIcons.size, `is`(countIconsToBe))

        for (favicon in extractedIcons) {
            assertThat(favicon.url, notNullValue())
            assertThat(favicon.url.startsWith("http"), `is`(true))
        }
    }

}