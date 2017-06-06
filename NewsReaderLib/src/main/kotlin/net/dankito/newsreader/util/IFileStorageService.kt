package net.dankito.newsreader.util


interface IFileStorageService {

    @Throws(Exception::class)
    fun readFromTextFile(filename: String): String?

    @Throws(Exception::class)
    fun readFromBinaryFile(filename: String): ByteArray?


    @Throws(Exception::class)
    fun writeToTextFile(fileContent: String, filename: String)

    @Throws(Exception::class)
    fun writeToBinaryFile(fileContent: ByteArray, filename: String)


    fun deleteFolderRecursively(path: String)

}