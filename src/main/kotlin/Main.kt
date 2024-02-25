import java.io.File
import java.nio.file.Files
import java.nio.file.Path


fun main() {
    //print("complete path to file: ")

    val reader = File("/home/dylan/Documents/tfotr.txt").bufferedReader(charset = Charsets.ISO_8859_1, bufferSize = 4_000)

    val wordsMap: MutableMap<String, Int> = mutableMapOf()

    reader.forEachLine {
        val wordList: MutableList<String> = mutableListOf()
        val charList: MutableList<Char> = mutableListOf()

        for (char in it) {
            if (char.isWhitespace()) {
                wordList.add(removeSpecials(charList.joinToString("").lowercase()))
                charList.clear()
            } else {
                charList.add(char)
            }
        }
        for (word in wordList) {
            if (word in wordsMap.keys) {
                wordsMap[word] = (wordsMap.getValue(word) + 1)
            }
            wordsMap.putIfAbsent(word, 1)

        }
    }


    Files.deleteIfExists(Path.of("/home/dylan/Documents/output.txt"))
    Files.createFile(Path.of("/home/dylan/Documents/output.txt"))

    val output = File("/home/dylan/Documents/output.txt")
    val writer = output.bufferedWriter(Charsets.ISO_8859_1,4_000)


    val max = wordsMap.values.maxOf { it }
    var current = 1
    val valuesThatExist: MutableList<Int> = mutableListOf()
    var index = 0

    for (value in wordsMap.values.sorted()) {
        if (value !in valuesThatExist && value >= current) {
            valuesThatExist.add(value)
        }
    }

    while (current <= max) {

        val indexList: MutableList<String> = mutableListOf()

        for (word in wordsMap.keys) {
            val value = wordsMap.getValue(word)
            if (current in valuesThatExist && value == current) {
                indexList.add(word)
            }
        }

        if (indexList.isNotEmpty()) {
            writer.write("\n$current ----- \n${indexList.joinToString("\n")}")
            writer.flush()
        }

        if (index == valuesThatExist.size) {
            break
        }
        current = valuesThatExist[index]
        index++


    }
    writer.close()

}

fun removeSpecials(word: String): String {
    val specials = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=',
        ',','.',':',';','?',']','[','}','{','/','\u0022','\u201c','\u201d', '\u0060','\u000d','\u000a')
    return word.filter {
        it !in specials
    }

}
