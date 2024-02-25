import java.io.File
import java.nio.file.Files
import java.nio.file.Path


fun main() {

    Files.deleteIfExists(Path.of("/home/dylan/Documents/output.txt"))
    Files.createFile(Path.of("/home/dylan/Documents/output.txt"))

    val wordsMap: MutableMap<String, Int> = createWordsMap()

    val output = File("/home/dylan/Documents/output.txt")
    val writer = output.bufferedWriter()

    val minimumOccurrenceValue = wordsMap.values.minOf { it }
    var currentOccurrenceValue = wordsMap.values.maxOf { it } - 1
    val valuesThatExist: MutableList<Int> = getValuesThatExist(wordsMap)
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

            writer.write("\n$currentOccurrenceValue ----- \n${indexList.joinToString("\n")}")
            writer.newLine()
            writer.flush()
        }

        if (index == 0) {
            break
        }

        index--
        currentOccurrenceValue = valuesThatExist[index]

    }
    writer.close()
}

fun createWordsMap() : MutableMap<String,Int>{

    print("Complete path to text file: ")
    val input = readln()

    val reader = File(input).bufferedReader()
    val returnMap: MutableMap<String,Int> = mutableMapOf()

    reader.forEachLine {
        val wordList: MutableList<String> = mutableListOf()
        val charList: MutableList<Char> = mutableListOf()

        for (char in it) {
            if (char.isWhitespace()) {
                wordList.add(removeSpecials(charList.joinToString("").lowercase()))
                charList.clear()
            } else {
                charList.add(char)
            }
        }
        for (word in wordList) {
            if (word in returnMap.keys) {
                returnMap[word] = (returnMap.getValue(word) + 1)
            }
            returnMap.putIfAbsent(word, 1)

        }
    }
    return returnMap
}

fun removeSpecials(word: String): String {
    val specials = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=',
        ',','.',':',';','?',']','[','}','{','/','\u0022','\u201c','\u201d', '\u0060','\u000d','\u000a')
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