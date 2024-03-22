import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
fun main() {
    print("Complete path to input text file: ")
    var input = "/Users/4JStudent/Documents/text.txt"//readln()
    while (!isInputFileOK(Path.of(input))) {
        print("Complete path to input file: ")
        input = readln()
    }
    print("Complete path to desired output file: ")
    val output = "/Users/4JStudent/Documents/output.txt"//readln()
    val occurrenceToWordsMap = createWordsMap(input)
    var totalWordsCount = 0
    for (occurrence in occurrenceToWordsMap.keys) {
        totalWordsCount += (occurrenceToWordsMap.getValue(occurrence).size * occurrence)
    }
    var uniqueWordsCount = 0
    for (key in occurrenceToWordsMap.keys) {
        uniqueWordsCount += occurrenceToWordsMap.getValue(key).size
    }
    if (File(output).exists()) {
        File(output).delete()
    }
    File(output).createNewFile()
    val writer = BufferedWriter(File(output).writer())
    writer.write("Total Words: $totalWordsCount")
    writer.newLine()
    writer.write("Unique Words: $uniqueWordsCount")
    writer.newLine()
    writer.newLine()
    for (occurrence in occurrenceToWordsMap.keys) {
        writer.write("$occurrence: (${occurrenceToWordsMap.getValue(occurrence).size})")
        writer.newLine()
        writer.write(occurrenceToWordsMap
            .getValue(occurrence)
            .sorted()
            .joinToString("\n"))
        writer.newLine()
        writer.newLine()
        writer.flush()
    }
    writer.close()
    println("Done!")
}
fun createWordsMap(inputPath: String) : MutableMap<Int, List<String>>{
    val bufReader = BufferedReader((File(inputPath).reader()))
    val wordToOccurrenceMap: MutableMap<String,Int> = mutableMapOf()
    val specialCharacters = listOf('!',',','.',':',';','?','\\','\u0022','\u0027','\u201c','\u201d', '\u0060','\u000d','\u000a','\u0009', '\u00a0', '\u0020')
    var line: String?
    while (
        run {
            line = bufReader.readLine()
            line
        } != null)  {
        val lineContents: String = line!!
        val wordList: MutableList<String> = mutableListOf()
        val wordBuilder = StringBuilder()
        for ((index, char) in lineContents.withIndex()) {
            if (char.isWhitespace() || char in listOf('\u0020', '\n', "")) {
                wordList.add(wordBuilder
                    .toString()
                    .lowercase()
                    .filter { it !in specialCharacters })
                wordBuilder.clear()
            }
            else if (index == lineContents.length - 1) {
                wordBuilder.append(char)
                wordList.add(wordBuilder
                    .toString()
                    .lowercase()
                    .filter { it !in specialCharacters })
                wordBuilder.clear()
            }
            else {
                wordBuilder.append(char)
            }
        }
        for (word in wordList) {
            if (word.isBlank()) continue
            if (word in wordToOccurrenceMap.keys) {
                wordToOccurrenceMap[word] = (wordToOccurrenceMap.getValue(word) + 1)
            }
            wordToOccurrenceMap.putIfAbsent(word, 1)
        }
    }
    val valuesThatExist: MutableList<Int> = mutableListOf()
    for (number in wordToOccurrenceMap.values.sorted()) {
        if (number !in valuesThatExist) {
            valuesThatExist.add(number)
        }
    }
    val occurrenceToWordsMap: MutableMap<Int, List<String>> = mutableMapOf()
    val minimumOccurrenceValue = wordToOccurrenceMap.values.minOf { it }
    var currentOccurrenceValue = wordToOccurrenceMap.values.maxOf { it } - 1
    var index = valuesThatExist.size
    while (currentOccurrenceValue >= minimumOccurrenceValue) {
        val indexList: MutableList<String> = mutableListOf()
        for (word in wordToOccurrenceMap.keys) {
            val occurrenceValue = wordToOccurrenceMap.getValue(word)
            if (currentOccurrenceValue in valuesThatExist && occurrenceValue == currentOccurrenceValue) {
                indexList.add(word)
            }
        }
        if (indexList.isNotEmpty()) occurrenceToWordsMap[currentOccurrenceValue] = indexList
        if (index == 0) break
        index--
        currentOccurrenceValue = valuesThatExist[index]
    }
    wordToOccurrenceMap.clear()
    return occurrenceToWordsMap
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