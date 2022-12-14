class Day14 : Solver {
    override val day = 14

    private val input = readStringList("14")
    private val dropPoint = Point(500, 0)

    override fun partA(): Any {
        val grid = parseGrid()

        var (stillMoving, newGrid) = dropSand(grid)
        while (stillMoving) {
            val (newStopped, andNewGrid) = dropSand(newGrid)
            stillMoving = newStopped
            newGrid = andNewGrid

//            newGrid.print()
//            println()
//            println()
        }

        return newGrid.map.values.count { it == "o" }
    }

    private fun dropSand(grid: Grid<String>): Pair<Boolean, Grid<String>> {
        var previousPt: Point = dropPoint
        var pt: Point = getNextMove(grid, dropPoint)

        while (pt != previousPt && pt.y < grid.yMax) {
            previousPt = pt
            pt = getNextMove(grid, pt)
        }

        if (pt == previousPt && pt != dropPoint) {
            return true to grid.setValue(previousPt, "o")
        } else {
            return false to grid
        }
    }

    private fun getNextMove(grid: Grid<String>, point: Point): Point {
        val preferredPoints =
            listOf(Point(point.x, point.y + 1), Point(point.x - 1, point.y + 1), Point(point.x + 1, point.y + 1), point)
        return preferredPoints.find { grid.map.getOrDefault(it, ".") == "." }!!
    }

    private fun parseGrid(): Grid<String> {
        val rockPoints: Set<Point> = input.flatMap { inputLine ->
            val pointPairs = inputLine.split(" -> ").map(::parsePoint).windowed(2)
            pointPairs.flatMap { getLine(it[0], it[1]) }
        }.toSet()

        val xMin = rockPoints.minOf { it.x }
        val xMax = rockPoints.maxOf { it.x }
        val yMax = rockPoints.maxOf { it.y }

        val map = mutableMapOf<Point, String>()

        for (x in (xMin..xMax)) {
            for (y in 0..yMax) {
                map[Point(x, y)] = if (rockPoints.contains(Point(x, y))) "#" else "."
            }
        }

        return Grid(map)
    }

    private fun parsePoint(pointStr: String): Point {
        val split = pointStr.split(",")
        return Point(split[0].toInt(), split[1].toInt())
    }

    private fun getLine(pointA: Point, pointB: Point): List<Point> =
        if (pointA.x != pointB.x && pointA.y != pointB.y) {
            throw Error("Points differ in both dirs: $pointA, $pointB")
        } else if (pointA.x != pointB.x) {
            (minOf(pointA.x, pointB.x)..maxOf(pointA.x, pointB.x)).map { Point(it, pointA.y) }
        } else {
            (minOf(pointA.y, pointB.y)..maxOf(pointA.y, pointB.y)).map { Point(pointA.x, it) }
        }

    override fun partB(): Any {
        val grid = parseGridB()

        var (stillMoving, newGrid) = dropSandB(grid)
        while (stillMoving) {
            val (newStopped, andNewGrid) = dropSandB(newGrid)
            stillMoving = newStopped
            newGrid = andNewGrid

//            newGrid.print()
//            println()
//            println()
        }

        return newGrid.map.values.count { it == "o" } + 1
    }

    private fun parseGridB(): Grid<String> {
        val rockPoints: Set<Point> = input.flatMap { inputLine ->
            val pointPairs = inputLine.split(" -> ").map(::parsePoint).windowed(2)
            pointPairs.flatMap { getLine(it[0], it[1]) }
        }.toSet()

        val xMin = rockPoints.minOf { it.x }
        val xMax = rockPoints.maxOf { it.x }
        val yMax = rockPoints.maxOf { it.y } + 2

        val map = mutableMapOf<Point, String>()

        for (x in (xMin..xMax)) {
            for (y in 0..yMax) {
                map[Point(x, y)] = if (rockPoints.contains(Point(x, y))) "#" else if (y == yMax) "#" else "."
            }
        }

        return Grid(map)
    }

    private fun dropSandB(grid: Grid<String>): Pair<Boolean, Grid<String>> {
        var previousPt: Point = dropPoint
        var pt: Point = getNextMoveB(grid, dropPoint)

        while (pt != previousPt && pt.y < grid.yMax) {
            previousPt = pt
            pt = getNextMoveB(grid, pt)
        }

        if (pt == previousPt && pt != dropPoint) {
            if (!(grid.xMin..grid.xMax).contains(pt.x)) {
                val newPts = (grid.yMin..grid.yMax).map { Point(pt.x, it) }
                val intermediate = grid.setValues(newPts, ".")
                return true to intermediate.setValue(Point(pt.x, grid.yMax), "#").setValue(pt, "o")
            }
            return true to grid.setValue(previousPt, "o")
        } else {
            return false to grid
        }
    }

    private fun getNextMoveB(grid: Grid<String>, point: Point): Point {
        val preferredPoints =
            listOf(Point(point.x, point.y + 1), Point(point.x - 1, point.y + 1), Point(point.x + 1, point.y + 1), point)
        return preferredPoints.find {
            val result = grid.map[it]
            val terrain = result ?: if (it.y == grid.yMax) "#" else "."
            terrain == "."
        }!!
    }
}