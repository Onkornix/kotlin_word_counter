import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
fun main() {
    print("Complete path to input text file: ")
    val input = "/Users/4JStudent/Documents/Paradise.txt"//readln()
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
        writer.write("$occurrence: \n${occurrenceToWordsMap.getValue(occurrence).joinToString("\n")}\n\n")
        writer.flush()
    }
    writer.close()
    println("Done!")
}
fun createWordsMap(inputPath: String) : MutableMap<Int, List<String>> {
    val specials = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=', ',','.',':',';','?',']','[','}','{','/','\\')
    val bufReader = BufferedReader((File(inputPath).reader()))
    val wordToOccurrenceMap: MutableMap<String, Int> = mutableMapOf()
    var line: String?

    while (
        run {
            line = bufReader.readLine()
            line
        } != null) {

        val lineIterator = line!!
            .lowercase()
            .filter { it !in specials }
            .split(' ')
            .iterator()
        val nextWord = lineIterator.next()
        if (nextWord in wordToOccurrenceMap.keys) {
                wordToOccurrenceMap[nextWord] = (wordToOccurrenceMap.getValue(nextWord) + 1)
            }
            wordToOccurrenceMap.putIfAbsent(nextWord, 1)


//        val wordList: MutableList<String> = mutableListOf()
//        val wordBuilder = StringBuilder()
//        for (char in lineContents) {
//            if (char.isWhitespace() || char in listOf('\u0020', '\n', "")) {
//                wordList.add(wordBuilder
//                    .toString()
//                    .lowercase()
//                    .filter { it !in specials}
//                )
//                wordBuilder.clear()
//            } else if (lineContents.indexOf(char) == lineContents.lastIndex) {
//                wordBuilder.append(char)
//                wordList.add(wordBuilder
//                    .toString()
//                    .lowercase()
//                    .filter { it !in specials }
//                )
//                wordBuilder.clear()
//            } else {
//                wordBuilder.append(char)
//            }
//        }
//        for (word in wordList) {
//            if (word.isBlank()) continue
//            if (word in wordToOccurrenceMap.keys) {
//                wordToOccurrenceMap[word] = (wordToOccurrenceMap.getValue(word) + 1)
//            }
//            wordToOccurrenceMap.putIfAbsent(word, 1)
//
//        }
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