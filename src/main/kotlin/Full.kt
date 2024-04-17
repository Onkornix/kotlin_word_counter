import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class WordererFull {
    private val ungroupedMap = mutableMapOf<String,Int>()
    val groupedMap = mutableMapOf<Int,MutableList<String>>()
    private var inputAndOutputPaths = listOf<String>()
    private var inputReader: BufferedReader
    private var outputWriter: BufferedWriter

    init {
        print("complete paths to input and then output files (separated by a space): ")

        inputAndOutputPaths = readln().split(' ')
        while (!checkInputErrors(Path.of(inputAndOutputPaths[0]))) {
            print("correct file path: ")
            inputAndOutputPaths = listOf(readln(), inputAndOutputPaths[1])
        }
        inputReader = BufferedReader(File(inputAndOutputPaths[0]).reader())
        outputWriter = BufferedWriter(File(inputAndOutputPaths[1]).writer())

        beginWordering(inputReader)
    }

    private fun addToUngroupedMap(line: Iterator<String>) {
        for (word in line) {
            if (word.isBlank()) continue
            if (word in ungroupedMap.keys) {
                ungroupedMap[word] = (ungroupedMap.getValue(word) + 1)
            }
            ungroupedMap.putIfAbsent(word, 1)
        }
    }

    private fun createGroupedMap() {
        val valuesThatExist = run {
            val valuesThatExist: MutableList<Int> = mutableListOf()
            for (number in ungroupedMap.values.sorted()) {
                if (number !in valuesThatExist) {
                    valuesThatExist.add(number)
                }
            }
            valuesThatExist
        }

        var currentOccurrenceValue = ungroupedMap.values.max() - 1
        var index = valuesThatExist.size

        while (currentOccurrenceValue >= ungroupedMap.values.min()) {
            val wordsInGroup: MutableList<String> = mutableListOf()

            for (word in ungroupedMap.keys) {
                if (currentOccurrenceValue in valuesThatExist
                    && ungroupedMap.getValue(word) == currentOccurrenceValue) {
                    wordsInGroup.add(word)
                }
            }

            if (wordsInGroup.isNotEmpty()) {
                groupedMap[currentOccurrenceValue] = wordsInGroup
            }

            if (index == 0) break
            index--
            currentOccurrenceValue = valuesThatExist[index]

            val forRemoval: MutableList<String> = mutableListOf()
            for (word in ungroupedMap.keys) {
                if (ungroupedMap.getValue(word) > currentOccurrenceValue) {
                    forRemoval.add(word)
                }
            }
            for (word in forRemoval) {
                ungroupedMap.remove(word)
            }
        }
    }

    fun writeToOutput() {
        if (File(inputAndOutputPaths[1]).exists()) File(inputAndOutputPaths[1]).delete()
        File(inputAndOutputPaths[1]).createNewFile()


        val totalWordCount = run {
            var count = 0
            for (occurrence in groupedMap.keys) {
                count += (groupedMap.getValue(occurrence).size * occurrence)
            }
            count
        }

        val totalUniqueWords = run {
            var count = 0
            for (key in groupedMap.keys) {
                count += groupedMap.getValue(key).size
            }
            count
        }

        outputWriter.write("Total Words: $totalWordCount")
        outputWriter.newLine()
        outputWriter.write("Unique Words: $totalUniqueWords")
        outputWriter.newLine()
        outputWriter.newLine()

        for (occurrence in groupedMap.keys) {
            outputWriter.write("$occurrence: \n${groupedMap.getValue(occurrence).joinToString("\n")}")
            outputWriter.newLine()
            outputWriter.newLine()
            outputWriter.flush()
        }

        outputWriter.close()
        println("Done!")
    }
    fun beginWordering(reader: BufferedReader) {

        val specialCharacters = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=', ',','.',':',';','?',']','[','}','{','/','\\')
        for (line in reader.lines().iterator()) {
            if (line != null) {
                addToUngroupedMap(line
                    .lowercase()
                    .filter { it !in specialCharacters }
                    .split(' ')
                    .iterator())
            }
        }
        createGroupedMap()
    }
    fun checkInputErrors(inputPath: Path) : Boolean {
        when {
        Files.notExists(inputPath) -> {
            println("File does not exist")
            return false
        }
        !Files.isRegularFile(inputPath) -> {
            println("File is not a regular file")
            return false
        }
        !Files.isReadable(inputPath) -> {
            println("File is not readable")
            return false
            }
        }
        return true
    }
    fun reset() {
        ungroupedMap.clear()
        groupedMap.clear()
    }
}


enum class ArgType {
    HELP, FIND, WORDS, INPUT, WRITE, INDEX, EXIT,
}
class Interact() {
    private val worderer = WordererFull()

    init {
        var interactiveMode = true

        while (interactiveMode) {
            print("> ")


            val args = readln().split(' ')

            val arg1 = when (args[0]) {
                "help" -> ArgType.HELP
                "find" -> ArgType.FIND
                "words" -> ArgType.WORDS
                "input" -> ArgType.INPUT
                "write" -> ArgType.WRITE
                "index" -> ArgType.INDEX
                "exit" -> ArgType.EXIT
                else -> ArgType.HELP
            }

            when {
                arg1 in listOf(ArgType.FIND, ArgType.WORDS, ArgType.INPUT) && args.size < 2 -> {
                    println("not enough arguments. use `help` to learn syntax")
                    continue
                }
                arg1 == ArgType.INDEX && args.size < 3 -> {
                    println("not enough arguments. use `help` to learn syntax")
                    continue
                }
                arg1 == ArgType.INDEX && (args[1].toIntOrNull() == null || args[2].toIntOrNull() == null) -> {
                    println("one or more arguments passed are not numbers. use `help` to learn syntax")
                    continue
                }
                arg1 == ArgType.WORDS && args[1].toIntOrNull() == null -> {
                    println("argument passed is not a number")
                    continue
                }
            }

            when (arg1) {
                ArgType.HELP -> help()
                ArgType.FIND -> println(find(args[1]))
                ArgType.WORDS -> println(words(args[1].toInt()))
                ArgType.INDEX -> index(args[1].toInt() - 1 , args[2].toInt() - 1)
                ArgType.INPUT -> input(args[1])
                ArgType.WRITE -> write()
                ArgType.EXIT -> interactiveMode = false
            }
        }
        println("bye bye")
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
                    "exit                     : Exits interactive mode and does not write to output file"
        )

    }
    private fun find(word: String): String {

        for (occurrence in worderer.groupedMap.keys) {
            if (word in worderer.groupedMap.getValue(occurrence)) {
                return occurrence.toString()
            } else {
                continue
            }
        }
        return "Word not found"
    }
    private fun words(searchValue: Int) : String{
        for (occurrence in worderer.groupedMap.keys) {
            if (occurrence == searchValue) {
                return worderer.groupedMap.getValue(occurrence).joinToString(", ")

            }
        }
        return "No words found"
    }
    private fun index(start: Int, stop: Int) {
        val allValues: MutableList<Int> = mutableListOf(0)
        allValues.run {
            for (v in worderer.groupedMap.keys) this.add(v)
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

        for (occ in worderer.groupedMap.keys) {
            if (worderer.groupedMap.keys.indexOf(occ) in start..stop) {
                println("$occ: ${worderer.groupedMap.getValue(occ).joinToString(", ")}")
            }
        }
    }

    private fun input(pathToInput: String) {
        if (!worderer.checkInputErrors(Path.of(pathToInput))) {
            return
        }
        worderer.reset()
        worderer.beginWordering(BufferedReader(File(pathToInput).reader()))
        println("Success!")
    }

    private fun write() {
        worderer.writeToOutput()
    }


}

fun main() {
    print("Run in interactive mode? [y/n]: ")
    if (readln() == "y") {
        val session = Interact()
    } else {
        val worderer = WordererFull()
        worderer.writeToOutput()
    }
}

