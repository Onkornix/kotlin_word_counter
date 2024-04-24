import fullModules.WordererFull
import fullModules.Interact

fun main() {
    print("Run in interactive mode? [y/n]: ")
    if (readln() == "y") {
        Interact().begin()
    } else {
        val worderer = WordererFull()
        worderer.writeToOutput()
    }
}

