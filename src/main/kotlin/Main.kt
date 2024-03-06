import java.io.File
import java.nio.file.Files
import java.nio.file.Path


fun main() {


    val outputPath = "/Users/4JStudent/Documents/output.txt"
    Files.deleteIfExists(Path.of(outputPath))
    Files.createFile(Path.of(outputPath))

    val wordsMap: MutableMap<String, Int> = createWordsMap()

    val output = File(outputPath)
    val writer = output.bufferedWriter(Charsets.ISO_8859_1)

    val minimumOccurrenceValue = wordsMap.values.minOf { it }
    var currentOccurrenceValue = wordsMap.values.maxOf { it } - 1
    val valuesThatExist: MutableList<Int> = getValuesThatExist(wordsMap)
    var index = valuesThatExist.size

    writer.write("Words: ${doCounting(wordsMap)}\n" +
            "Unique Words: ${wordsMap.keys.size}\n")

    while (currentOccurrenceValue >= minimumOccurrenceValue) {

        val indexList: MutableList<String> = mutableListOf()

        for (word in wordsMap.keys) {
            val value = wordsMap.getValue(word)
            if (currentOccurrenceValue in valuesThatExist && value == currentOccurrenceValue) {
                indexList.add(word)
            }
        }

        if (indexList.isNotEmpty()) {

            writer.write("\n$currentOccurrenceValue ----- ${percentOfMax(doCounting(wordsMap), currentOccurrenceValue)}%" +
                    "\n${indexList.joinToString("\n")}")
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
    println("written to $outputPath")
}

fun createWordsMap() : MutableMap<String,Int>{

    print("Complete path to text file: ")
    var inputPath = "/Users/4JStudent/Documents/readme.txt"//readln()

    while (!isInputFileOK(Path.of(inputPath))) {
        print("Complete path to text file: ")
        inputPath = readln()
    }

    val reader = File(inputPath).bufferedReader(Charsets.ISO_8859_1)
    val returnMap: MutableMap<String,Int> = mutableMapOf()

    reader.forEachLine {
        val wordList: MutableList<String> = mutableListOf()
        val wordBuilder = StringBuilder()

        for (char in it) {
            if (char.isWhitespace()) {
                wordList.add(removeSpecials(wordBuilder.toString()))
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
    return returnMap
}
fun percentOfMax(max: Int, current: Int) : String {
    val percent = ((current.toDouble() / max.toDouble() ) * 100.0)
    if ("%.2f".format(percent) == "0.00") {
        return "0"
    }
    return "%.2f".format(percent)
}
fun doCounting(wordsMap: MutableMap<String,Int>) : Int {
    var count = 0
    for (word in wordsMap.keys) {
        count += wordsMap.getValue(word)
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
        ',','.',':',';','?',']','[','}','{','/','\\','\u0022','\u0027','\u201c','\u201d', '\u0060','\u000d','\u000a')
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