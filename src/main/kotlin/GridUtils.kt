data class Point(val x: Int, val y: Int)

fun Point.neighbours() = listOf(
    Point(x, y - 1),
    Point(x, y + 1),
    Point(x - 1, y),
    Point(x + 1, y)
)

fun Point.neighboursWithDiagonals() = neighbours() + listOf(
    Point(x - 1, y - 1),
    Point(x - 1, y + 1),
    Point(x + 1, y - 1),
    Point(x + 1, y + 1)
)

fun readStringGrid(filename: String): Grid<String> {
    val list = readStringList(filename)
    return parseGrid(list)
}

fun parseGrid(gridLines: List<String>): Grid<String> {
    val rowLengths = gridLines.map { it.length }
    if (rowLengths.distinct().size != 1) {
        throw Error("Uneven row sizes in grid: $rowLengths")
    }

    val rowLength = rowLengths.first()
    val map = mutableMapOf<Point, String>()

    for (x in (0 until rowLength)) {
        for (y in gridLines.indices) {
            map[Point(x, y)] = gridLines[y][x].toString()
        }
    }

    return Grid(map.toMap())
}

class Grid<T>(val map: Map<Point, T>) {
    val xMax = map.keys.maxOf { it.x }
    val xMin = map.keys.minOf { it.x }
    val yMax = map.keys.maxOf { it.y }
    val yMin = map.keys.minOf { it.y }

    val rows = computeRows()
    val columns = computeColumns()

    val entries = map.entries

    fun prettyString() = (yMin..yMax).joinToString("\n") { y ->
        (xMin..xMax).joinToString("") { x -> map.getValue(Point(x, y)).toString() }
    }

    fun print() {
        println(prettyString())
    }

    fun <NewType> transformValues(transformer: (T) -> NewType): Grid<NewType> =
        Grid(map.mapValues { entry -> transformer(entry.value) })

    fun neighbours(pt: Point) =
        pt.neighbours().filter(map::containsKey)

    fun transpose(): Grid<T> =
        Grid(map.mapKeys { (key, _) -> Point(key.y, key.x) })

    private fun computeRows() =
        (yMin..yMax).map { y ->
            (xMin..xMax).map { x ->
                map.getValue(Point(x, y))
            }
        }

    private fun computeColumns() =
        (xMin..xMax).map { x ->
            (yMin..yMax).map { y ->
                map.getValue(Point(x, y))
            }
        }
}