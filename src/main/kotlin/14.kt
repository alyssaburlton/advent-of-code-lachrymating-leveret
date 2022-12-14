class Day14 : Solver {
    override val day = 14

    private val input = readStringList("14")
    private val rockPoints = getRockPoints()
    private val lowestRock = rockPoints.maxOf { it.y }

    override fun partA() = dropAllSand(false)

    override fun partB() = dropAllSand(true)

    private fun dropAllSand(withFloor: Boolean) = dropSandRecursive(withFloor).values.flatten().size

    private tailrec fun dropSandRecursive(
        withFloor: Boolean,
        sandPoints: Map<Int, Set<Point>> = emptyMap(),
        prevPoints: List<Point> = listOf(Point(500, 0))
    ): Map<Int, Set<Point>> {
        if (prevPoints.isEmpty()) {
            return sandPoints
        }

        val moveResult = getNextMove(sandPoints, prevPoints.last(), prevPoints)
        val (newPoint, newPrevPoints) = moveResult
        if (!withFloor && newPoint.y > lowestRock) {
            return sandPoints
        }

        val otherPoints = sandPoints.getOrDefault(newPoint.y, emptySet()) + newPoint
        return dropSandRecursive(
            withFloor,
            sandPoints.plus(newPoint.y to otherPoints),
            newPrevPoints
        )
    }

    private fun getNextMove(
        sandPoints: Map<Int, Set<Point>>,
        point: Point,
        prevPoints: List<Point>
    ): Pair<Point, List<Point>> {
        val preferredPoints =
            listOf(Point(point.x, point.y + 1), Point(point.x - 1, point.y + 1), Point(point.x + 1, point.y + 1))

        val relevantSandPoints = sandPoints.getOrDefault(point.y + 1, emptySet())
        val newPoint = preferredPoints.firstOrNull { pt -> !isRockPoint(pt) && !relevantSandPoints.contains(pt) }
        
        return if (newPoint == null) {
            point to prevPoints.filterNot { it == point }
        } else {
            getNextMove(sandPoints, newPoint, prevPoints + newPoint)
        }
    }

    private fun isRockPoint(pt: Point) = (pt.y == lowestRock + 2) || rockPoints.contains(pt)

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