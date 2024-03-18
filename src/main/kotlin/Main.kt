import java.io.File
import java.nio.file.Files
import java.nio.file.Path


fun main() {

    val m = WordMap()
    m.makeMap()


    //print("Complete path to input text file: ")
    var input = "/home/dylan/Documents/tfotr.txt"//readln()
    while (!isInputFileOK(Path.of(input))) {
        print("Complete path to input file: ")
        input = readln()
    }


    //print("Complete path to desired output file: ")
    var output = "/home/dylan/Documents/output.txt"//readln()
    while (!isInputFileOK(Path.of(output))) {
        print("Complete path to output file: ")
        output = readln()
    }

    print("Run in interactive mode? [y/n]: ")
    val response = readln()
    if (response == "y") {
        interactive(input, output)
    } else {
        val orderedMap: MutableMap<Int, List<String>> = createWordsMap(input)
        writeToFile(output,orderedMap)
    }
}
fun writeToFile(output: String, orderedMap: MutableMap<Int, List<String>>) {

    Files.deleteIfExists(Path.of(output))
    Files.createFile(Path.of(output))

    val writer = File(output).bufferedWriter(Charsets.UTF_8)

    writer.write("Total Words: ${getAmountOfWords(orderedMap)}\n" +
            "Unique Words: ${getUniqueWords(orderedMap)}")
    writer.newLine()
    writer.newLine()

    for (occurrence in orderedMap.keys) {
        writer.write("$occurrence: \n${orderedMap.getValue(occurrence).joinToString("\n")}\n\n")
        writer.flush()
    }
    writer.close()
    println("Successfully Written")
}

class WordMap{
    val input = File("/Users/4JStudent/Documents/readme.txt")

    private val bigMap = mutableMapOf<Int, MutableList<String>>()
    private val bufReader = input.bufferedReader()
    // read line.
    // add words to a small map
    // add words to full map
    // next line
    private fun readLineToList(): MutableList<String> {
        val line = bufReader.readLine()
        val wordList: MutableList<String> = mutableListOf()
        val wordBuilder = StringBuilder()

        for (char in line) {
            if (char.isWhitespace() || char in listOf('\u0020', '\n', "")) {
                wordList.add(removeSpecials(wordBuilder.toString().lowercase()))
                wordBuilder.clear()
            } else {
                wordBuilder.append(char)
            }
        }
        return wordList
    }
    private fun smallUnordMap(wordList: MutableList<String>) : MutableMap<String, Int>{
        val smallMap: MutableMap<String, Int> = mutableMapOf()
        for (word in wordList) {
            if (word in smallMap.keys) {
                smallMap[word] = (smallMap.getValue(word) + 1)
            }
            smallMap.putIfAbsent(word, 1)
        }
        return smallMap
    }

    private fun addToMap(smallMap: MutableMap<String, Int>) {
       if (bigMap.keys.isEmpty()) {
            for (word in smallMap.keys) {
                if (!bigMap.keys.contains(smallMap.getValue(word))) bigMap[smallMap.getValue(word)] = mutableListOf(word)
                else bigMap.getValue(smallMap.getValue(word)).add(word)
            }
           return
        }

        // go through unordered map
        // check words in unordered map against words in ordered map
        // if word is found in ordered map change the key by x amount
        for (word in smallMap.keys) {
            for (v in bigMap.keys) {
                val valList = bigMap.getValue(v)
                if (valList.contains(word)) {
                    valList.remove(word)
                    if (!bigMap.keys.contains(smallMap.getValue(word) + v)) bigMap[smallMap.getValue(word) + v] = mutableListOf(word)
                    else bigMap.getValue(smallMap.getValue(word) + v).add(word)
                }
            }
        }
        println(bigMap)


    }
    fun makeMap() {
        for (x in 0..20) {
            val lineList = readLineToList()
            val unord = smallUnordMap(lineList)
            addToMap(unord)
        }

    }

}

fun createWordsMap(inputPath: String) : MutableMap<Int, List<String>>{
    print("Parsing input... ")
    val bufReader = File(inputPath).bufferedReader(Charsets.UTF_8)
    val unorderedWordMap: MutableMap<String,Int> = mutableMapOf()

    bufReader.forEachLine {
        val wordList: MutableList<String> = mutableListOf()
        val wordBuilder = StringBuilder()

        for (char in it) {
            if (char.isWhitespace() || char in listOf('\u0020', '\n', "")) {
                wordList.add(removeSpecials(wordBuilder.toString().lowercase()))
                wordBuilder.clear()
            } else {
                wordBuilder.append(char)
            }
        }
        for (word in wordList) {
            if (word in unorderedWordMap.keys) {
                unorderedWordMap[word] = (unorderedWordMap.getValue(word) + 1)
            }
            unorderedWordMap.putIfAbsent(word, 1)

        }
    }

    val orderedWordMap: MutableMap<Int, List<String>> = mutableMapOf()

    val minimumOccurrenceValue = unorderedWordMap.values.minOf { it }
    var currentOccurrenceValue = unorderedWordMap.values.maxOf { it } - 1
    val valuesThatExist = getValuesThatExist(unorderedWordMap)
    var index = valuesThatExist.size

    while (currentOccurrenceValue >= minimumOccurrenceValue) {
        val indexList: MutableList<String> = mutableListOf()

        for (word in unorderedWordMap.keys) {
            val value = unorderedWordMap.getValue(word)
            if (currentOccurrenceValue in valuesThatExist && value == currentOccurrenceValue) {
                indexList.add(word)
            }
        }

        if (indexList.isNotEmpty()) {
            orderedWordMap[currentOccurrenceValue] = indexList
        }

        if (index == 0) {
            break

        }

        index--
        currentOccurrenceValue = valuesThatExist[index]

    }
    unorderedWordMap.clear()
    println("done!")
    return orderedWordMap
}

fun interactive(inputPath: String, outputPath: String) {
    val interact = Interact(inputPath)
    var interactiveMode = true

    while (interactiveMode) {
        print("> ")
        val input = readln()

        val args: MutableList<String> = mutableListOf()
        val wordBuilder = StringBuilder()

        for ((i, char) in input.withIndex()) {
            if (char.isWhitespace()) {
                args.add(wordBuilder.toString())
                wordBuilder.clear()
            } else if (i == input.length - 1) {
                wordBuilder.append(char)
                args.add(wordBuilder.toString())
                wordBuilder.clear()
            }
            else{
                wordBuilder.append(char)
            }
        }

        val arg1Enum = when (args[0]) {
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
            arg1Enum in listOf(ArgType.FIND, ArgType.WORDS, ArgType.INPUT) && args.size < 2 -> {
                println("not enough arguments. use `help` to learn syntax")
                continue
            }
            arg1Enum == ArgType.INDEX && args.size < 3 -> {
                println("not enough arguments. use `help` to learn syntax")
                continue
            }
            arg1Enum == ArgType.INDEX && (args[1].toIntOrNull() == null || args[2].toIntOrNull() == null) -> {
                println("one or more arguments passed are not numbers. use `help` to learn syntax")
                continue
            }
            arg1Enum == ArgType.WORDS && args[1].toIntOrNull() == null -> {
                println("argument passed is not a number")
                continue
            }
        }

        when (arg1Enum) {
            ArgType.HELP -> interact.help()
            ArgType.FIND -> interact.find(args[1])
            ArgType.WORDS -> interact.words(args[1].toInt())
            ArgType.INDEX -> interact.index(args[1].toInt() - 1 , args[2].toInt() - 1)
            ArgType.INPUT -> interact.input(args[1])
            ArgType.WRITE -> interact.write(outputPath)
            ArgType.EXIT -> interactiveMode = false
        }
    }
    println("bye bye")
}

enum class ArgType {
    HELP, FIND, WORDS, INPUT, WRITE, INDEX, EXIT,
}
class Interact(inputPath: String) {

    private var wordMap = createWordsMap(inputPath)

    private fun createMap(inputPath: String) {
        wordMap = createWordsMap(inputPath)
    }

    fun help() {
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
    fun find(word: String) {

        for (occurrence in wordMap.keys) {
            if (word in wordMap.getValue(occurrence)) {
                print(occurrence.toString())
            } else {
                continue
            }
        }
        print("Word not found")
    }
    fun words(searchValue: Int){
        for (occurrence in wordMap.keys) {
            if (occurrence == searchValue) {
                print(wordMap.getValue(occurrence).joinToString(", "))

            }
        }
        print("No words found")
    }

    fun input(pathToInput: String) {
        if (!isInputFileOK(Path.of(pathToInput))) {
            return
        }
        wordMap.clear()
        createMap(pathToInput)
    }

    fun write(outputPath: String) {
        writeToFile(outputPath, wordMap)
    }

    fun index(start: Int, stop: Int) {
        val allValues: MutableList<Int> = mutableListOf()
        for (v in wordMap.keys) allValues.add(v)

        if (start !in allValues || stop !in allValues) println("start or stop index not a valid index (max: ${allValues.size})")

        for (occ in wordMap.keys) {
            if (wordMap.keys.indexOf(occ) in start..stop) {
                println("$occ: ${wordMap.getValue(occ).joinToString(", ")}")
            }

        }
    }
}



fun getAmountOfWords(orderedMap: MutableMap<Int, List<String>>) : Int {
    var count = 0
    for (occurrence in orderedMap.keys) {
        count += (orderedMap.getValue(occurrence).size * occurrence)
    }
    return count
}

fun getUniqueWords(orderedMap: MutableMap<Int, List<String>>) : Int {
    var count = 0
    for (key in orderedMap.keys) {
        count += orderedMap.getValue(key).size
    }
    return count
}

fun isInputFileOK(path: Path) : Boolean {
    when {
        Files.notExists(path) -> {
            println("File does not exist")
            return false
        }
        !Files.isRegularFile(path) -> {
            println("File is not a regular file")
            return false
        }
        !Files.isReadable(path) -> {
            println("File is not readable")
            return false
        }
    }
    return true

}

fun removeSpecials(word: String): String {
    val specials = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=',
        ',','.',':',';','?',']','[','}','{','/','\\','\u0022','\u0027','\u201c','\u201d', '\u0060','\u000d','\u000a','\u0009',
        '\u00a0', '\u0020')
    return word.filter {
        it !in specials
    }

}
fun getValuesThatExist(map:MutableMap<String,Int>): MutableList<Int> {

    val returnList: MutableList<Int> = mutableListOf()
    for (value in map.values.sorted()) {
        if (value !in returnList) {
            returnList.add(value)
        }
    }
    return returnList

}