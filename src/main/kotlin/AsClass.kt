import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
class Worderer {
    private val ungroupedMap = mutableMapOf<String,Int>()
    private val groupedMap = mutableMapOf<Int,MutableList<String>>()
    private val inputOutputPaths = readln().split(' ')
    private val inputReader = BufferedReader(File(inputOutputPaths[0]).reader())
    private val outputWriter = BufferedWriter(File(inputOutputPaths[1]).writer())
    init {
        val specials = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=', ',','.',':',';','?',']','[','}','{','/','\\')
        for (line in inputReader.lines().iterator()) {
            if (line != null) {
                addToUngroupedMap(line
                    .lowercase()
                    .filter { it !in specials }
                    .split(' ')
                    .iterator())
            }
        }
        createGroupedMap()
        writeToOutput()
    }
    private fun addToUngroupedMap(line: Iterator<String>) {
        for (word in line) {
            if (word.isBlank()) continue
            if (word in ungroupedMap.keys) {
                ungroupedMap[word] = (ungroupedMap.getValue(word) + 1)
            }
            ungroupedMap.putIfAbsent(word, 1)
        }
    }
    private fun createGroupedMap() {
        val valuesThatExist = run {
            val valuesThatExist: MutableList<Int> = mutableListOf()
            for (number in ungroupedMap.values.sorted()) {
                if (number !in valuesThatExist) {
                    valuesThatExist.add(number)
                }
            }
            valuesThatExist
        }
        var currentOccurrenceValue = ungroupedMap.values.max() - 1
        var index = valuesThatExist.size
        while (currentOccurrenceValue >= ungroupedMap.values.min()) {
            val wordInGroup: MutableList<String> = mutableListOf()
            for (word in ungroupedMap.keys) {
                if (currentOccurrenceValue in valuesThatExist && ungroupedMap.getValue(word) == currentOccurrenceValue) {
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
            for (word in forRemoval) ungroupedMap.remove(word)
        }
        ungroupedMap.clear()
    }
    private fun writeToOutput() {
        if (File(inputOutputPaths[1]).exists()) File(inputOutputPaths[1]).delete()
        File(inputOutputPaths[1]).createNewFile()
        for (occurrence in groupedMap.keys) {
            outputWriter.write("$occurrence: \n${groupedMap.getValue(occurrence).joinToString("\n")}\n\n")
            outputWriter.flush()
        }
        outputWriter.close()
        println("Done!")
    }
}
fun main() {
    print("complete paths to input and then output files (separated by a space): ")
    Worderer()
}