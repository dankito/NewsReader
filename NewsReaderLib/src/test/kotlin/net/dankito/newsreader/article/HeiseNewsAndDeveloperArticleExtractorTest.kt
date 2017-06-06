package net.dankito.newsreader.article

import org.junit.Test

class HeiseNewsAndDeveloperArticleExtractorTest : ArticleExtractorTestBase() {

    override fun createArticleExtractor(): IArticleExtractor {
        return HeiseNewsAndDeveloperArticleExtractor()
    }


    @Test
    fun extractKotlinFuerAndroidArticle() {
        getAndTestArticle("https://www.heise.de/developer/meldung/Kommentar-Kotlin-fuer-Android-Googles-fremde-Lorbeeren-3717940.html",
                "Kommentar: Kotlin für Android – Googles fremde Lorbeeren",
                "Nach dem Wechsel von Eclipse zu IntellJ IDEA mag man die Kotlin-Unterstützung in Android als weiteren Ritterschlag verstehen. Doch der Geadelte ist hier nicht das Projekt Kotlin, sondern Google selbst – sagt Technologieexperte Benjamin Schmid.")
    }

}