package net.dankito.newsreader.util

import java.io.*


open class JavaFileStorageService : IFileStorageService {

    @Throws(Exception::class)
    override fun writeToTextFile(fileContent: String, filename: String) {
        val outputStreamWriter = OutputStreamWriter(createFileOutputStream(filename))

        outputStreamWriter.write(fileContent)

        outputStreamWriter.flush()
        outputStreamWriter.close()
    }

    @Throws(Exception::class)
    override fun writeToBinaryFile(fileContent: ByteArray, filename: String) {
        val outputStream = createFileOutputStream(filename)

        outputStream.write(fileContent)

        outputStream.flush()
        outputStream.close()
    }

    @Throws(FileNotFoundException::class)
    protected open fun createFileOutputStream(filename: String): OutputStream {
        return FileOutputStream(filename)
    }


    @Throws(Exception::class)
    override fun readFromTextFile(filename: String): String? {
        val inputStream = createFileInputStream(filename)

        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            var fileContent = bufferedReader.use { it.readLines() }.joinToString(separator = "") { it }

            bufferedReader.close()
            inputStream.close()

            return fileContent
        }

        return null
    }

    @Throws(Exception::class)
    override fun readFromBinaryFile(filename: String): ByteArray? {
        val inputStream = createFileInputStream(filename)

        if (inputStream != null) {
            val buffer = ByteArrayOutputStream()

            var nRead: Int = 0
            val data = ByteArray(16384)

            while (nRead > -1) {
                nRead = inputStream.read(data, 0, data.size)
                buffer.write(data, 0, nRead)
            }

            buffer.flush()
            inputStream.close()

            return buffer.toByteArray()
        }

        return null
    }

    @Throws(FileNotFoundException::class)
    protected open fun createFileInputStream(filename: String): InputStream? {
        return FileInputStream(filename)
    }


    override fun deleteFolderRecursively(path: String) {
        deleteRecursively(File(path))
    }

    protected fun deleteRecursively(file: File) {
        if (file.isDirectory) {
            for (containingFile in file.listFiles()!!) {
                deleteRecursively(containingFile)
            }
        }

        file.delete()
    }

}