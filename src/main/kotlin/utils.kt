import java.io.File

fun readIntegerList(filename: String) =
    File("src/main/resources/$filename.txt").readLines().map { it.toInt() }
