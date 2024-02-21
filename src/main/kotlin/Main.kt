import java.io.File


fun main() {
    //print("complete path to file: ")
    val reader = File("/home/dylan/Downloads/tfotr.txt").bufferedReader(charset = Charsets.UTF_8, bufferSize = 40_000)

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


}

fun removeSpecials(word: String): String {
    val specials = listOf('!','@','#','$','%','^','&','*','(',')','-','_','+','=',
        ',','.',':',';','?',']','[','}','{','/','\u0022','\u201c','\u201d', '\u0060')
    return word.filter {
        it !in specials
    }

}
