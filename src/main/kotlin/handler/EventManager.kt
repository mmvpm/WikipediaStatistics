package com.stroganovns.wiki.handler

import java.util.concurrent.TimeUnit
import java.util.concurrent.Executors
import com.stroganovns.wiki.listeners.*

class EventManager(nThreads: Int = 1) {

    private val timeout: Long = 1
    private val timeUnit = TimeUnit.MINUTES
    private val threadPool = Executors.newFixedThreadPool(nThreads)

    private val listeners = mapOf(
        "title"     to FrequencyListener(),
        "text"      to FrequencyListener(),
        "bytes"     to SizeListener(),
        "timestamp" to YearListener(),
    )

    fun newContent(content: Content) {
        for ((name, listener) in listeners) {
            threadPool.submit {
                listener.listen(content.getValue(name))
            }
        }
    }

    fun shutdown() {
        threadPool.shutdown()
        var done = false
        while (!done) {
            done = threadPool.awaitTermination(timeout, timeUnit)
        }
    }

    fun collectStatistics(): Map<String, List<Pair<String, String>>> =
        listeners.mapValues { (_, listener) ->
            listener.collectStatistics()
        }
}