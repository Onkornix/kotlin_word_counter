import java.io.File
import java.nio.file.Files
import java.nio.file.Path


fun main() {

    //print("Complete path to input text file: ")
    var input = "/Users/4JStudent/Documents/readme.txt"//readln()
    while (!isInputFileOK(Path.of(input))) {
        print("Complete path to input file: ")
        input = readln()
    }


    //print("Complete path to desired output file: ")
    var output = "/Users/4JStudent/Documents/output.txt"//readln()
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



fun orderWordsMap(wordsMap: MutableMap<String, Int>) : MutableMap<Int, List<String>>{
    val returnMap: MutableMap<Int, List<String>> = mutableMapOf()

    val minimumOccurrenceValue = wordsMap.values.minOf { it }
    var currentOccurrenceValue = wordsMap.values.maxOf { it } - 1
    val valuesThatExist = getValuesThatExist(wordsMap)
    var index = valuesThatExist.size

    while (currentOccurrenceValue >= minimumOccurrenceValue) {
        val indexList: MutableList<String> = mutableListOf()

        for (word in wordsMap.keys) {
            val value = wordsMap.getValue(word)
            if (currentOccurrenceValue in valuesThatExist && value == currentOccurrenceValue) {
                indexList.add(word)
            }
        }

        if (indexList.isNotEmpty()) {
            returnMap[currentOccurrenceValue] = indexList
        }

        if (index == 0) {
            break
        }

        index--
        currentOccurrenceValue = valuesThatExist[index]

    }
    //println("Input Parsed")
    return returnMap
}
fun createWordsMap(inputPath: String) : MutableMap<Int, List<String>>{

    val reader = File(inputPath).bufferedReader(Charsets.UTF_8)
    val returnMap: MutableMap<String,Int> = mutableMapOf()

    reader.forEachLine {
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
            if (word in returnMap.keys) {
                returnMap[word] = (returnMap.getValue(word) + 1)
            }
            returnMap.putIfAbsent(word, 1)

        }
    }
    return orderWordsMap(returnMap)
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

enum class ResponseType {
    HELP,
    FIND,
    WORDS,
    INPUT,
    WRITE,
    EXIT,
}
class Interact(inputPath: String, outputPath: String) {

    private var orderedMap = createWordsMap(inputPath)

    private fun createMap(inputPath: String) {
        orderedMap = createWordsMap(inputPath)
    }
    fun help() : String =
            "Interactive commands are:\n" +
            "help                     : Prints this help\n" +
            "find [word]              : Prints given word's occurrence value\n" +
            "words [value]            : Lists all words that occur [value]] amount of times. multiple words are separated with [sep] (warning: may be a lot of words)\n" +
            "input [path/to/file]     : Runs the program on file given without exiting interactive mode\n" +
            "write                    : Continues program and writes to output file without exiting interactive mode\n" +
            "exit                     : Exits interactive mode and finishes program"
    fun find(word: String) : String{

        for (occurrence in orderedMap.keys) {
            if (word in orderedMap.getValue(occurrence)) {
                return occurrence.toString()
            } else {
                continue
            }
        }
        return "Word not found"
    }
    fun words(searchValue: Int) : String {
        for (occurrence in orderedMap.keys) {
            if (occurrence == searchValue) {
                return orderedMap.getValue(occurrence).joinToString(", ")

            }
        }
        return "No words found"
    }

    fun input(pathToInput: String) {
        var pathToInput = pathToInput
        while (!isInputFileOK(Path.of(pathToInput))) {
            print("Complete path to text file: ")
            pathToInput = readln()
        }
        orderedMap.clear()
        createMap(pathToInput)
    }

    fun write(outputPath: String) {
        writeToFile(outputPath, orderedMap)
    }
}

fun interactive(inputPath: String, outputPath: String) {
    val interact = Interact(inputPath, outputPath)
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
        when {
            args[0] in listOf("find", "words", "input", "write") && args.size < 2 -> {
                println("not enough arguments. use `help` to learn syntax")
                continue
            }
            args[0] == "words" && args[1].toIntOrNull() == null -> {
                println("argument passed cannot parse to an integer. use `help` to learn syntax")
                continue
            }
        }

        val response = when (args[0]) {
            "help" -> ResponseType.HELP
            "find" -> ResponseType.FIND
            "words" -> ResponseType.WORDS
            "input" -> ResponseType.INPUT
            "write" -> ResponseType.WRITE
            "exit" -> ResponseType.EXIT
            else -> ResponseType.HELP
        }

        when (response) {
            ResponseType.HELP -> println(interact.help())
            ResponseType.FIND -> println(interact.find(args[1]))
            ResponseType.WORDS -> println(interact.words(args[1].toInt()))
            ResponseType.INPUT -> interact.input(args[1])
            ResponseType.WRITE -> interact.write(outputPath)
            ResponseType.EXIT -> interactiveMode = false
        }
//        when (args[0]) {
//            "words" -> {
//                val searchValue = args[1].toInt()
//                var sepChar = ","
//                if (args.size >= 3) {
//                    sepChar = args[2]
//                }
//
//                var isFound = false
//                for (occurrence in orderedMap.keys) {
//                    if (occurrence == searchValue) {
//                        println(orderedMap.getValue(occurrence).joinToString("$sepChar "))
//                        isFound = true
//                        break
//                    }
//                }
//                if (!isFound) println("No words found")
//            }
//            "input" -> {
//                var newInput = args[1]
//
//                while (!isInputFileOK(Path.of(newInput))) {
//                    print("Complete path to text file: ")
//                    newInput = readln()
//                }
//                orderedMap.clear()
//                orderedMap = createWordsMap(args[1])
//            }
//            "write" -> {
//                writeToFile(outputPath, orderedMap)
//            }
//            "exit" -> {
//                orderedMap.clear()
//                interactiveMode = false
//            }
//            else -> {
//                println(helpText)
//            }
//        }
    }
    println("bye bye")
}