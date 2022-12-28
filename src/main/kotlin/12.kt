class Day12(mode: SolverMode) : Solver(12, mode) {
    private val input = readStringGrid(filename)
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
    ) = explorePaths(
        listOf(startingPosition),
        setOf(startingPosition),
        stepValidator,
        stopCondition
    )

    private fun explorePaths(
        currentPoints: List<Point>,
        visited: Set<Point>,
        stepValidator: (Int, Int) -> Boolean,
        stopCondition: (Point) -> Boolean,
        steps: Int = 0
    ): Int {
        if (currentPoints.any { stopCondition(it) }) {
            return steps
        }

        val newPoints = currentPoints.flatMap { point -> takeAllSteps(point, visited, stepValidator) }.distinct()
        return explorePaths(newPoints, visited + newPoints, stepValidator, stopCondition, steps + 1)
    }

    private fun takeAllSteps(
        point: Point,
        visited: Set<Point>,
        stepValidator: (Int, Int) -> Boolean,
    ) = grid.neighbours(point)
        .filter { neighbour ->
            !visited.contains(neighbour) && stepValidator(grid.getValue(point), grid.getValue(neighbour))
        }
}