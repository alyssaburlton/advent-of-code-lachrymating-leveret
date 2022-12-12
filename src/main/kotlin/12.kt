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

    // Pre-cache valid neighbours up front
    private val cachedNeighbours = grid.map.keys.associateWith { pt ->
        grid.neighbours(pt).filter { neighbour ->
            val newElevation = grid.getValue(neighbour)
            newElevation <= grid.getValue(pt) + 1
        }
    }

    override fun partA() = getMinimumSteps(myPosition)

    override fun partB() = getPotentialStartingPoints()
        .minOf(::getMinimumSteps)

    /**
     * Eliminate any point whose only neighbours are either others of elevation 0, or impossible to move to
     */
    private fun getPotentialStartingPoints() = grid.map.entries
        .filter { it.value == 0 }
        .map { it.key }
        .filterNot { pt ->
            cachedNeighbours.getValue(pt).all { neighbour ->
                grid.getValue(neighbour) == 0
            }
        }

    private fun getMinimumSteps(startingPosition: Point): Int {
        val startingPaths = listOf(listOf(startingPosition))
        val result = explorePaths(startingPaths, mutableSetOf(startingPosition))
        return result.minOf { it.size - 1 }
    }

    private fun explorePaths(
        currentPaths: List<List<Point>>,
        visited: MutableSet<Point>
    ): List<List<Point>> {
        if (currentPaths.all { it.last() == desiredPosition }) {
            return currentPaths
        }

        val newPaths = currentPaths.flatMap { path -> takeAllSteps(path, visited) }
        val prunedPaths = newPaths.distinctBy { it.last() }
        return explorePaths(prunedPaths, visited)
    }

    private fun takeAllSteps(
        path: List<Point>,
        visited: MutableSet<Point>
    ): List<List<Point>> {
        val currentPoint = path.last()
        if (currentPoint == desiredPosition) {
            return listOf(path)
        }

        val validNeighbours = cachedNeighbours.getValue(currentPoint).filterNot(visited::contains)
        visited.addAll(validNeighbours)
        return validNeighbours.map { path + it }
    }
}