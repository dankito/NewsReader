package net.dankito.newsreader.favicon

import net.dankito.newsreader.util.SimpleImageInfo
import net.dankito.newsreader.util.web.IWebClient
import net.dankito.newsreader.util.web.OkHttpWebClient
import net.dankito.newsreader.util.web.RequestParameters


class FaviconSorter {

    private val webClient : IWebClient = OkHttpWebClient() // TODO: inject


    fun getBestIcon(favicons: List<Favicon>) : Favicon? {
        var bestIcon : Favicon? = null

        // return icon with largest size
        favicons.filter { it.size != null }.sortedByDescending { it.size }.firstOrNull()?.let {
            if (hasMinSize(it.size)) {
                return it
            }
        }

        val faviconsWithUnknownSize = mutableListOf<Favicon>()
        favicons.filter { it.size == null }.forEach {
            it.size = retrieveIconSize(it)
            faviconsWithUnknownSize.add(it)
        }

        faviconsWithUnknownSize.sortedByDescending { it.size }.firstOrNull()?.let {
            if (hasMinSize(it.size)) {
                return it
            }
        }

        favicons.filter { it.size == null }.firstOrNull()?.let { return it }

        return bestIcon
    }

    fun hasMinSize(iconUrl: String): Boolean {
        retrieveIconSize(iconUrl)?.let {
            return hasMinSize(it)
        }

        return false
    }

    private fun hasMinSize(iconSize: Size?): Boolean {
        if(iconSize != null) {
            return iconSize.width > 16 && iconSize.height > 16
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
            parameters.setDownloadProgressListener { progress, downloadedChunk -> downloadedBytes.addAll(downloadedChunk.toList()) }

            val response = webClient.get(parameters)
            if (response != null && response.isSuccessful) {
                val imageInfo = SimpleImageInfo(downloadedBytes.toByteArray())
                return Size(imageInfo.width, imageInfo.height)
            }
        } catch(e: Exception) { } // TODO: log

        return null
    }
}