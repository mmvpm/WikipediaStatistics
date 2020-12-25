package com.stroganovns.wiki

import java.io.*
import java.nio.file.Paths
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream

class AppTest {
    companion object {
        private const val tempDirectory = "temp_test_data"
        private const val testData = "src/test/resources/test_data"

        @BeforeAll
        @JvmStatic
        fun compressFiles() {
            val testRoot = File(tempDirectory)
            if (!testRoot.exists()) {
                testRoot.mkdirs()
            }
            File(testData).listFiles().orEmpty().filter { file ->
                file.extension == "xml"
            }.forEach { file ->
                createTemporaryBzip2(file)
            }
            File("invalid".toInputs()).writeText("invalid текст")
        }

        @AfterAll
        @JvmStatic
        fun deleteTempFiles() {
            File(tempDirectory).deleteRecursively()
        }

        private fun String.toInputs(): String =
            this.split(',').joinToString(",") {
                Paths.get(tempDirectory).resolve("$it.bz2").toString()
            }

        private fun createTemporaryBzip2(file: File) {
            val input = file.inputStream()
            input.use {
               BZip2CompressorOutputStream(
                    FileOutputStream(file.name.toInputs())
               ).use { input.copyTo(it) }
            }
        }
    }

    @Test
    fun `good xml`() =
        testInputs("simple.xml", threads = 1)

    @Test
    fun `not well formed xml`() {
        assertThrows<Throwable> {
            testInputs("not-well-formed.xml", threads = 1)
        }
    }

    @Test
    fun `some missed tags`() =
        testInputs("missed-tags.xml", threads = 1)

    @Test
    fun `no pages`() =
        testInputs("no-pages.xml", threads = 1)

    @Test
    fun `wrong nesting of tags`() =
        testInputs("wrong-nesting.xml", threads = 1)

    @Test
    fun `incorrect archive`() {
        assertThrows<Throwable> {
            testInputs("invalid", threads = 1)
        }
    }

    @Test
    fun `incorrect input`() {
        assertThrows<Throwable> {
            testInputs("nonexistent", threads = 1)
        }
    }

    @Test
    fun `multiple inputs`() =
        testInputs("simple.xml,second.xml", threads = 1)

    @Test
    fun `big xml`() =
        testInputs("big.xml", threads = 1)

    @Test
    fun `big xml four threads`() =
        testInputs("big.xml", threads = 4)


    private fun testInputs(xmlInputs: String, threads: Int) {
        val outputPrefix = xmlInputs.replace(",", "__")
        val outputFileName = "$outputPrefix.actual.txt"

        val args = arrayOf(
            "--threads = $threads",
            "--inputs = ${xmlInputs.toInputs()}",
            "--output = ${outputFileName.relativeToTemporaryDir()}"
        )
        main(args)
        val expectedFileName = "$outputPrefix.expected.txt"
        assertFilesHaveSameContent(expectedFileName, outputFileName)
    }

    private fun assertFilesHaveSameContent(
        expectedFileName: String,
        actualFileName: String,
        message: String? = null
    ) {
        assertEquals(
            Paths.get(testData).resolve(expectedFileName).toFile().readText(),
            Paths.get(tempDirectory).resolve(actualFileName).toFile().readText(),
            message
        )
    }

    private fun String.relativeToTemporaryDir(): String
        = Paths.get(tempDirectory).resolve(this).toString()
}