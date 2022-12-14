class Day14 : Solver {
    override val day = 14

    private val input = readStringList("14")
    private val dropPoint = Point(500, 0)

    override fun partA() = dropAllSand(false)

    override fun partB() = dropAllSand(true)

    private fun dropAllSand(withFloor: Boolean): Int {
        val grid = parseGrid(withFloor)
        val yMax = grid.keys.maxOf { it.y }

        var stillMoving = dropSand(grid, yMax)
        while (stillMoving) {
            stillMoving = dropSand(grid, yMax)
        }

        return grid.values.count { it == "o" }
    }

    private fun dropSand(grid: MutableMap<Point, String>, yMax: Int): Boolean {
        val newPoint = getNextMove(grid, yMax)
        if (newPoint != null) {
            grid[newPoint] = "o"
        }

        return newPoint != null
    }

    private fun getNextMove(grid: Map<Point, String>, yMax: Int, point: Point = dropPoint): Point? {
        if (grid[dropPoint] == "o") {
            return null
        }

        val preferredPoints =
            listOf(Point(point.x, point.y + 1), Point(point.x - 1, point.y + 1), Point(point.x + 1, point.y + 1), point)

        val newPoint = preferredPoints.first { grid.getOrDefault(it, ".") == "." }
        return if (newPoint.y > yMax) {
            null
        } else if (newPoint == point) {
            point
        } else {
            getNextMove(grid, yMax, newPoint)
        }
    }

    private fun parseGrid(withFloor: Boolean): MutableMap<Point, String> {
        val rockPoints: Set<Point> = input.flatMap { inputLine ->
            val pointPairs = inputLine.split(" -> ").map(::parsePoint).windowed(2)
            pointPairs.flatMap { getLine(it[0], it[1]) }
        }.toSet()

        val xMin = rockPoints.minOf { it.x }
        val xMax = rockPoints.maxOf { it.x }
        val yMax = rockPoints.maxOf { it.y }

        val xStart = if (withFloor) xMin - 200 else xMin
        val xEnd = if (withFloor) xMax + 200 else xMax
        val yEnd = if (withFloor) yMax + 2 else yMax

        val map = mutableMapOf<Point, String>()

        for (x in (xStart..xEnd)) {
            for (y in 0..yEnd) {
                map[Point(x, y)] =
                    if (rockPoints.contains(Point(x, y))) "#" else if (withFloor && y == yEnd) "#" else "."
            }
        }

        return map
    }

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