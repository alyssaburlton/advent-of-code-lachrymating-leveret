class Day24 : Solver {
    override val day = 24

    private val grid = readStringGrid("24")
    private val start = grid.map.entries.first { it.value == "." && it.key.y == 0 }.key
    private val end = grid.map.entries.first { it.value == "." && it.key.y == grid.yMax }.key
    private val startBlizzards = parseBlizzards()


    override fun partA(): Any {
        return explorePaths(listOf(listOf(start))).first.minOf { it.size - 1 }
    }

    override fun partB(): Any {
        var stepsSoFar = 0
        val (pathsA, blizzardsA) = explorePaths(listOf(listOf(start)))
        stepsSoFar += pathsA.minOf { it.size - 1 }
        println(stepsSoFar)

        val (pathsB, blizzardsB) = explorePaths(listOf(listOf(end)), blizzardsA, start)
        stepsSoFar += pathsB.minOf { it.size - 1 }
        println(stepsSoFar)

        val (pathsC, _) = explorePaths(listOf(listOf(start)), blizzardsB, end)
        return stepsSoFar + pathsC.minOf { it.size - 1 }
    }

    private tailrec fun explorePaths(
        currentPaths: List<List<Point>>,
        blizzards: List<Blizzard> = startBlizzards,
        goal: Point = end
    ): Pair<List<List<Point>>, List<Blizzard>> {
        if (currentPaths.any { it.last() == goal }) {
            return currentPaths.filter { it.last() == goal } to blizzards
        }

        val newBlizzards = iterateBlizzards(blizzards)
        val blizzardPoints = newBlizzards.map { it.position }.toSet()

        val newPaths = currentPaths.flatMap { path -> takeAllSteps(path, blizzardPoints, goal) }
        val prunedPaths = newPaths.distinctBy { it.last() }
        return explorePaths(prunedPaths, newBlizzards, goal)
    }

    private fun takeAllSteps(
        path: List<Point>,
        blizzards: Set<Point>,
        goal: Point
    ): List<List<Point>> {
        val currentPoint = path.last()
        if (currentPoint == goal) {
            return listOf(path)
        }

        return (grid.neighbours(currentPoint) + currentPoint)
            .filter { neighbour ->
                grid.getValue(neighbour) != "#" && !blizzards.contains(neighbour)
            }.map { path + it }
    }

    private data class Blizzard(val position: Point, val direction: Direction)

    private fun parseBlizzards(): List<Blizzard> {
        val upBlizzards = grid.map.filter { it.value == "^" }.keys.map { Blizzard(it, Direction(0, -1)) }
        val downBlizzards = grid.map.filter { it.value == "v" }.keys.map { Blizzard(it, Direction(0, 1)) }
        val rightBlizzards = grid.map.filter { it.value == ">" }.keys.map { Blizzard(it, Direction(1, 0)) }
        val leftBlizzards = grid.map.filter { it.value == "<" }.keys.map { Blizzard(it, Direction(-1, 0)) }

        return upBlizzards + downBlizzards + rightBlizzards + leftBlizzards
    }

    private fun iterateBlizzards(blizzards: List<Blizzard>) = blizzards.map {
        it.move()
    }

    private fun Blizzard.move(): Blizzard {
        val newPos = Point(position.x + direction.x, position.y + direction.y)
        if (newPos.y == grid.yMin) {
            return Blizzard(Point(newPos.x, grid.yMax - 1), direction)
        } else if (newPos.y == grid.yMax) {
            return Blizzard(Point(newPos.x, grid.yMin + 1), direction)
        } else if (newPos.x == grid.xMax) {
            return Blizzard(Point(grid.xMin + 1, newPos.y), direction)
        } else if (newPos.x == grid.xMin) {
            return Blizzard(Point(grid.xMax - 1, newPos.y), direction)
        } else {
            return Blizzard(newPos, direction)
        }
    }
}