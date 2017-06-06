package net.dankito.newsreader


data class AsyncResult<T>(val successful : Boolean, val error : Exception? = null, val result : T? = null) {
}