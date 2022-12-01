class Day1 : Solver {
    override val day = 1

    private val input = readIntegerGroupedList("1")

    override fun partA() = input.maxOf { it.sum() }

    override fun partB(): Int {
        val sorted = input.sortedByDescending { it.sum() }
        return sorted.subList(0, 3).sumOf { it.sum() }
    }
}