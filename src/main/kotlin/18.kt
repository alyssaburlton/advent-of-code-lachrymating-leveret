class Day18 : Solver {
    override val day = 18

    private val input = readStringList("18").map(::parsePoint).toSet()

    private val xMax = input.maxOf(Point3D::x) + 1
    private val xMin = input.minOf(Point3D::x) - 1
    private val yMax = input.maxOf(Point3D::y) + 1
    private val yMin = input.minOf(Point3D::y) - 1
    private val zMax = input.maxOf(Point3D::z) + 1
    private val zMin = input.minOf(Point3D::z) - 1

    override fun partA() =
        (input.flatMap { it.neighbours() } - input).size

    override fun partB(): Any {
        val emptySpaces = input.flatMap {
            it.neighbours()
        } - input

        val outsideSpaces = emptySpaces.filter { canReachOutside(listOf(it)) }
        return outsideSpaces.size
    }

    private fun canReachOutside(
        currentSpots: List<Point3D>,
        spotsVisited: Set<Point3D> = emptySet()
    ): Boolean {
        val nextStep = currentSpots.flatMap { it.neighbours() }.distinct().filterNot {
            input.contains(it) || spotsVisited.contains(it)
        }

        if (nextStep.isEmpty()) return false
        if (nextStep.any(::outOfBounds)) return true

        return canReachOutside(nextStep, spotsVisited + currentSpots)
    }

    private fun outOfBounds(point: Point3D) =
        point.x < xMin || point.x > xMax || point.y < yMin || point.y > yMax || point.z < zMin || point.z > zMax

    private fun parsePoint(pointStr: String): Point3D {
        val split = pointStr.split(",")
        return Point3D(split[0].toInt(), split[1].toInt(), split[2].toInt())
    }
}