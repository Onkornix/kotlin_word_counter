import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

class WordererCore {
    private val ungroupedMap = mutableMapOf<String,Int>()
    private val groupedMap = mutableMapOf<Int,MutableList<String>>()
    private val inputAndOutputPaths = readln().split(' ')
    private val inputReader = BufferedReader(File(inputAndOutputPaths[0]).reader())
    private val outputWriter = BufferedWriter(File(inputAndOutputPaths[1]).writer())

    init {
        populateUnorderedMap()
        createGroupedMap()
        writeToOutput()
    }

    private fun populateUnorderedMap() {
        val specialCharacters = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=', ',','.',':',';','?',']','[','}','{','/','\\',"<",">")
        val lineIterator = inputReader.lines().iterator()
        while (lineIterator.hasNext()) {
            addToUngroupedMap(lineIterator.next()
                .lowercase()
                .filter { it !in specialCharacters }
                .split(' '))
        }
        inputReader.close()
    }

    private fun addToUngroupedMap(line: List<String>) {
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
            val createValuesThatExist: MutableList<Int> = mutableListOf()
            for (number in ungroupedMap.values.sorted()) {
                if (number !in createValuesThatExist) {
                    createValuesThatExist.add(number)
                }
            }
            createValuesThatExist
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
    WordererCore()
}