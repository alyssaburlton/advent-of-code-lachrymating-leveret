import java.io.File

fun readStringList(filename: String) =
    File("src/main/resources/$filename.txt").readLines()

fun readIntegerList(filename: String) =
    readStringList(filename).map { it.toInt() }
