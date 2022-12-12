class Day12 : Solver {
    override val day = 12

    private val input = readStringGrid("12")
    private val myPosition = input.entries.first { entry -> entry.value == "S" }.key
    private val desiredPosition = input.entries.first { entry -> entry.value == "E" }.key

    private val grid = input.transformValues { value ->
        when (value) {
            "S" -> 0
            "E" -> 25
            else -> ('a'..'z').map { it.toString() }.indexOf(value)
        }
    }

    override fun partA() = getMinimumSteps(myPosition, ::canStepForwards) { it == desiredPosition }

    override fun partB() = getMinimumSteps(desiredPosition, ::canStepBackwards) { grid.getValue(it) == 0 }

    private fun canStepForwards(myElevation: Int, neighbourElevation: Int) = neighbourElevation <= myElevation + 1

    private fun canStepBackwards(myElevation: Int, neighbourElevation: Int) = neighbourElevation >= myElevation - 1

    private fun getMinimumSteps(
        startingPosition: Point,
        stepValidator: (Int, Int) -> Boolean,
        stopCondition: (Point) -> Boolean
    ): Int {
        val startingPaths = listOf(listOf(startingPosition))
        val result = explorePaths(startingPaths, setOf(startingPosition), stepValidator, stopCondition)
        return result.minOf { it.size - 1 }
    }

    private fun explorePaths(
        currentPaths: List<List<Point>>,
        visited: Set<Point>,
        stepValidator: (Int, Int) -> Boolean,
        stopCondition: (Point) -> Boolean
    ): List<List<Point>> {
        if (currentPaths.all { stopCondition(it.last()) }) {
            return currentPaths
        }

        val newPaths = currentPaths.flatMap { path -> takeAllSteps(path, visited, stepValidator, stopCondition) }
        val prunedPaths = newPaths.distinctBy { it.last() }
        val pointsVisited = prunedPaths.map { it.last() }
        return explorePaths(prunedPaths, visited + pointsVisited, stepValidator, stopCondition)
    }

    private fun takeAllSteps(
        path: List<Point>,
        visited: Set<Point>,
        stepValidator: (Int, Int) -> Boolean,
        stopCondition: (Point) -> Boolean
    ): List<List<Point>> {
        val currentPoint = path.last()
        if (stopCondition(currentPoint)) {
            return listOf(path)
        }

        val currentElevation = grid.getValue(currentPoint)
        val validNeighbours = grid.neighbours(currentPoint).filter { neighbour ->
            !visited.contains(neighbour) && stepValidator(currentElevation, grid.getValue(neighbour))
        }
        return validNeighbours.map { path + it }
    }
}