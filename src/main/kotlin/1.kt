class Day1(mode: SolverMode) : Solver(1, mode) {
    private val input = readIntegerGroupedList(filename)

    override fun partA() = input.maxOf { it.sum() }

    override fun partB(): Any {
        val sorted = input.sortedByDescending { it.sum() }
        return sorted.subList(0, 3).sumOf { it.sum() }
    }
}