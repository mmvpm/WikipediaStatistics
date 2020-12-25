package com.stroganovns.wiki.handler

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

class SaxHandler(nThreads: Int = 1) : DefaultHandler() {

    private var currentContent = Content()
    private val currentPath = mutableListOf<String>()
    private val eventManager = EventManager(nThreads)

    private val pageTag = "page"
    private val relevantTags = mapOf(
        "title"     to listOf("page", "title"),
        "text"      to listOf("page", "revision", "text"),
        "timestamp" to listOf("page", "revision", "timestamp"),
    )
    private val relevantAttributes = listOf(
        "bytes"     to listOf("page", "revision", "text"),
    )

    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        if (qName == null) {
            return
        }
        currentPath.add(qName)
        relevantAttributes.forEach { (attribute, path) ->
            if (currentPath.takeLast(path.size) == path) {
                val data = attributes?.getValue(attribute)
                if (data != null) {
                    currentContent.addValue(attribute, data.toCharArray())
                    if (data == "0") {
                        currentContent.addValue(path.last(), CharArray(0))
                    }
                } else {
                    resetContent()
                    return
                }
            }
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        if (qName == pageTag) {
            if (currentContent.isDone()) {
                eventManager.newContent(currentContent)
            }
            resetContent()
        }
        if (currentPath.isNotEmpty()) {
            currentPath.removeLast()
        }
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        val currentTag = currentPath.lastOrNull()
        relevantTags[currentTag]?.let { path ->
            if (currentPath.takeLast(path.size) == path) {
                val range = start until start + length
                currentContent.addValue(currentTag!!, ch.sliceArray(range))
            }
        }
    }

    fun shutdown() = eventManager.shutdown()

    fun collectStatistics(): String {
        val tagInfo = listOf(
            "title"     to "Топ-300 слов в заголовках статей:",
            "text"      to "Топ-300 слов в статьях:",
            "bytes"     to "Распределение статей по размеру:",
            "timestamp" to "Распределение статей по времени:",
        )
        val result = eventManager.collectStatistics()
        val lineBreak = "\n"
        return (tagInfo.joinToString(lineBreak) { (name, info) ->
            info + lineBreak +
            result[name]?.joinToString("") { (index, value) ->
                "$index $value$lineBreak"
            }
        })
    }

    private fun resetContent() {
        currentContent = Content()
        currentPath.clear()
    }
}