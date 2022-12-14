class Day14 : Solver {
    override val day = 14

    private val input = readStringList("14")
    private val dropPoint = Point(500, 0)

    override fun partA() = dropAllSand(false)

    override fun partB() = dropAllSand(true)

    private fun dropAllSand(withFloor: Boolean): Int {
        val rockPoints = getRockPoints()
        val lowestRock = rockPoints.maxOf { it.y }
        val yMax = if (withFloor) lowestRock + 2 else lowestRock
        val isRockPoint = { pt: Point -> rockPoints.contains(pt) || (withFloor && pt.y == yMax) }

        val result = dropSandRecursive(yMax, isRockPoint)
        return result.size
    }

    private tailrec fun dropSandRecursive(
        yMax: Int,
        isRockPoint: (Point) -> Boolean,
        sandPoints: Set<Point> = emptySet()
    ): Set<Point> {
        val newPoint = getNextMove(sandPoints, yMax, isRockPoint) ?: return sandPoints
        return dropSandRecursive(yMax, isRockPoint, sandPoints + newPoint)
    }

    private fun getNextMove(
        sandPoints: Set<Point> = emptySet(),
        yMax: Int,
        isRockPoint: (Point) -> Boolean,
        point: Point = dropPoint
    ): Point? {
        if (sandPoints.contains(dropPoint)) {
            return null
        }

        val preferredPoints =
            listOf(Point(point.x, point.y + 1), Point(point.x - 1, point.y + 1), Point(point.x + 1, point.y + 1), point)

        val newPoint =
            preferredPoints.first { pt -> !isRockPoint(pt) && !sandPoints.contains(pt) }
        return if (newPoint.y > yMax) {
            null
        } else if (newPoint == point) {
            point
        } else {
            getNextMove(sandPoints, yMax, isRockPoint, newPoint)
        }
    }

    private fun getRockPoints(): Set<Point> = input.flatMap { inputLine ->
        val pointPairs = inputLine.split(" -> ").map(::parsePoint).windowed(2)
        pointPairs.flatMap { getLine(it[0], it[1]) }
    }.toSet()

    private fun parsePoint(pointStr: String): Point {
        val split = pointStr.split(",")
        return Point(split[0].toInt(), split[1].toInt())
    }

    private fun getLine(pointA: Point, pointB: Point): List<Point> =
        if (pointA.x != pointB.x) {
            (minOf(pointA.x, pointB.x)..maxOf(pointA.x, pointB.x)).map { Point(it, pointA.y) }
        } else {
            (minOf(pointA.y, pointB.y)..maxOf(pointA.y, pointB.y)).map { Point(pointA.x, it) }
        }
}