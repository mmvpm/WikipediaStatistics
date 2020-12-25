package com.stroganovns.wiki.listeners

import java.util.concurrent.ConcurrentHashMap

class SizeListener : IStatisticsListener {

    private val storage = ConcurrentHashMap<Int, Int>()

    override fun listen(data: String) {
        var index = data.toIntOrNull() ?: run {
            println("Warning: Wrong bytes size in test block in *.bz2.")
            return
        }
        index = index.toString().lastIndex
        storage.compute(index) { _, value ->
            value?.plus(1) ?: 1
        }
    }

    override fun collectStatistics(): List<Pair<String, String>> =
        getFullRange(storage.keys).map { index ->
            Pair("$index", "${storage[index] ?: 0}")
        }
}