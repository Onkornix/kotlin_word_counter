import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

class Worderer {
    private val ungroupedMap = mutableMapOf<String,Int>()
    private val groupedMap = mutableMapOf<Int,MutableList<String>>()
    private val inputAndOutputPaths = readln().split(' ')
    private val inputReader = BufferedReader(File(inputAndOutputPaths[0]).reader())
    private val outputWriter = BufferedWriter(File(inputAndOutputPaths[1]).writer())

    init {
        val specialCharacters = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=', ',','.',':',';','?',']','[','}','{','/','\\')
        for (line in inputReader.lines().iterator()) {
            if (line != null) {
                addToUngroupedMap(line
                    .lowercase()
                    .filter { it !in specialCharacters }
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
            val wordsInGroup: MutableList<String> = mutableListOf()

            for (word in ungroupedMap.keys) {
                if (currentOccurrenceValue in valuesThatExist
                    && ungroupedMap.getValue(word) == currentOccurrenceValue) {
                    wordsInGroup.add(word)
                }
            }

            if (wordsInGroup.isNotEmpty()) {
                groupedMap[currentOccurrenceValue] = wordsInGroup
            }

            if (index == 0) break
            index--
            currentOccurrenceValue = valuesThatExist[index]

            val forRemoval: MutableList<String> = mutableListOf()
            for (word in ungroupedMap.keys) {
                if (ungroupedMap.getValue(word) > currentOccurrenceValue) {
                    forRemoval.add(word)
                }
            }
            for (word in forRemoval) {
                ungroupedMap.remove(word)
            }
        }
    }

    private fun writeToOutput() {
        if (File(inputAndOutputPaths[1]).exists()) File(inputAndOutputPaths[1]).delete()
        File(inputAndOutputPaths[1]).createNewFile()

        for (occurrence in groupedMap.keys) {
            outputWriter.write("$occurrence: \n${groupedMap.getValue(occurrence).joinToString("\n")}")
            outputWriter.newLine()
            outputWriter.newLine()
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