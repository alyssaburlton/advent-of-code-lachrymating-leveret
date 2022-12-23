import kotlin.math.abs

data class Point(val x: Int, val y: Int)
data class PointL(val x: Long, val y: Long)
data class Point3D(val x: Int, val y: Int, val z: Int)

fun Point3D.neighbours() = listOf(
    Point3D(x, y - 1, z),
    Point3D(x, y + 1, z),
    Point3D(x - 1, y, z),
    Point3D(x + 1, y, z),
    Point3D(x, y, z + 1),
    Point3D(x, y, z - 1)
)

fun Point.neighbours() = listOf(
    Point(x, y - 1),
    Point(x, y + 1),
    Point(x - 1, y),
    Point(x + 1, y)
)

fun Point.stepDistance(other: Point) = abs(x - other.x) + abs(y - other.y)

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

fun parseGrid(gridLines: List<String>, defaultRowValue: Char = ' '): Grid<String> {
    val rowLengths = gridLines.map { it.length }
//    if (rowLengths.distinct().size != 1) {
//        throw Error("Uneven row sizes in grid: $rowLengths")
//    }

    val rowLength = rowLengths.max()
    val pairs = (0 until rowLength).flatMap { x ->
        gridLines.indices.map { y ->
            Point(x, y) to gridLines[y].getOrElse(x) { defaultRowValue }.toString()
        }
    }

    return Grid(mapOf(*pairs.toTypedArray()))
}

class Grid<T>(val map: Map<Point, T>) {
    val xMax = map.keys.maxOf { it.x }
    val xMin = map.keys.minOf { it.x }
    val yMax = map.keys.maxOf { it.y }
    val yMin = map.keys.minOf { it.y }

    val rows = computeRows()
    val columns = computeColumns()

    val entries = map.entries

    fun getValue(pt: Point) = map.getValue(pt)

    fun setValue(pt: Point, value: T): Grid<T> {
        val newMap = map + (pt to value)
        return Grid(newMap)
    }

    fun prettyString() = (yMin..yMax).joinToString("\n") { y ->
        (xMin..xMax).joinToString("") { x -> map.getOrDefault(Point(x, y), ".").toString() }
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
                map.getOrDefault(Point(x, y), ".")
            }
        }

    private fun computeColumns() =
        (xMin..xMax).map { x ->
            (yMin..yMax).map { y ->
                map.getOrDefault(Point(x, y), ".")
            }
        }
}