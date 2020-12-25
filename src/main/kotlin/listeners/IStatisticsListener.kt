package com.stroganovns.wiki.listeners

interface IStatisticsListener {
    fun listen(data: String)
    fun collectStatistics(): List<Pair<String, String>>
}