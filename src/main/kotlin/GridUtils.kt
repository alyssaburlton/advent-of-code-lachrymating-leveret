data class Point(val x: Int, val y: Int)

fun readStringGrid(filename: String): Map<Point, String> {
    val list = readStringList(filename)
    return parseGrid(list)
}

fun parseGrid(gridLines: List<String>): Map<Point, String> {
    val rowLength = gridLines.first().length
    val map = mutableMapOf<Point, String>()

    for (x in (0 until rowLength)) {
        for (y in gridLines.indices) {
            map[Point(x, y)] = gridLines[y][x].toString()
        }
    }

    return map.toMap()
}

fun <T> Map<Point, String>.transformValues(transformer: (String) -> T): Map<Point, T> =
    mapValues { entry -> transformer(entry.value) }

fun Map<Point, String>.xMax() = keys.maxOf { it.x }
fun Map<Point, String>.xMin() = keys.minOf { it.x }
fun Map<Point, String>.yMax() = keys.maxOf { it.y }
fun Map<Point, String>.yMin() = keys.minOf { it.y }

fun Map<Point, String>.prettyString() =
    (yMin()..yMax()).joinToString("\n") { y ->
        (xMin()..xMax()).joinToString("") { x -> getValue(Point(x, y)) }
    }

fun Map<Point, String>.print() {
    println(prettyString())
}

fun Point.neighbours() = listOf(
    Point(x, y - 1),
    Point(x, y + 1),
    Point(x - 1, y),
    Point(x + 1, y)
)

fun Map<Point, String>.neighbours(pt: Point) =
    pt.neighbours().filter(::containsKey)
