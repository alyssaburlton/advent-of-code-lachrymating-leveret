class Day24 : Solver {
    override val day = 24

    private val grid = readStringGrid("24")
    private val start = grid.map.entries.first { it.value == "." && it.key.y == grid.yMin }.key
    private val end = grid.map.entries.first { it.value == "." && it.key.y == grid.yMax }.key
    private val startBlizzards = parseBlizzards()

    override fun partA() = explorePaths(listOf(start)).first

    override fun partB(): Any {
        val (stepsA, blizzardsA) = explorePaths(listOf(start))
        val (stepsB, blizzardsB) = explorePaths(listOf(end), blizzardsA, start)
        val (stepsC, _) = explorePaths(listOf(start), blizzardsB, end)

        return stepsA + stepsB + stepsC
    }

    private tailrec fun explorePaths(
        currentPoints: List<Point>,
        blizzards: List<Blizzard> = startBlizzards,
        goal: Point = end,
        steps: Int = 0
    ): Pair<Int, List<Blizzard>> {
        if (currentPoints.contains(goal)) {
            return steps to blizzards
        }

        val newBlizzards = iterateBlizzards(blizzards)
        val blizzardPoints = newBlizzards.map { it.position }.toSet()

        val newPoints = currentPoints.flatMap { point -> takeAllSteps(point, blizzardPoints) }.distinct()
        return explorePaths(newPoints, newBlizzards, goal, steps + 1)
    }

    private fun takeAllSteps(
        point: Point,
        blizzards: Set<Point>
    ) = (grid.neighbours(point) + point)
        .filter { neighbour ->
            grid.getValue(neighbour) != "#" && !blizzards.contains(neighbour)
        }

    private data class Blizzard(val position: Point, val direction: Direction)

    private fun parseBlizzards(): List<Blizzard> {
        val upBlizzards = grid.map.filter { it.value == "^" }.keys.map { Blizzard(it, Direction(0, -1)) }
        val downBlizzards = grid.map.filter { it.value == "v" }.keys.map { Blizzard(it, Direction(0, 1)) }
        val rightBlizzards = grid.map.filter { it.value == ">" }.keys.map { Blizzard(it, Direction(1, 0)) }
        val leftBlizzards = grid.map.filter { it.value == "<" }.keys.map { Blizzard(it, Direction(-1, 0)) }

        return upBlizzards + downBlizzards + rightBlizzards + leftBlizzards
    }

    private fun iterateBlizzards(blizzards: List<Blizzard>) = blizzards.map { it.move() }

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