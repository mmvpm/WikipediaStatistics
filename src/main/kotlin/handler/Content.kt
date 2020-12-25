package com.stroganovns.wiki.handler

class Content {

    private var received = 0

    private val data = mutableMapOf(
        "title"     to StringBuilder(),
        "text"      to StringBuilder(),
        "bytes"     to StringBuilder(),
        "timestamp" to StringBuilder()
    )

    fun getValue(name: String): String {
        return data[name]?.toString() ?: error("Content bad request: $name")
    }

    fun addValue(name: String, ch: CharArray) {
        data[name]?.let { builder ->
            if (builder.isEmpty()) {
                received += 1
            }
            builder.append(ch)
        }
    }

    fun isDone(): Boolean {
        return received == data.size
    }
}