package com.stroganovns.wiki.listeners

fun getFullRange(list: Iterable<Int>): IntRange {
    val minIndex = list.minOrNull() ?: return IntRange.EMPTY
    val maxIndex = list.maxOrNull() ?: return IntRange.EMPTY
    return minIndex..maxIndex
}