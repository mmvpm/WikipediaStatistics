package com.stroganovns.wiki

import kotlin.time.measureTime

fun getArgument(args: Array<String>, argName: String): List<String> {
    val argument = args.find {
        it.take(argName.length) == argName
    } ?: error("No such argument: $argName")

    return argument.drop(argName.length).filter { ch ->
        ch !in listOf(' ', '=')
    }.split(",")
}

fun main(args: Array<String>) {
    try {
        val inputs = getArgument(args, "--inputs")
        val output = getArgument(args, "--output").first()
        val threads = getArgument(args, "--threads").first().toIntOrNull()
            ?: error("Number of threads must be an integer")
        if (threads !in 1..64) {
            error("Number of threads must be in 1..64")
        }

        val duration = measureTime {
            StatisticsService().collectStatistics(inputs, output, threads)
        }
        println("Time: ${duration.inMilliseconds} ms")

    } catch (e: Exception) {
        println("Error! ${e.message}")
        throw e
    }
}