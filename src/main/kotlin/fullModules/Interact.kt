package fullModules

import java.io.BufferedReader
import java.io.File
import java.nio.file.Path
import kotlin.random.Random
enum class ArgType {
    HELP,
    FIND,
    WORDS,
    INPUT,
    WRITE,
    INDEX,
    EXIT,
    RANDOM
}
class Interact {
    private val worderer = WordererFull()
    private var groupedMap = worderer.giveGroupedMap()
    fun begin() {

        var interactiveMode = true

        while (interactiveMode) {
            print("> ")


            val args = readln().split(' ')

            val command = when (args[0]) {
                "help" -> ArgType.HELP
                "find" -> ArgType.FIND
                "words" -> ArgType.WORDS
                "input" -> ArgType.INPUT
                "write" -> ArgType.WRITE
                "index" -> ArgType.INDEX
                "random" -> ArgType.RANDOM
                "exit" -> ArgType.EXIT
                else -> ArgType.HELP
            }

            when {
                command in listOf(ArgType.FIND, ArgType.WORDS, ArgType.INPUT) && args.size < 2 -> {
                    println("not enough arguments. use `help` to learn syntax")
                    continue
                }

                command == ArgType.INDEX && args.size < 3 -> {
                    println("not enough arguments. use `help` to learn syntax")
                    continue
                }

                command == ArgType.INDEX && (args[1].toIntOrNull() == null || args[2].toIntOrNull() == null) -> {
                    println("one or more arguments passed are not numbers. use `help` to learn syntax")
                    continue
                }

                command == ArgType.WORDS && args[1].toIntOrNull() == null -> {
                    println("argument passed is not a number")
                    continue
                }
            }

            when (command) {
                ArgType.HELP -> help()
                ArgType.FIND -> println(find(args[1]))
                ArgType.WORDS -> println(words(args[1].toInt()))
                ArgType.INDEX -> index(args[1].toInt() - 1, args[2].toInt() - 1)
                ArgType.INPUT -> input(args[1])
                ArgType.WRITE -> write()
                ArgType.RANDOM -> random(args[1].toInt())
                ArgType.EXIT -> interactiveMode = false
            }
        }
        println("bye bye")
    }

    private fun checkCommand() {

    }

    private fun help() {
        println(
            "Interactive commands are:\n" +
                    "help                     : Prints this help\n" +
                    "find [word]              : Prints given word's occurrence value\n" +
                    "words [value]            : Prints all words that occur [value] amount of times (warning: may be a lot of words)\n" +
                    "index [start] [stop]     : Prints the [start] most common word to the [stop] most common word \n" +
                    "input [path/to/file]     : Runs the program on file given without exiting interactive mode\n" +
                    "write                    : Writes to output file without exiting interactive mode\n" +
                    "random [number]          : Randomly prints [number] words and their associated value" +
                    "exit                     : Exits interactive mode and does not write to output file"
        )

    }

    private fun find(word: String): String {
        for (occurrence in groupedMap.keys) {
            if (word in groupedMap.getValue(occurrence)) {
                return occurrence.toString()
            } else {
                continue
            }
        }
        return "Word not found"
    }

    private fun words(searchValue: Int): String {
        val keys = groupedMap.keys.toList()
        for (occurrence in keys) {
            if (occurrence == searchValue) {
                val wordsAtSearch = groupedMap.getValue(occurrence)
                if (wordsAtSearch.size > 100) {
                    println("You are about to print ${wordsAtSearch.size} words to your terminal. are you sure?\nType 'do it' to proceed: ")
                    if (readln() == "do it") {
                        return wordsAtSearch.joinToString(", ")
                    }
                }
                return wordsAtSearch.joinToString(", ")
            }
        }
        /*
        Scuffed binary sort to find the two closest values if given value is invalid.
         */
        var start = 0
        var end = keys.size - 1

        while (true) {
            val mid = (start + end) / 2

            if (searchValue > keys.last()) return "No words found \nHighest value is ${keys.last()}"
            if ((searchValue > keys[start] && searchValue < keys[end]) && end == start + 1) {
                return "No words found \nNearby values are: ${keys[start]} and ${keys[end]}"
            } else if (searchValue < keys[mid]) {
                end = mid
            } else {
                start = mid
            }
        }
    }

    private fun index(start: Int, stop: Int) {
        val allValues = mutableListOf<Int>().run {
            this.add(0)
            for (v in groupedMap.keys) this.add(v)
            this
        }

        when {
            start !in allValues || stop !in allValues -> {
                println("start or stop index not a valid index (max: ${allValues.size})")
                return
            }

            start > stop -> {
                println("'start' index cannot be greater than 'stop' index")
            }
        }

        val startAdj = allValues.size - 2 - stop
        val stopAdj = allValues.size - 1 - start

        for (occ in groupedMap.keys.reversed()) {
            if (groupedMap.keys.indexOf(occ) in startAdj..stopAdj) {
                println("$occ: ${groupedMap.getValue(occ).joinToString(", ")}")
            }
        }
    }

    private fun input(pathToInput: String) {
        if (!worderer.checkInputErrors(Path.of(pathToInput))) {
            return
        }
        worderer.reset()
        worderer.beginWordering(BufferedReader(File(pathToInput).reader()))
        groupedMap = worderer.giveGroupedMap()
        println("Success!")
    }

    private fun random(amount: Int) {
        return
//        for (i in 0..amount) {
//            val valuesThatExist = groupedMap.keys.toList()
//            val max = valuesThatExist.last()
//            val min = valuesThatExist[0]
//            Random.nextInt()
//        }
    }

    private fun write() {
        worderer.writeToOutput()
    }
}
