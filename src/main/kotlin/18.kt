class Day18 : Solver {
    override val day = 18

    private val input = readStringList("18").map(::parsePoint).toSet()

    private val xMax = input.maxOf(Point3D::x) + 1
    private val xMin = input.minOf(Point3D::x) - 1
    private val yMax = input.maxOf(Point3D::y) + 1
    private val yMin = input.minOf(Point3D::y) - 1
    private val zMax = input.maxOf(Point3D::z) + 1
    private val zMin = input.minOf(Point3D::z) - 1

    private val canReachOutsideMemo = mutableMapOf<Point3D, Boolean>()

    override fun partA() = getEmptyAdjacents().size

    override fun partB() = getEmptyAdjacents().filter { canReachOutside(listOf(it)) }.size

    private fun getEmptyAdjacents() = input.flatMap {
        it.neighbours()
    } - input

    private fun canReachOutside(
        currentSpots: List<Point3D>,
        spotsVisited: Set<Point3D> = emptySet()
    ): Boolean {
        val nextStep = currentSpots.flatMap { it.neighbours() }.distinct().filterNot {
            input.contains(it) || spotsVisited.contains(it)
        }

        val known = nextStep.firstNotNullOfOrNull { canReachOutsideMemo[it] }
        if (known != null) {
            return known
        }

        if (nextStep.isEmpty()) {
            val newValues = spotsVisited.map { it to false }
            canReachOutsideMemo.putAll(newValues)
            return false
        }
        if (nextStep.any(::outOfBounds)) {
            val newValues = spotsVisited.map { it to true }
            canReachOutsideMemo.putAll(newValues)
            return true
        }

        return canReachOutside(nextStep, spotsVisited + currentSpots)
    }

    private fun outOfBounds(point: Point3D) =
        point.x < xMin || point.x > xMax || point.y < yMin || point.y > yMax || point.z < zMin || point.z > zMax

    private fun parsePoint(pointStr: String): Point3D {
        val split = pointStr.split(",")
        return Point3D(split[0].toInt(), split[1].toInt(), split[2].toInt())
    }
}