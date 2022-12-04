class Day4 : Solver {
    override val day = 4

    private val input = readStringList("4")

    override fun partA(): Int {
        val ranges = input.count { pairLine ->
            val xAndY = pairLine.split(",")
            val x = xAndY[0].split("-").map { it.toInt() }
            val y = xAndY[1].split("-").map { it.toInt() }

            val xList = (x[0]..x[1]).toSet()
            val yList = (y[0]..y[1]).toSet()

            xList.containsAll(yList) || yList.containsAll(xList)
        }

        return ranges
    }

    override fun partB(): Int {
        val ranges = input.count { pairLine ->
            val xAndY = pairLine.split(",")
            val x = xAndY[0].split("-").map { it.toInt() }
            val y = xAndY[1].split("-").map { it.toInt() }

            val xList = (x[0]..x[1]).toSet()
            val yList = (y[0]..y[1]).toSet()

            xList.intersect(yList).isNotEmpty()
        }

        return ranges
    }
}