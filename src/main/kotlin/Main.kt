import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Path


fun main() {

    print("Complete path to input text file: ")
    var input = readln()
    while (!isInputFileOK(Path.of(input))) {
        print("Complete path to input file: ")
        input = readln()
    }

    print("Complete path to desired output file: ")
    val output = readln()

    writeToFile(output,createWordsMap(input))

}
fun writeToFile(output: String, orderedMap: MutableMap<Int, List<String>>) {

    if (File(output).exists()) {
        File(output).delete()
    }
    File(output).createNewFile()

    val writer = BufferedWriter(File(output).writer())

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

fun createWordsMap(inputPath: String) : MutableMap<Int, List<String>>{
    print("Parsing input... ")
    val bufReader = BufferedReader((File(inputPath).reader()))

    val unorderedWordAndCount: MutableMap<String,Int> = mutableMapOf()

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
            if (word in unorderedWordAndCount.keys) {
                unorderedWordAndCount[word] = (unorderedWordAndCount.getValue(word) + 1)
            }
            unorderedWordAndCount.putIfAbsent(word, 1)

        }
    }

    val orderedWordMap: MutableMap<Int, List<String>> = mutableMapOf()

    val minimumOccurrenceValue = unorderedWordAndCount.values.minOf { it }
    var currentOccurrenceValue = unorderedWordAndCount.values.maxOf { it } - 1
    val valuesThatExist = getValuesThatExist(unorderedWordAndCount)
    var index = valuesThatExist.size

    while (currentOccurrenceValue >= minimumOccurrenceValue) {
        val indexList: MutableList<String> = mutableListOf()

        for (word in unorderedWordAndCount.keys) {
            val value = unorderedWordAndCount.getValue(word)
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
    unorderedWordAndCount.clear()
    println("done!")
    return orderedWordMap
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