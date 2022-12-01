import java.io.File

fun readStringList(filename: String) =
    File("src/main/resources/$filename.txt").readLines()

fun readGroupedList(filename: String): List<List<Int>> =
    File("src/main/resources/$filename.txt").readText().trim().split("\n\n").map { it.split("\n") }
        .map { list -> list.map { it.toInt() } }

fun readIntegerList(filename: String) =
    readStringList(filename).map { it.toInt() }
