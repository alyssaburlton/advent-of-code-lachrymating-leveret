import kotlin.math.abs

data class Point(val x: Int, val y: Int)
data class PointL(val x: Long, val y: Long)
data class Point3D(val x: Int, val y: Int, val z: Int)

typealias Direction = Point

operator fun Point.plus(other: Point) = Point(x + other.x, y + other.y)
operator fun Point.unaryMinus() = Point(-x, -y)

fun Point3D.neighbours() = listOf(
    Point3D(x, y - 1, z),
    Point3D(x, y + 1, z),
    Point3D(x - 1, y, z),
    Point3D(x + 1, y, z),
    Point3D(x, y, z + 1),
    Point3D(x, y, z - 1)
)

fun Point.neighbours() = setOf(
    Point(x, y - 1),
    Point(x, y + 1),
    Point(x - 1, y),
    Point(x + 1, y)
)

fun Point.stepDistance(other: Point) = abs(x - other.x) + abs(y - other.y)

fun Point.neighboursWithDiagonals() = neighbours() + setOf(
    Point(x - 1, y - 1),
    Point(x - 1, y + 1),
    Point(x + 1, y - 1),
    Point(x + 1, y + 1)
)

fun readStringGrid(filename: String): FixedGrid<String> {
    val list = readStringList(filename)
    return parseGrid(list)
}

fun parsePointMap(lines: List<String>): Map<Point, String> {
    val pairs = lines.flatMapIndexed { y, line ->
        line.toCharArray().mapIndexed { x, value ->
            Point(x, y) to value.toString()
        }
    }

    return mapOf(*pairs.toTypedArray())
}

fun parseGrid(gridLines: List<String>): FixedGrid<String> {
    val rowLengths = gridLines.map { it.length }
    if (rowLengths.distinct().size != 1) {
        throw Error("Uneven row sizes in grid: $rowLengths")
    }

    return FixedGrid(parsePointMap(gridLines))
}

class FixedGrid<T>(val map: Map<Point, T>) {
    val xMax = map.keys.maxOf { it.x }
    val xMin = map.keys.minOf { it.x }
    val yMax = map.keys.maxOf { it.y }
    val yMin = map.keys.minOf { it.y }

    val rows = computeRows()
    val columns = computeColumns()

    val entries = map.entries

    fun getValue(pt: Point) = map.getValue(pt)

    fun prettyString() = (yMin..yMax).joinToString("\n") { y ->
        (xMin..xMax).joinToString("") { x -> map.getValue(Point(x, y)).toString() }
    }

    fun <NewType> transformValues(transformer: (T) -> NewType): FixedGrid<NewType> =
        FixedGrid(map.mapValues { entry -> transformer(entry.value) })

    fun neighbours(pt: Point) =
        pt.neighbours().filter(map::containsKey)

    fun transpose(): FixedGrid<T> =
        FixedGrid(map.mapKeys { (key, _) -> Point(key.y, key.x) })

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