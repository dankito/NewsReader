package net.dankito.newsreader.rss

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class FeedAddressExtractorTest {

    private val underTest : FeedAddressExtractor = FeedAddressExtractor();


    @Test
    fun extractWikipediaFeedAddresses() {
        var extractedFeedAddresses = getFeedAddressesForUrl("https://en.wikipedia.org/")

        testExtractedFeedAddresses(extractedFeedAddresses, 3)
    }

    @Test
    fun extractGuardianFeedAddresses() {
        var extractedFeedAddresses = getFeedAddressesForUrl("https://www.theguardian.com/international")

        testExtractedFeedAddresses(extractedFeedAddresses, 1)
    }

    @Test
    fun extractNewYorkTimesFeedAddresses() {
        var extractedFeedAddresses = getFeedAddressesForUrl("https://www.nytimes.com/")

        testExtractedFeedAddresses(extractedFeedAddresses, 1)
    }

    @Test
    fun extractHeiseFeedAddresses() {
        var extractedFeedAddresses = getFeedAddressesForUrl("https://www.heise.de/")

        testExtractedFeedAddresses(extractedFeedAddresses, 2)
    }

    @Test
    fun extractPostillonFeedAddresses() {
        var extractedFeedAddresses = getFeedAddressesForUrl("http://www.der-postillon.com/")

        testExtractedFeedAddresses(extractedFeedAddresses, 2)
    }


    private fun getFeedAddressesForUrl(url: String): MutableList<FeedAddress> {
        var extractedFeedAddresses = mutableListOf<FeedAddress>()
        var countDownLatch = CountDownLatch(1)

        underTest.extractFeedAddressesAsync(url) {
            it.result?.let { extractedFeedAddresses.addAll(it) }

            countDownLatch.countDown()
        }

        countDownLatch.await(20, TimeUnit.SECONDS)
        return extractedFeedAddresses
    }

    private fun testExtractedFeedAddresses(extractedFeedAddresses: MutableList<FeedAddress>, countFeedsToBe: Int) {
        Assert.assertThat(extractedFeedAddresses.size, `is`(countFeedsToBe))

        for (feedAddress in extractedFeedAddresses) {
            Assert.assertThat(feedAddress.url, notNullValue())
            Assert.assertThat(feedAddress.url.startsWith("http"), `is`(true))
            Assert.assertThat(feedAddress.title.isNullOrBlank(), `is`(false))
        }
    }

}