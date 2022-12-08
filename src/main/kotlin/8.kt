class Day8 : Solver {
    override val day = 8

    private val input = readStringGrid("8").transformValues(String::toInt)

    override fun partA() = input.keys.count { pt ->
        val height = input.getValue(pt)

        val lines = getOrderedTreeLines(pt)
        lines.any { line ->
            line.all { it < height }
        }
    }

    override fun partB() = input.keys.maxOf { pt ->
        val height = input.getValue(pt)
        val lines = getOrderedTreeLines(pt)
        lines.productOf { scenicScore(height, it) }
    }

    private fun getOrderedTreeLines(point: Point) = listOf(
        ((point.x + 1)..input.xMax()).map { input.getValue(Point(it, point.y)) },
        (input.xMin() until point.x).map { input.getValue(Point(it, point.y)) }.reversed(),
        ((point.y + 1)..input.yMax()).map { input.getValue(Point(point.x, it)) },
        (input.yMin() until point.y).map { input.getValue(Point(point.x, it)) }.reversed()
    )

    private fun scenicScore(myHeight: Int, directionTrees: List<Int>): Int {
        val ix = directionTrees.indexOfFirst { it >= myHeight }
        return if (ix == -1) directionTrees.size else ix + 1
    }
}