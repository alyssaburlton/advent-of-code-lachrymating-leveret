class Day8 : Solver {
    override val day = 8

    private val grid = readStringGrid("8").transformValues(String::toInt)

    override fun partA() = grid.entries.count { (pt, height) ->
        val lines = getOrderedTreeLines(pt)
        lines.any { line ->
            line.all { it < height }
        }
    }

    override fun partB() = grid.entries.maxOf { (pt, height) ->
        val lines = getOrderedTreeLines(pt)
        lines.productOf { scenicScore(height, it) }
    }

    private fun getOrderedTreeLines(point: Point): List<List<Int>> {
        val row = grid.rows[point.y]
        val col = grid.columns[point.x]

        return listOf(
            row.subList(point.x + 1, grid.xMax + 1),
            row.subList(0, point.x).reversed(),
            col.subList(point.y + 1, grid.yMax + 1),
            col.subList(0, point.y).reversed(),
        )
    }

    private fun scenicScore(myHeight: Int, directionTrees: List<Int>): Int {
        val ix = directionTrees.indexOfFirst { it >= myHeight }
        return if (ix == -1) directionTrees.size else ix + 1
    }
}