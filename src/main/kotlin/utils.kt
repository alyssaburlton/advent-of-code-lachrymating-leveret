import java.io.File

fun readString(filename: String) =
    File("src/main/resources/$filename.txt").readText().trimEnd()

fun readStringList(filename: String) =
    File("src/main/resources/$filename.txt").readLines()

fun readGroupedList(filename: String): List<List<String>> =
    File("src/main/resources/$filename.txt").readText().split("\n\n").map { it.split("\n") }

fun readIntegerGroupedList(filename: String) =
    readGroupedList(filename).map { group -> group.map { it.toInt() } }

fun readIntegerList(filename: String) =
    readStringList(filename).map { it.toInt() }

fun <E> Collection<E>.only(): E {
    if (size != 1) {
        throw Error("Trying to get only element, but size is $size")
    }

    return first()
}

fun <E> List<E>.productOf(transform: (E) -> Int) = map(transform).product()
fun List<Int>.product() = reduce(Int::times)
fun List<Long>.product() = reduce(Long::times)

fun <E> List<E>.replaceAt(index: Int, newValue: E) = subList(0, index) + newValue + subList(index + 1, size)

fun <E> List<E>.padWith(length: Int, value: E): List<E> {
    if (size > length) {
        throw Error("Trying to pad to length $length but my size is larger: $size")
    }

    val diff = length - size
    return this + List(diff) { value }
}