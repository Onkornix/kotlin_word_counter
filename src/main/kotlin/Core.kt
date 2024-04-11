import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

fun main() {
    print("complete paths to input and then output files (separated by a space): ")
    val input = readln().split(' ')
    val inputFile = File(input[0])
    val outputFile = File(input[1])

    val ungroupedMap = populateUngroupedMap(inputFile.bufferedReader())

    val groupedMap = createGroupedMap(ungroupedMap)

    writeToOutput(outputFile.bufferedWriter(), groupedMap)
    println("Done!")

}
fun populateUngroupedMap(bufInputReader: BufferedReader) : MutableMap<String, Int> {
    val linesIterator = bufInputReader.lines().iterator()
    val ungroupedMap: MutableMap<String, Int> = mutableMapOf()

    while (linesIterator.hasNext()) {
        val line = linesIterator
            .next()
            .lowercase()
            .filter { it !in listOf('!','@','#','$','%','^','&','*','(',')','-','_',
                '+','=', ',','.',':',';','?',']','[','}','{','/','\\','<','>','`','\'') }
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
    return ungroupedMap
}
fun createGroupedMap(ungroupedMap: MutableMap<String, Int>) : MutableMap<Int, List<String>> {
    val valuesThatExist: MutableList<Int> = mutableListOf<Int>().run {
        for (number in ungroupedMap.values.sorted().reversed()) {
            if (number !in this) {
                this.add(number)
            }
        }
        this
    }

    val groupedMap: MutableMap<Int, List<String>> = mutableMapOf()
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
    return groupedMap
}
fun writeToOutput(bufOutputWriter: BufferedWriter, groupedMap: MutableMap<Int, List<String>>) {
    for (occurrence in groupedMap.keys.reversed()) {
        bufOutputWriter.write("$occurrence: \n${groupedMap.getValue(occurrence).joinToString("\n")}")
        bufOutputWriter.newLine()
        bufOutputWriter.newLine()
        bufOutputWriter.flush()
    }
    bufOutputWriter.close()
}