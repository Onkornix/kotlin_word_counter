import java.io.File
import java.nio.file.Files
import java.nio.file.Path


fun main() {
    //print("complete path to file: ")

    val reader = File("/Users/4jStudent/Documents/readme.txt").bufferedReader(charset = Charsets.UTF_8, bufferSize = 4_000)

    val wordsMap: MutableMap<String, Int> = mutableMapOf()

    reader.forEachLine {
        val wordList: MutableList<String> = mutableListOf()
        val charList: MutableList<Char> = mutableListOf()

        for (char in it) {

            if (char.isWhitespace()) {
                charList.add(char)
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

    println(makeOrderedMap(wordsMap))

    /* FILE OUTPUT TEMPORARILY DISABLED
    Files.deleteIfExists(Path.of("/Users/4JStudent/Documents/output.txt"))
    Files.createFile(Path.of("/Users/4JStudent/Documents/output.txt"))

    val output = File("/Users/4JStudent/Documents/output.txt")
    val writer = output.bufferedWriter(Charsets.UTF_8,4_000)


    for (key in wordsMap.keys) {
        //println("$key : ${wordsMap.getValue(key)}")
        writer.write("$key : ${wordsMap.getValue(key)}")
        writer.newLine()
    }
    writer.flush()
    writer.close()
    */

}

fun makeOrderedMap(unorderedMap:MutableMap<String,Int>): MutableMap<Int,List<String>> {
    val orderedMap: MutableMap<Int,List<String>> = mutableMapOf()
    val indexList: MutableList<String> = mutableListOf()

    var occurrence = 0 // occurrence means amount of times the word occurs in the file.
    while (occurrence < unorderedMap.size) {

        for (word in unorderedMap.keys) {
            if (unorderedMap.getValue(word) == occurrence) {
                indexList.add(word)
            }
        }
        orderedMap[occurrence] = indexList
        indexList.clear()
        occurrence++
    }

    return orderedMap


}

fun removeSpecials(word: String): String {
    val specials = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=',
        ',','.',':',';','?',']','[','}','{','/','\u0022','\u201c','\u201d', '\u0060')
    return word.filter {
        it !in specials
    }

}
