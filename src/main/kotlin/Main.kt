import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.writer


fun main() {
    //print("complete path to file: ")
    val output = Files.createFile(Path.of("/Users/4JStudent/Documents/output.txt"))
    val reader = File("/Users/4jStudent/Documents/readme.txt").bufferedReader(charset = Charsets.UTF_16, bufferSize = 40_000)

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
    val writer = output.writer(Charsets.UTF_16)

    for (key in wordsMap.keys) {
        println("$key : ${wordsMap.getValue(key)}")
        //writer.appendLine()
    }
}

fun removeSpecials(word: String): String {
    val specials = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=',
        ',','.',':',';','?',']','[','}','{','/','\u0022','\u201c','\u201d', '\u0060')
    return word.filter {
        it !in specials
    }

}
