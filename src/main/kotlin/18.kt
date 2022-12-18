class Day18 : Solver {
    override val day = 18

    private val input = readStringList("18").map(::parsePoint)

    private val xMax = input.maxOf(Point3D::x) + 1
    private val xMin = input.minOf(Point3D::x) - 1
    private val yMax = input.maxOf(Point3D::y) + 1
    private val yMin = input.minOf(Point3D::y) - 1
    private val zMax = input.maxOf(Point3D::z) + 1
    private val zMin = input.minOf(Point3D::z) - 1

    override fun partA(): Any {
        val sides = input.flatMap(::toCube)
        val singleSides = sides.filter { side -> sides.count { it == side } == 1 }
        return singleSides.size
    }

    override fun partB(): Any {
        val allCubes = input.map(::toCube)
        val sides = allCubes.flatten()
        val singleSides: Set<Set<Point3D>> = sides.filter { side -> sides.count { it == side } == 1 }.toSet()
        println(singleSides.size)

        val extraSides = singleSides.flatMap {
            listOf(
                moveLeft(it),
                moveRight(it),
                moveIn(it),
                moveOut(it),
                moveDown(it),
                moveUp(it),
                it
            )
        }
        val cubesToMove =
            extraSides.flatten().toSet().map(::toCube).filterNot { allCubes.contains(it) }
        val newSides = cubesToMove.flatten()
        val singleSidesBeingConsidered = singleSides.filter { newSides.contains(it) }
        println("Considering ${singleSidesBeingConsidered.size}")


        val cubesThatCanReachOutside = cubesToMove.filter { canReachOutside(allCubes.toSet(), listOf(it)) }

        println("${cubesThatCanReachOutside.size} / ${cubesToMove.size} could reach outside")
        val sidesThatCanReachOutside = cubesThatCanReachOutside.flatten().toSet()
        return singleSides.intersect(sidesThatCanReachOutside).size
    }

    private fun canReachOutside(
        occupiedCubes: Set<Set<Set<Point3D>>>,
        currentCubes: List<Set<Set<Point3D>>>,
        placesVisited: Set<Set<Set<Point3D>>> = emptySet()
    ): Boolean {
        val nextStep = currentCubes.flatMap(::doAllMoves).distinct()
            .filterNot { occupiedCubes.contains(it) || placesVisited.contains(it) }
        if (nextStep.isEmpty()) return false // Nowhere left to go
        if (nextStep.any { !cubeInBounds(it) }) return true // Outside!

        return canReachOutside(occupiedCubes, nextStep, placesVisited + currentCubes)
    }

    private fun doAllMoves(cube: Set<Set<Point3D>>): List<Set<Set<Point3D>>> {
        return listOf(
            cube.map(::moveUp).toSet(),
            cube.map(::moveDown).toSet(),
            cube.map(::moveRight).toSet(),
            cube.map(::moveLeft).toSet(),
            cube.map(::moveOut).toSet(),
            cube.map(::moveIn).toSet(),
        )
    }

    private fun cubeInBounds(cube: Set<Set<Point3D>>): Boolean =
        cube.all(::inBounds)

    private fun inBounds(side: Set<Point3D>): Boolean {
        return side.none { it.x < xMin || it.x > xMax || it.y < yMin || it.y > yMax || it.z < zMin || it.z > zMax }
    }

    private fun moveUp(side: Set<Point3D>) =
        side.map { Point3D(it.x, it.y + 1, it.z) }.toSet()

    private fun moveDown(side: Set<Point3D>) =
        side.map { Point3D(it.x, it.y - 1, it.z) }.toSet()

    private fun moveRight(side: Set<Point3D>) =
        side.map { Point3D(it.x + 1, it.y, it.z) }.toSet()

    private fun moveLeft(side: Set<Point3D>) =
        side.map { Point3D(it.x - 1, it.y, it.z) }.toSet()

    private fun moveIn(side: Set<Point3D>) =
        side.map { Point3D(it.x, it.y, it.z + 1) }.toSet()

    private fun moveOut(side: Set<Point3D>) =
        side.map { Point3D(it.x, it.y, it.z - 1) }.toSet()

    private fun parsePoint(pointStr: String): Point3D {
        val split = pointStr.split(",")
        return Point3D(split[0].toInt(), split[1].toInt(), split[2].toInt())
    }

    /**
     * Call the point the top-left-front corner.
     */
    private fun toCube(pt: Point3D): Set<Set<Point3D>> {
        return setOf(
            frontFace(pt),
            topFace(pt),
            leftFace(pt),
            setOf(
                Point3D(pt.x + 1, pt.y, pt.z),
                Point3D(pt.x + 1, pt.y + 1, pt.z),
                Point3D(pt.x + 1, pt.y, pt.z + 1),
                Point3D(pt.x + 1, pt.y + 1, pt.z + 1)
            ), // Right face
            setOf(
                Point3D(pt.x, pt.y + 1, pt.z),
                Point3D(pt.x + 1, pt.y + 1, pt.z),
                Point3D(pt.x, pt.y + 1, pt.z + 1),
                Point3D(pt.x + 1, pt.y + 1, pt.z + 1),
            ), // Bottom face
            setOf(
                Point3D(pt.x, pt.y, pt.z + 1),
                Point3D(pt.x, pt.y + 1, pt.z + 1),
                Point3D(pt.x + 1, pt.y + 1, pt.z + 1),
                Point3D(pt.x + 1, pt.y, pt.z + 1)
            ), // Back face
        )
    }

    private fun frontFace(pt: Point3D): Set<Point3D> {
        val z = pt.z
        return setOf(
            Point3D(pt.x, pt.y, z),
            Point3D(pt.x, pt.y + 1, z),
            Point3D(pt.x + 1, pt.y + 1, z),
            Point3D(pt.x + 1, pt.y, z)
        )
    }

    private fun topFace(pt: Point3D): Set<Point3D> {
        val y = pt.y
        return setOf(
            Point3D(pt.x, y, pt.z),
            Point3D(pt.x + 1, y, pt.z),
            Point3D(pt.x, y, pt.z + 1),
            Point3D(pt.x + 1, y, pt.z + 1)
        )
    }

    private fun leftFace(pt: Point3D): Set<Point3D> {
        val x = pt.x
        return setOf(
            Point3D(x, pt.y, pt.z),
            Point3D(x, pt.y + 1, pt.z),
            Point3D(x, pt.y, pt.z + 1),
            Point3D(x, pt.y + 1, pt.z + 1)
        )
    }
}