import java.io.File
import java.nio.file.Files
import java.nio.file.Path


fun main() {

    print("Complete path to input text file: ")
    var input = readln()
    print("Complete path to desired output file: ")
    var output = readln()

    while (!isInputFileOK(Path.of(input))) {
        print("Complete path to input file: ")
        input = readln()
    }
    while (!isInputFileOK(Path.of(output))) {
        print("Complete path to output file: ")
        output = readln()
    }

    print("Run in interactive mode? [y/n]: ")
    if (readln() == "y") {
        interactive(input, output)
    }

    val orderedMap: MutableMap<Int, List<String>> = createWordsMap(input)

    writeToFile(output,orderedMap)

    println("Program complete")
}
fun writeToFile(output: String, orderedMap: MutableMap<Int, List<String>>) {

    Files.deleteIfExists(Path.of(output))
    Files.createFile(Path.of(output))

    val writer = File(output).bufferedWriter(Charsets.ISO_8859_1, 1024)

    writer.write("Total Words: ${getAmountOfWords(orderedMap)}\n" +
            "Unique Words: ${getUniqueWords(orderedMap)}")
    writer.newLine()
    writer.newLine()

    for (occurrence in orderedMap.keys) {
        writer.write("$occurrence: \n${orderedMap.getValue(occurrence).joinToString("\n")}\n\n")
        writer.flush()
    }
    writer.close()
}

fun interactive(inputPath: String, outputPath: String) {
    var orderedMap = createWordsMap(inputPath)
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

        when (args[0]) {
            "help" -> {
                println("Interactive commands are:\n" +
                        "find [word]              : Prints given word's occurrence value\n" +
                        "words [value] [sep]      : Lists all words that occur [value]] amount of times. multiple words are separated with [sep] (warning: may be a lot of words)\n" +
                        "input [path/to/file]     : Runs the program on file given without exiting interactive mode\n" +
                        "write                    : Continues program and writes to output file without exiting interactive mode\n" +
                        "exit                     : Exits interactive mode and finishes program\n")
            }
            "find" -> {
                val findWord = args[1].lowercase()
                var isFound = false
                for (occurrence in orderedMap.keys) {
                    if (findWord in orderedMap.getValue(occurrence)) {
                        println(occurrence)
                        isFound = true
                        break
                    } else {
                        continue
                    }
                }

                if (!isFound) println("Word not found.")

            }
            "words" -> {
                val searchValue = args[1].toInt()
                var sepChar = ","
                if (args.size >= 3) {
                    sepChar = args[2]
                }

                var isFound = false
                for (occurrence in orderedMap.keys) {
                    if (occurrence == searchValue) {
                        println(orderedMap.getValue(occurrence).joinToString("$sepChar "))
                        isFound = true
                        break
                    }
                }
                if (!isFound) println("No words found")
            }
            "input" -> {
                var newInput = args[1]

                while (!isInputFileOK(Path.of(newInput))) {
                    print("Complete path to text file: ")
                    newInput = readln()
                }
                orderedMap.clear()
                orderedMap = createWordsMap(args[1])
            }
            "write" -> {
                writeToFile(outputPath, orderedMap)
            }
            "exit" -> {
                orderedMap.clear()
                interactiveMode = false
            }
            else -> {
                println("Interactive commands are:\n" +
                        "find [word]              : Prints given word's occurrence value\n" +
                        "words [value] [sep]      : Lists all words that occur [value]] amount of times. multiple words are separated with [sep] (warning: may be a lot of words)\n" +
                        "input [path/to/file]     : Runs the program on file given without exiting interactive mode\n" +
                        "write                    : Continues program and writes to output file without exiting interactive mode\n" +
                        "exit                     : Exits interactive mode and finishes program\n")
            }
        }
    }
    println("bye bye")
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

    return returnMap
}
fun createWordsMap(inputPath: String) : MutableMap<Int, List<String>>{

    val reader = File(inputPath).bufferedReader(Charsets.ISO_8859_1)
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