class Day12 : Solver {
    override val day = 12

    private val input = readStringGrid("12")
    private val elevations = ('a'..'z').map { it.toString() }
    private val myPosition = input.entries.first { entry -> entry.value == "S" }.key
    private val desiredPosition = input.entries.first { entry -> entry.value == "E" }.key

    private val updatedMap = input.transformValues { value ->
        when (value) {
            "S" -> 0
            "E" -> 25
            else -> elevations.indexOf(value)
        }
    }

    override fun partA() = getMinimumSteps(myPosition)

    override fun partB(): Any {
        val startingPoints = updatedMap.map.entries.filter { it.value == 0 }.map { it.key }
        return startingPoints.minOf(::getMinimumSteps)
    }

    private fun getMinimumSteps(startingPosition: Point): Int {
        val ptToShortestRoute = mutableMapOf<Point, Int>()
        var currentPaths = listOf(listOf(startingPosition))
        while (currentPaths.any { it.last() != desiredPosition }) {
            currentPaths =
                currentPaths.flatMap { path -> takeAllSteps(desiredPosition, path, updatedMap, ptToShortestRoute) }

            currentPaths = currentPaths.distinctBy { it.last() }
        }

        return (currentPaths.minOfOrNull { it.size } ?: Int.MAX_VALUE) - 1
    }

    private fun takeAllSteps(
        desiredPoint: Point,
        path: List<Point>,
        map: Grid<Int>,
        ptToShortestRoute: MutableMap<Point, Int>
    ): List<List<Point>> {
        val currentPoint = path.last()
        if (currentPoint == desiredPoint) {
            return listOf(path)
        }

        val currentElevation = map.getValue(currentPoint)

        val validNeighbours = map.neighbours(currentPoint).filter { newPoint ->
            val newElevation = map.getValue(newPoint)
            newElevation <= currentElevation + 1
                    && (ptToShortestRoute[newPoint] ?: Int.MAX_VALUE) >= path.size + 1
        }

        validNeighbours.forEach {
            ptToShortestRoute[it] = path.size + 1
        }

        return validNeighbours.map { path + it }
    }
}