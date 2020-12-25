package com.stroganovns.wiki

import java.io.*
import javax.xml.parsers.SAXParserFactory
import com.stroganovns.wiki.handler.SaxHandler

class StatisticsService {

    private val tempDirectory = "temp_data"

    fun collectStatistics(inputs: List<String>, output: String, nThreads: Int) {
        println("Unpacking archive(s)...")
        val outputFiles = unarchive(inputs)

        println("Parsing xml-file(s)...")
        val statistics = parse(outputFiles, nThreads)

        println("Printing results...")
        printResults(output, statistics)
    }

    private fun unarchive(inputs: List<String>): List<File> {
        createTempDirectory()

        val inputFiles = inputs.map { File(it) }
        inputFiles.all { file ->
            file.exists() && file.isFile && file.canRead()
        }
        val outputFiles = List(inputs.size) { index ->
            File("$tempDirectory/$index.xml")
        }

        val archiver = BZip2Archiver()
        for (index in inputs.indices) {
            archiver.unarchive(inputFiles[index], outputFiles[index])
        }
        return outputFiles
    }

    private fun parse(outputFiles: List<File>, nThreads: Int): String {
        val handler = SaxHandler(nThreads)
        val parser = SAXParserFactory.newInstance().newSAXParser()
        for (outputFile in outputFiles) {
            parser.parse(outputFile, handler)
        }
        handler.shutdown()
        return handler.collectStatistics()
    }

    private fun printResults(output: String, statistics: String) {
        val outputStream = File(output).outputStream()
        outputStream.write(statistics.toByteArray())
        outputStream.close()
        deleteTempDirectory()
    }

    private fun createTempDirectory() {
        val root = File(tempDirectory)
        if (!root.exists()) {
            root.mkdirs()
        }
    }

    private fun deleteTempDirectory() {
        File(tempDirectory).deleteRecursively()
    }

}