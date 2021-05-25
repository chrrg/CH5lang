import ch5.Compiler
import java.io.File

fun main() {
    Compiler.compile(File("src/test/code/dev.ch5"), File("src/test/code/dev.exe"))
}