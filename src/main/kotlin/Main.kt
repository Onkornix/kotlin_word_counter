import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
fun main() {
    print("Complete path to input text file: ")
    val input = readln()
    print("Complete path to desired output file: ")
    val output = readln()
    val wordsMap = createWordsMap(input)
    val totalWordsCount = run {
        var totalWordsCount = 0
        for (occurrence in wordsMap.keys) {
            totalWordsCount += (wordsMap.getValue(occurrence).size * occurrence)
        }
        totalWordsCount
    }
    val uniqueWordsCount = run {
        var uniqueWordsCount = 0
        for (key in wordsMap.keys) {
            uniqueWordsCount += wordsMap.getValue(key).size
        }
        uniqueWordsCount
    }
    if (File(output).exists()) {
        File(output).delete()
    }
    File(output).createNewFile()
    val writer = BufferedWriter(File(output).writer())
    writer.write("Total Words: $totalWordsCount"); writer.newLine()
    writer.write("Unique Words: $uniqueWordsCount") ;writer.newLine(); writer.newLine()
    for (occurrence in wordsMap.keys) {
        writer.write("$occurrence: \n${wordsMap.getValue(occurrence).joinToString("\n")}\n\n")
        writer.flush()
    }
    writer.close()
    println("Done!")
}
fun createWordsMap(inputPath: String) : MutableMap<Int, List<String>> {
    val specials = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=', ',','.',':',';','?',']','[','}','{','/','\\')
    val bufReader = BufferedReader((File(inputPath).reader()))
    val ungroupedMap: MutableMap<String, Int> = mutableMapOf()
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
        for (nextWord in lineIterator) {
            if (nextWord.isBlank()) continue
            if (nextWord in ungroupedMap.keys) {
                ungroupedMap[nextWord] = (ungroupedMap.getValue(nextWord) + 1)
            }
            ungroupedMap.putIfAbsent(nextWord, 1)
        }
    }
    val valuesThatExist = run {
        val valuesThatExist: MutableList<Int> = mutableListOf()
        for (number in ungroupedMap.values.sorted()) {
            if (number !in valuesThatExist) {
                valuesThatExist.add(number)
            }
        }
        valuesThatExist
    }

    val groupedMap: MutableMap<Int, List<String>> = mutableMapOf()
    var currentOccurrenceValue = ungroupedMap.values.max() - 1
    var index = valuesThatExist.size
    while (currentOccurrenceValue >= ungroupedMap.values.max()) {
        val wordInGroup: MutableList<String> = mutableListOf()
        for (word in ungroupedMap.keys) {
            if (currentOccurrenceValue in valuesThatExist
                && ungroupedMap.getValue(word) == currentOccurrenceValue) {
                wordInGroup.add(word)
            }
        }
        if (wordInGroup.isNotEmpty()) groupedMap[currentOccurrenceValue] = wordInGroup
        if (index == 0) break
        index--
        currentOccurrenceValue = valuesThatExist[index]
        val forRemoval: MutableList<String> = mutableListOf()
        for (word in ungroupedMap.keys) {
            if (ungroupedMap.getValue(word) > currentOccurrenceValue) forRemoval.add(word)
        }
        for (word in forRemoval){
            ungroupedMap.remove(word)
        }
    }
    ungroupedMap.clear()
    return groupedMap
}