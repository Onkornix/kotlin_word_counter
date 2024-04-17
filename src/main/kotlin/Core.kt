import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

fun main() {
    print("complete paths to input and then output files (separated by a space): ")
    val input = readln().split(' ')
    val inputFile = File(input[0])
    val outputFile = File(input[1])

    // doing the ungrouped map as a separate function because
    // it makes things more readable in my opinion ...
    val ungroupedMap = populateUngroupedMap(inputFile.bufferedReader())

    /// ... mainly because this function is long and spacious
    val groupedMap = createGroupedMap(ungroupedMap)

    // Output is not a separate function because it's easier this way
    // and the process is so short.
    // Also, because it's just this and will never be anything but
    val bufOutputWriter = outputFile.bufferedWriter()
    for (occurrence in groupedMap.keys.reversed()) {
        bufOutputWriter.write("$occurrence: \n${groupedMap.getValue(occurrence).joinToString("\n")}")
        bufOutputWriter.newLine()
        bufOutputWriter.newLine()
        bufOutputWriter.flush()
    }
    bufOutputWriter.close()

    println("Done!")

}
fun populateUngroupedMap(bufInputReader: BufferedReader) : MutableMap<String, Int> {
    val linesIterator = bufInputReader.lines().iterator()
    val ungroupedMap: MutableMap<String, Int> = mutableMapOf()

    // I'm using an iterator on all the lines of the file (that fill the buffer)
    // because I felt like it lol.
    // I could have just used .forEachLine and achieved basically the same result I guess
    while (linesIterator.hasNext()) {

        // exhuming any sense of character from the text :3
        val line = linesIterator.next()
            .lowercase()
            .filter { it !in listOf('!','@','#','$','%','^','&','*','(',')','-','_',
                '+','=', ',','.',':',';','?',']','[','}','{','/','\\','<','>','`','\'') }
            .split(' ')

        for (word in line) {
            if (word.isBlank()) continue
            if (word in ungroupedMap.keys) {
                ungroupedMap.run {
                    // I can assert newValue will not be null because
                    // it is only using existing keys due to the if statement above
                    val newValue = get(word)!! + 1
                    set(word, newValue)
                }

            } else {
                ungroupedMap[word] = 1
            }
        }
    }
    bufInputReader.close()
    return ungroupedMap
}
fun createGroupedMap(ungroupedMap: MutableMap<String, Int>) : MutableMap<Int, List<String>> {
    // using a .run closure because I think it is more clear
    // than declaring the variable and then mutating it later
    val valuesThatExist = mutableListOf<Int>().run {
        // reversed so grouping begins with the most common word
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