package net.dankito.newsreader.favicon


data class Favicon(val url : String, val iconType : FaviconType, var size : Size? = null, val type : String? = null) {

    fun setSizeFromString(sizeString: String) {
        val parts = sizeString.split("x")
        if(parts.size == 2) {
            this.size = Size(parts[0].toInt(), parts[1].toInt())
        }
    }

}