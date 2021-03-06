package net.dankito.newsreader.favicon


data class Size(val width: Int, val height: Int) : Comparable<Size> {

    override fun compareTo(other: Size): Int {
        if(width == other.width) {
            return height.compareTo(other.height)
        }

        return width.compareTo(other.width)
    }


    fun isSquare(): Boolean {
        return width == height
    }

}