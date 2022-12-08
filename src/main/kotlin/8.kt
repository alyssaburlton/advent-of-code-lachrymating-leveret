class Day8 : Solver {
    override val day = 8

    private val input = readStringGrid("8").transformValues(String::toInt)

    override fun partA(): Any {
        return input.keys.count { pt ->
            val height = input.getValue(pt)
            val allKeys = input.keys
            val rightPts = allKeys.filter { it.y == pt.y && it.x > pt.x }
            val leftPts = allKeys.filter { it.y == pt.y && it.x < pt.x }
            val upPts = allKeys.filter { it.y < pt.y && it.x == pt.x }
            val downPts = allKeys.filter { it.y > pt.y && it.x == pt.x }

            val visible = rightPts.none { input.getValue(it) >= height } ||
                    leftPts.none { input.getValue(it) >= height } ||
                    upPts.none { input.getValue(it) >= height } ||
                    downPts.none { input.getValue(it) >= height }
            visible
        }
    }

    override fun partB(): Any {
        return input.keys.maxOf { pt ->
            val height = input.getValue(pt)
            val allKeys = input.keys
            val rightPts = allKeys.filter { it.y == pt.y && it.x > pt.x }.sortedBy { it.x }
            val leftPts = allKeys.filter { it.y == pt.y && it.x < pt.x }.sortedByDescending { it.x }
            val upPts = allKeys.filter { it.y < pt.y && it.x == pt.x }.sortedByDescending { it.y }
            val downPts = allKeys.filter { it.y > pt.y && it.x == pt.x }.sortedBy { it.y }

            val visible =
                scenicScore(height, rightPts) * scenicScore(height, leftPts) * scenicScore(height, upPts) * scenicScore(
                    height,
                    downPts
                )
            visible
        }
    }

    private fun scenicScore(myHeight: Int, directionTrees: List<Point>): Int {
        val ix = directionTrees.indexOfFirst { input.getValue(it) >= myHeight }
        return if (ix == -1) directionTrees.size else ix + 1
    }
}