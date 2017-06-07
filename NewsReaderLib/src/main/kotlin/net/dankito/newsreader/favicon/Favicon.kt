package net.dankito.newsreader.favicon


data class Favicon(val url : String, val iconType : FaviconType, var size : Size? = null, val type : String? = null) {

}