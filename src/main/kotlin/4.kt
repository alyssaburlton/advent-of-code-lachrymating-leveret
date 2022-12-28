class Day4(mode: SolverMode) : Solver(4, mode) {
    private val sectionAssignments = readStringList(filename).map(::parseSectionAssignments)

    override fun partA() = sectionAssignments.count { (elfOne, elfTwo) ->
        elfOne.containsAll(elfTwo) || elfTwo.containsAll(elfOne)
    }

    override fun partB() = sectionAssignments.count { (elfOne, elfTwo) ->
        elfOne.intersect(elfTwo).isNotEmpty()
    }

    private fun parseSectionAssignments(pairLine: String) =
        pairLine.split(",").map(::parseSectionAssignment)

    private fun parseSectionAssignment(str: String): Set<Int> {
        val (start, end) = str.split("-").map(String::toInt)
        return (start..end).toSet()
    }
}