package com.stroganovns.wiki

import java.io.*
import javax.xml.parsers.SAXParserFactory
import com.stroganovns.wiki.handler.SaxHandler

class StatisticsService {

    private val tempDirectory = "temp_data"

    fun collectStatistics(inputs: List<String>, output: String, nThreads: Int) {
        createTempDirectory()
        println("Unpacking...")
        val outputFiles = unarchive(inputs)

        println("Parsing...")
        val handler = SaxHandler(nThreads)
        val parser = SAXParserFactory.newInstance().newSAXParser()
        for (outputFile in outputFiles) {
            parser.parse(outputFile, handler)
        }
        handler.shutdown()

        val outputStream = File(output).outputStream()
        outputStream.write(handler.collectStatistics().toByteArray())
        outputStream.close()
        deleteTempDirectory()
    }

    private fun unarchive(inputs: List<String>): List<File> {
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