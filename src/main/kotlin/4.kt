class Day4 : Solver {
    override val day = 4

    private val input = readStringList("4")

    override fun partA() = input.count { pairLine ->
        val (elfOne, elfTwo) = parseSectionAssignments(pairLine)
        elfOne.containsAll(elfTwo) || elfTwo.containsAll(elfOne)
    }

    override fun partB() = input.count { pairLine ->
        val (elfOne, elfTwo) = parseSectionAssignments(pairLine)
        elfOne.intersect(elfTwo).isNotEmpty()
    }

    private fun parseSectionAssignments(pairLine: String) =
        pairLine.split(",").map(::parseSectionAssignment)

    private fun parseSectionAssignment(str: String): Set<Int> {
        val (start, end) = str.split("-").map(String::toInt)
        return (start..end).toSet()
    }
}