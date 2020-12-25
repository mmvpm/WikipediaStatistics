package com.stroganovns.wiki.listeners

import java.util.concurrent.ConcurrentHashMap

class FrequencyListener : IStatisticsListener {

    private val takeFirst = 300
    private val regex = """[а-я]{3,}""".toRegex()
    private val storage = ConcurrentHashMap<String, Int>()

    override fun listen(data: String) =
        regex.findAll(data.toLowerCase()).forEach { result ->
            val word = result.value
            storage.compute(word) { _, value ->
                value?.plus(1) ?: 1
            }
        }

    override fun collectStatistics(): List<Pair<String, String>> =
        storage.toList().sortedWith(compareByDescending<Pair<String, Int>> { (_, count) ->
            count
        }.thenBy { (name, _) ->
            name
        }).take(takeFirst).map { (name, count) ->
            Pair("$count", name)
        }

}