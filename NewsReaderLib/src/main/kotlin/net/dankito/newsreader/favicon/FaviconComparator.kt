package net.dankito.newsreader.favicon

import net.dankito.newsreader.util.SimpleImageInfo
import net.dankito.newsreader.util.web.IWebClient
import net.dankito.newsreader.util.web.OkHttpWebClient
import net.dankito.newsreader.util.web.RequestParameters
import org.slf4j.LoggerFactory


class FaviconComparator {

    companion object {
        val DEFAULT_MIN_SIZE = 32

        private val log = LoggerFactory.getLogger(FaviconComparator::class.java)
    }


    private val webClient : IWebClient = OkHttpWebClient() // TODO: inject


    fun getBestIcon(favicons: List<Favicon>, minSize: Int = DEFAULT_MIN_SIZE, maxSize: Int? = null, returnSquarishOneIfPossible: Boolean = false) : Favicon? {
        val bestIcon : Favicon? = null

        // return icon with largest size
        favicons.filter { doesFitSize(it, minSize, maxSize, returnSquarishOneIfPossible) }.sortedByDescending { it.size }.firstOrNull()?.let {
            return it
        }

        // ok, sorting by known sizes didn't work, so retrieve sizes of icons which's size isn't known yet
        val faviconsWithUnknownSize = mutableListOf<Favicon>()
        favicons.filter { it.size == null }.forEach {
            it.size = retrieveIconSize(it)
            faviconsWithUnknownSize.add(it)
        }

        faviconsWithUnknownSize.filter { doesFitSize(it, minSize, maxSize, returnSquarishOneIfPossible) }.sortedByDescending { it.size }.firstOrNull()?.let {
            return it
        }

        if(returnSquarishOneIfPossible) { // then try without returnSquarishOneIfPossible
            favicons.filter { doesFitSize(it, minSize, maxSize, false) }.sortedByDescending { it.size }.firstOrNull()?.let {
                return it
            }
        }

        favicons.filter { it.size == null }.firstOrNull()?.let { return it }

        return bestIcon
    }

    fun doesFitSize(iconUrl: String, minSize: Int = DEFAULT_MIN_SIZE, maxSize: Int? = null, mustBeSquarish: Boolean = false): Boolean {
        retrieveIconSize(iconUrl)?.let {
            return doesFitSize(it, minSize, maxSize, mustBeSquarish)
        }

        return false
    }

    private fun doesFitSize(favicon: Favicon, minSize: Int, maxSize: Int? = null, mustBeSquarish: Boolean) : Boolean {
        favicon.size?.let { faviconSize ->
            return doesFitSize(faviconSize, minSize, maxSize, mustBeSquarish)
        }

        return false
    }

    private fun doesFitSize(faviconSize: Size, minSize: Int, maxSize: Int? = null, mustBeSquarish: Boolean) : Boolean {
        var result = hasMinSize(faviconSize, minSize)

        maxSize?.let { result = result.and(hasMaxSize(faviconSize, maxSize)) }

        if(mustBeSquarish) {
            result = result.and(faviconSize.isSquare())
        }

        return result
    }

    private fun hasMinSize(iconSize: Size?, minSize: Int = DEFAULT_MIN_SIZE): Boolean {
        if(iconSize != null) {
            return iconSize.width >= minSize && iconSize.height >= minSize
        }

        return false
    }

    private fun hasMaxSize(iconSize: Size?, maxSize: Int): Boolean {
        if(iconSize != null) {
            return iconSize.width <= maxSize && iconSize.height <= maxSize
        }

        return false
    }

    private fun retrieveIconSize(favicon: Favicon) : Size? {
        return retrieveIconSize(favicon.url)
    }

    private fun retrieveIconSize(iconUrl: String) : Size? {
        try {
            val downloadedBytes = mutableListOf<Byte>()

            val parameters = RequestParameters(iconUrl)
            parameters.setHasStringResponse(false)
            parameters.setDownloadProgressListener { _, downloadedChunk -> downloadedBytes.addAll(downloadedChunk.toList()) }

            val response = webClient.get(parameters)
            if (response != null && response.isSuccessful) {
                val imageInfo = SimpleImageInfo(downloadedBytes.toByteArray())
                return Size(imageInfo.width, imageInfo.height)
            }
        } catch(e: Exception) { log.error("Could not retrieve icon size for url $iconUrl", e) }

        return null
    }
}