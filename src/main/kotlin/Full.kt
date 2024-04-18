import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class WordererFull {
    private val ungroupedMap = mutableMapOf<String,Int>()
    private val groupedMap = mutableMapOf<Int,MutableList<String>>()
    private var inputAndOutputPaths = listOf<String>()
    private lateinit var inputReader: BufferedReader
    private lateinit var outputWriter: BufferedWriter
    fun begin() {
        print("complete paths to input and then output files (separated by a space): ")

        inputAndOutputPaths = readln().split(' ')
        while (!checkInputErrors(Path.of(inputAndOutputPaths[0]))) {
            print("correct file path: ")
            inputAndOutputPaths = listOf(readln(), inputAndOutputPaths[1])
        }
        inputReader = BufferedReader(File(inputAndOutputPaths[0]).reader())
        outputWriter = BufferedWriter(File(inputAndOutputPaths[1]).writer())

        populateUngroupedMap(inputReader)
        createGroupedMap()
    }
    private fun populateUngroupedMap(reader: BufferedReader) {
        val specialCharacters = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=', ',','.',':',';','?',']','[','}','{','/','\\')
        val linesIterator = reader.lines().iterator()
        while (linesIterator.hasNext()) {
            val line = linesIterator.next()
                .lowercase()
                .filter { it !in specialCharacters }
                .split(' ')
            for (word in line) {
                if (word.isBlank()) continue
                if (word in ungroupedMap.keys) {
                    ungroupedMap.run {
                        val newValue = get(word)!! + 1
                        set(word,newValue)
                    }
                }
                ungroupedMap[word] = 1
            }
        }

    }

    fun giveGroupedMap() : MutableMap<Int, MutableList<String>> {
        return groupedMap
    }

    private fun createGroupedMap() {
        // using a .run closure because I think it is more clear
        // than declaring the variable and then mutating it later
        val valuesThatExist = mutableListOf<Int>().run {
            // reversed so grouping begins with the highest occurrence values
            // a.k.a the least common words (there's always a lot of them)
            for (number in ungroupedMap.values.sorted().reversed()) {
                if (number !in this) {
                    this.add(number)
                }
            }
            this
        }

        // Starting with the least common words so that the huge list of
        // words that occur once is quickly grouped and then removed from
        // the ungrouped map for further iteration.
        val groupedMap: MutableMap<Int, List<String>> = mutableMapOf()
        var currentOccurrenceValue = ungroupedMap.values.min()
        var indexOfCurrentVal = valuesThatExist.size - 1


        while (indexOfCurrentVal != 0) {

            val group = mutableListOf<String>().run {
                for (word in ungroupedMap.keys) {
                    if (ungroupedMap.getValue(word) == currentOccurrenceValue) {
                        this.add(word)
                    }
                }
                this
            }

            groupedMap[currentOccurrenceValue] = group

            // setting the next occurrence value this way avoids setting it
            // to values that don't exist in the ungrouped map and decreases
            // time complexity a LOT
            indexOfCurrentVal--
            currentOccurrenceValue = valuesThatExist[indexOfCurrentVal]

            // removing words that have already been grouped obviously makes
            // subsequent iterations way faster. And it doesn't cause concurrent
            // modification errors or anything which is really chill.
            val forRemoval = mutableListOf<String>().run {
                for (word in ungroupedMap.keys) {
                    if (ungroupedMap.getValue(word) < currentOccurrenceValue) {
                        this.add(word)
                    }
                }
                this
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
class Interact {
    private val worderer = WordererFull()
    private val groupedMap = worderer.giveGroupedMap()
    fun begin() {
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

        for (occurrence in groupedMap.keys) {
            if (word in groupedMap.getValue(occurrence)) {
                return occurrence.toString()
            } else {
                continue
            }
        }
        return "Word not found"
    }
    private fun words(searchValue: Int) : String{
        for (occurrence in groupedMap.keys) {
            if (occurrence == searchValue) {
                return groupedMap.getValue(occurrence).joinToString(", ")

            }
        }
        return "No words found"
    }
    private fun index(start: Int, stop: Int) {
        val allValues: MutableList<Int> = mutableListOf(0)
        allValues.run {
            for (v in groupedMap.keys) this.add(v)
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

        for (occ in groupedMap.keys) {
            if (groupedMap.keys.indexOf(occ) in start..stop) {
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
        session.begin()
    } else {
        val worderer = WordererFull()
        worderer.begin()
        worderer.writeToOutput()
    }
}

