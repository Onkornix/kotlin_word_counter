import java.nio.file.Path
import kotlin.random.Random

class Interact {
    private val worderer = WordererFull()
    private var groupedMap = worderer.giveGroupedMap()
    fun begin() {
        println("<<< Welcome to Interactive Mode! >>>")
        var interactiveMode = true

        while (interactiveMode) {
            print("> ")

            val args = readln().split(' ')

            when (handleCommands(args)) {
                Arg.EXIT -> interactiveMode = false
                else -> continue
            }
        }
        println("bye bye")
    }

    private fun handleCommands(args: List<String>): Arg {

        val command = when (args[0]) {
            "help" -> Arg.HELP
            "find" -> Arg.FIND
            "words" -> Arg.WORDS
            "input" -> Arg.INPUT
            "write" -> Arg.WRITE
            "index" -> Arg.INDEX
            "random" -> Arg.RANDOM
            "randat" -> Arg.RANDAT
            "exit" -> Arg.EXIT
            else -> Arg.HELP
        }

        when {
            command in listOf(Arg.FIND, Arg.WORDS, Arg.INPUT, Arg.RANDOM) && args.size < 2 -> {
                println("not enough arguments. use `help` to learn syntax")
                return Arg.ERROR
            }

            command in listOf(Arg.INDEX, Arg.RANDAT) && args.size < 3 -> {
                println("not enough arguments. use `help` to learn syntax")
                return Arg.ERROR
            }

            (command in listOf(Arg.INDEX, Arg.RANDAT, Arg.RANDOM) && args[1].toIntOrNull() == null)
                    || (command in listOf(Arg.INDEX, Arg.RANDAT) && args[2].toIntOrNull() == null) -> {
                println("one or more arguments passed are not numbers. use `help` to learn syntax")
                return Arg.ERROR
            }

            command == Arg.WORDS && args[1].toIntOrNull() == null -> {
                println("argument passed is not a number")
                return Arg.ERROR
            }
        }

        when (command) {
            Arg.HELP -> help()
            Arg.FIND -> println(find(args[1]))
            Arg.WORDS -> println(words(args[1].toInt()))
            Arg.RANDAT -> println(randAt(args[1].toInt(), args[2].toInt()))
            Arg.INDEX -> index(args[1].toInt() - 1, args[2].toInt() - 1)
            Arg.INPUT -> input(args[1])
            Arg.WRITE -> write()
            Arg.RANDOM -> random(args[1].toInt())
            Arg.EXIT -> return Arg.EXIT
            else -> Arg.ERROR
        }
        return Arg.ERROR
    }

    private fun help() {
        println(
            "Interactive commands are:\n" +
                    "help                     : Prints this help\n" +
                    "find [word]              : Prints given word's occurrence value\n" +
                    "words [value]            : Prints all words that occur [value] times (warning: may be a lot of words)\n" +
                    "index [start] [stop]     : Prints the [start] most common word to the [stop] most common word \n" +
                    "input [path/to/file]     : Runs the program on file given without exiting interactive mode\n" +
                    "write                    : Writes to output file without exiting interactive mode\n" +
                    "random [number]          : Randomly prints [number] words and their associated value\n" +
                    "randat [value] [number]  : Randomly prints [number] words at [value] occurrence. It may generate duplicates, but they will not be printed\n" +
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

        if (searchValue !in keys) {
            return nearbyWords(searchValue)
        }
        val wordsAtSearch = groupedMap.getValue(searchValue)
        if (wordsAtSearch.size > 100) {
            println("You are about to print \u001B[38;5;197m${wordsAtSearch.size}\u001B[0m words to your terminal. are you sure?\nType 'do it' to proceed: ")
            if (readln() == "do it") {
                if (wordsAtSearch.size > 10000) {
                    println("R.I.P.")
                    Thread.sleep(1000)
                }
                return wordsAtSearch.joinToString(", ")
            } else {
                return "Smart Choice"
            }
        }
        return wordsAtSearch.joinToString(", ")
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
        worderer.updateInputPath(pathToInput)
        worderer.beginWordering()
        groupedMap = worderer.giveGroupedMap()
        println("Success!")
    }

    private fun random(amount: Int){
        val randomValues = mutableListOf<Int>()
        val valuesThatExist = groupedMap.keys.toList()
        val max = valuesThatExist.size

        for (i in 0..amount - 1) {
            randomValues.run {
                val randomIndex = Random.nextInt(until = max)
                add(valuesThatExist[randomIndex])
            }
        }
        for (groupValue in randomValues) {
            val maxIndex = groupedMap.getValue(groupValue).size
            val randomIndex = Random.nextInt(until = maxIndex)
            val randomWord = groupedMap.getValue(groupValue)[randomIndex]

            println("$randomWord: $groupValue")
        }
    }

    private fun randAt(search: Int, amount: Int): String {
        if (search !in groupedMap.keys) {
            return nearbyWords(search)
        }
        val max = groupedMap.getValue(search).size
        val randomWordsAtSearch = mutableListOf<String>()
        for (i in 0..amount - 1) {

            val randomIndex = Random.nextInt(until = max)
            randomWordsAtSearch.add(groupedMap.getValue(search)[randomIndex])
        }
        return  randomWordsAtSearch.toSet().joinToString(", ")
        }

    private fun write() {
        worderer.writeToOutput()
    }
    private fun nearbyWords(searchValue: Int) : String{
        val keys = groupedMap.keys.toList()
        var start = 0
        var end: Int = keys.size - 1

        while (true) {
            val mid = (start + end) / 2

            if (searchValue > keys.last()) {
                return "No words found \nHighest value is ${keys.last()}"
            }
            if ((searchValue > keys[start] && searchValue < keys[end]) && end == start + 1) {
                return "No words found \nNearby values are: ${keys[start]} and ${keys[end]}"
            } else if (searchValue < keys[mid]) {
                end = mid
            } else {
                start = mid
            }
        }
    }
}
