private data class Blizzard(val position: Point, val direction: Direction)
private data class ExplorationResult(val steps: Int, val blizzards: List<Blizzard>)

class Day24(mode: SolverMode) : Solver(24, mode) {
    private val grid = readStringGrid(filename)
    private val start = grid.entries.first { it.value == "." && it.key.y == grid.yMin }.key
    private val end = grid.entries.first { it.value == "." && it.key.y == grid.yMax }.key
    private val walls = grid.map.filter { it.value == "#" }.keys
    private val startBlizzards = parseBlizzards()

    override fun partA() = explorePaths(start, end, startBlizzards).steps

    override fun partB() =
        listOf(start to end, end to start, start to end).fold(
            ExplorationResult(
                0,
                startBlizzards
            )
        ) { (steps, blizzards), (start, end) ->
            val newResult = explorePaths(start, end, blizzards)
            ExplorationResult(steps + newResult.steps, newResult.blizzards)
        }.steps

    private fun explorePaths(startPoint: Point, endPoint: Point, blizzards: List<Blizzard>) =
        explorePaths(listOf(startPoint), blizzards, endPoint, 0)

    private tailrec fun explorePaths(
        currentPoints: List<Point>,
        blizzards: List<Blizzard>,
        goal: Point,
        steps: Int
    ): ExplorationResult {
        if (currentPoints.contains(goal)) {
            return ExplorationResult(steps, blizzards)
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
            !walls.contains(neighbour) && !blizzards.contains(neighbour)
        }

    private fun parseBlizzards() = parseBlizzardsForDirection("^", Direction(0, -1)) +
            parseBlizzardsForDirection("v", Direction(0, 1)) +
            parseBlizzardsForDirection(">", Direction(1, 0)) +
            parseBlizzardsForDirection("<", Direction(-1, 0))

    private fun parseBlizzardsForDirection(directionStr: String, direction: Direction) =
        grid.map.filter { it.value == directionStr }.keys.map { Blizzard(it, direction) }

    private fun iterateBlizzards(blizzards: List<Blizzard>) = blizzards.map { it.move() }

    private fun Blizzard.move(): Blizzard {
        val warpedX = warpCoordinate(position.x + direction.x, grid.xMin, grid.xMax)
        val warpedY = warpCoordinate(position.y + direction.y, grid.yMin, grid.yMax)
        return Blizzard(Point(warpedX, warpedY), direction)
    }

    private fun warpCoordinate(coord: Int, min: Int, max: Int) =
        when (coord) {
            min -> max - 1
            max -> min + 1
            else -> coord
        }
}