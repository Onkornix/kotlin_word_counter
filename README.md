# Worderer 
*[Word] + [Orderer] = Worderer*

This is my create task (aka final project) for the AP Computer Science Principles class at my high school.

### Core Version
It's got all of the things that the AP people wanna see: input, collecton type, function, function call, algorithm, output

1. Reads a plain text file (.txt) line by line
2. Counts how many times each word occurs
3. Groups words together based on how many times they occur
4. Writes the numerically ordered word groups to another text file

### Full Version
Same core functionality as mentioned before, but with some extra stuff:
- **Interactive mode:** call functions to view data related to the ordered words real time
  - Find:  prints the amount of times a word occurs (if at all)
  - Words: prints all the word groups that occur *n* times to the terminal
  - Index: prints all the word groups between occurrence values *a* and *b* (descending)
  - Input: runs core functionality on a new file and updates the map without exiting interactive mode
  - Write: writes numerically ordered words to output file without exiting interactive mode
- **Error Handling!** (YAY!)
  - It won't crash! (as much)
  - I didn't add error handling to the core version because the AP people didn't say I needed to, and I wanted to keep the script more simple and concise
 
### Why Kotlin though?
That's a good question. I joined my high school's robotics team and hopped on the software sub-team. They used Kotlin for the robot and I figured 
learning it as an entire class would be great practice. It was, and I've definitely become a more skilled member of the team... however, knowing Kotlin =/= 
knowing how to program an FRC robot. Suffice to say I ended up not being much help to the team that season. 

I do kind of wish I had decided to learn a programming language that's a bit more useful, instead of one for high school robotics and android apps... 
So I [re-wrote this project in Rust!](https://github.com/Onkornix/rust_word_counter) (well i'm currently doing that). Mainly because I think that learning something more technical and difficult, 
a low level systems programming language for example, will help me stand out more in the job market in the future. Except I currently plan on doing 
hardware or network engineering as a college major, while learning Rust and making/contributing to projects as a hobby. But I guess I'll see where life takes me!
