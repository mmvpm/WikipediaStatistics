package com.stroganovns.wiki.listeners

import java.util.concurrent.ConcurrentHashMap

class YearListener : IStatisticsListener {

    private val yearLength = 4
    private val storage = ConcurrentHashMap<Int, Int>()

    override fun listen(data: String) {
        val year = data.take(yearLength).toIntOrNull() ?: run {
            println("Warning: Wrong formed date in timestamp block in *.bz2.")
            return
        }
        storage.compute(year) { _, value ->
            value?.plus(1) ?: 1
        }
    }

    override fun collectStatistics(): List<Pair<String, String>> =
        getFullRange(storage.keys).map { year ->
            Pair("$year", "${storage[year] ?: 0}")
        }
}