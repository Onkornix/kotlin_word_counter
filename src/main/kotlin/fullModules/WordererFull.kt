package fullModules

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
class WordererFull {
    private val homeDirectory = System.getProperty("user.home")
    private val workingDirectory = System.getProperty("user.dir")
    //private val props = System.getProperties()

    private val ungroupedMap = mutableMapOf<String,Int>()
    private val groupedMap = mutableMapOf<Int,MutableList<String>>()
    private var inputPath = "/dev/null"
    private var outputPath = "$homeDirectory/Documents/worderer_output.txt"
    init {
        val inputPrompt =
            "------------\n" +
            "Please type out path to the desired input file. \u001B[38;5;39mBy default uses relative path.\u001B[0m\n" +
            "Prefix with '~/' to use path relative to home directory.\n" +
            "prefix with '/' to use absolute path\n> "
        var goodInput = false
        while (!goodInput) {
            print(inputPrompt)
            val input = readln()

            inputPath = when(input[0].toString()) {
                "~/" -> {
                    "$homeDirectory$input"
                }
                "/" -> {
                    input
                }
                else -> {
                    "$workingDirectory/$input"
                }
            }

            if (!checkInputErrors(Path.of(inputPath))) {
                print(inputPrompt)
                continue
            }
            goodInput = true
        }
        val inputReader = BufferedReader(File(inputPath).reader())

        populateUngroupedMap(inputReader)
        createGroupedMap()
    }
    private fun populateUngroupedMap(reader: BufferedReader) {
        val specialCharacters = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=', ',','.',':',';','?',']','[','}','{','/','\\')
        //val alphabet = listOf('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z')
        val linesIterator = reader.lines().iterator()
        while (linesIterator.hasNext()) {
            val line: List<String> = linesIterator.next()
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
                } else {
                    ungroupedMap[word] = 1
                }
            }
        }
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
        var currentOccurrenceValue = ungroupedMap.values.min()
        var indexOfCurrentVal = valuesThatExist.size - 1


        while (true) {

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
            when (indexOfCurrentVal) {
                0 -> break
                else -> indexOfCurrentVal--
            }
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

    fun writeToOutput(outputWriter: BufferedWriter) {

        print("Default output is \u001B[38;5;m $outputPath \u001B[0m\n" +
                "Will be overwritten if it already exists. continue? [y/n]: ")

        if (readln() != "y") {
            println("Please enter absolute path to output file")
            outputPath = readln()
        }




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

        for (occurrence in groupedMap.keys.reversed()) {
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
                println("\u001B[38;5;197mERROR\u001B[0m: File does not exist")
                return false
            }

            !Files.isRegularFile(inputPath) -> {
                println("\u001B[38;5;197mERROR\u001B[0m: File is not a regular file")
                return false
            }

            !Files.isReadable(inputPath) -> {
                println("\u001B[38;5;197mERROR\u001B[0m: File is not readable")
                return false
            }
        }
        return true
    }
    fun beginWordering(reader: BufferedReader) {
        populateUngroupedMap(reader)
        createGroupedMap()
    }
    fun giveGroupedMap() : MutableMap<Int, MutableList<String>> {
        return groupedMap
    }

    fun reset() {
        ungroupedMap.clear()
        groupedMap.clear()
    }
}