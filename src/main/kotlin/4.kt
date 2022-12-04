class Day4 : Solver {
    override val day = 4

    private val input = readStringList("4")

    override fun partA() = input.count { pairLine ->
        val (xList, yList) = parseSectionAssignments(pairLine)
        xList.containsAll(yList) || yList.containsAll(xList)
    }

    override fun partB() = input.count { pairLine ->
        val (xList, yList) = parseSectionAssignments(pairLine)
        xList.intersect(yList).isNotEmpty()
    }

    private fun parseSectionAssignments(str: String): Pair<Set<Int>, Set<Int>> {
        val xAndY = str.split(",")
        val xList = parseSectionAssignment(xAndY[0])
        val yList = parseSectionAssignment(xAndY[1])
        return xList to yList
    }

    private fun parseSectionAssignment(str: String): Set<Int> {
        val (start, end) = str.split("-").map { it.toInt() }
        return (start..end).toSet()
    }
}