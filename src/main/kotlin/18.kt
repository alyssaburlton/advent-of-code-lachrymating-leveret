class Day18 : Solver {
    override val day = 18

    private val input = readStringList("18").map(::parsePoint).toSet()

    private val xMax = input.maxOf(Point3D::x) + 1
    private val xMin = input.minOf(Point3D::x) - 1
    private val yMax = input.maxOf(Point3D::y) + 1
    private val yMin = input.minOf(Point3D::y) - 1
    private val zMax = input.maxOf(Point3D::z) + 1
    private val zMin = input.minOf(Point3D::z) - 1

    override fun partA() = getEmptyAdjacents().size

    override fun partB(): Any {
        val map = getEmptyAdjacents().fold(mapOf<Point3D, Boolean>()) { map, pt ->
            checkWhatCanReachOutside(
                listOf(pt),
                map
            )
        }

        return getEmptyAdjacents().filter { map.getValue(it) }.size
    }

    private fun getEmptyAdjacents() = input.flatMap {
        it.neighbours()
    } - input

    private tailrec fun checkWhatCanReachOutside(
        currentSpots: List<Point3D>,
        knownValues: Map<Point3D, Boolean>,
        spotsVisited: Set<Point3D> = emptySet()
    ): Map<Point3D, Boolean> {
        if (currentSpots.isEmpty()) {
            val newValues = spotsVisited.map { it to false }
            return knownValues + newValues
        }

        if (currentSpots.any(::outOfBounds)) {
            val newValues = (spotsVisited + currentSpots).map { it to true }
            return knownValues + newValues
        }

        val known = currentSpots.firstNotNullOfOrNull { knownValues[it] }
        if (known != null) {
            val newValues = (spotsVisited - knownValues.keys).map { it to known }
            return knownValues + newValues
        }

        val nextStep = currentSpots.flatMap { it.neighbours() }.distinct().filterNot {
            input.contains(it) || spotsVisited.contains(it)
        }

        return checkWhatCanReachOutside(nextStep, knownValues, spotsVisited + currentSpots)
    }

    private fun outOfBounds(point: Point3D) =
        point.x < xMin || point.x > xMax || point.y < yMin || point.y > yMax || point.z < zMin || point.z > zMax

    private fun parsePoint(pointStr: String): Point3D {
        val split = pointStr.split(",")
        return Point3D(split[0].toInt(), split[1].toInt(), split[2].toInt())
    }
}