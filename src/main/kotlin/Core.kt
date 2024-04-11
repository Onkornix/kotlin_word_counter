import java.io.File

fun main() {
    print("complete paths to input and then output files (separated by a space): ")
    WordererCore(readln().split(' '))
}

class WordererCore(private val inputAndOutputPaths: List<String>) {
    private val ungroupedMap = mutableMapOf<String,Int>()
    private val groupedMap = mutableMapOf<Int,MutableList<String>>()

    init {
        populateUngroupedMap()
        createGroupedMap()
        writeToOutput()
    }

    private fun populateUngroupedMap() {
        val bufInputReader = File(inputAndOutputPaths[0]).bufferedReader(bufferSize = 16384)
        val linesIterator = bufInputReader.lines().iterator()

        while (linesIterator.hasNext()) {
            val line = linesIterator
                .next()
                .lowercase()
                .filter { it !in listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=', ',','.',':',';','?',']','[','}','{','/','\\','<','>','`','\'') }
                .split(' ')

            for (word in line) {
                if (word.isBlank()) continue
                if (word in ungroupedMap.keys) {
                    ungroupedMap[word] = (ungroupedMap.getValue(word) + 1)
                }
                ungroupedMap.putIfAbsent(word, 1)
            }
        }
        bufInputReader.close()
    }

    private fun createGroupedMap() {
        val valuesThatExist: MutableList<Int> = mutableListOf<Int>().run {
            for (number in ungroupedMap.values.sorted().reversed()) {
                if (number !in this) {
                    this.add(number)
                }
            }
            this
        }

        var currentOccurrenceValue = ungroupedMap.values.min()
        var index = valuesThatExist.size

        while (currentOccurrenceValue <= ungroupedMap.values.max()) {
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

            val forRemoval = mutableListOf<String>().run {
                for (word in ungroupedMap.keys) {
                    if (ungroupedMap.getValue(word) < currentOccurrenceValue) {
                        this.add(word)
                    }
                }
                this
            }
            for (word in forRemoval) {
                ungroupedMap.remove(word)
            }
        }
        ungroupedMap.clear()
    }

    private fun writeToOutput() {
        val bufOutputWriter = File(inputAndOutputPaths[1]).bufferedWriter()
        for (occurrence in groupedMap.keys.reversed()) {
            bufOutputWriter.write("$occurrence: \n${groupedMap.getValue(occurrence).joinToString("\n")}")
            bufOutputWriter.newLine()
            bufOutputWriter.newLine()
            bufOutputWriter.flush()
        }
        bufOutputWriter.close()
        println("Done!")
    }
}