// /Users/4JStudent/Documents/example.txt /Users/4JStudent/Documents/output.txt
fun main() {
    print("Run in interactive mode? [y/n]: ")
    if (readln() == "y") {
        Interact().begin()
    } else {
        val worderer = WordererFull()
        worderer.writeToOutput()
    }
}

