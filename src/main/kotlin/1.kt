class Day1 : Solver {
    override val day = 1

    private val input = readGroupedList("1")

    override fun partA() = input.maxBy { it.sum() }.sum()

    override fun partB(): Int {
        val sorted = input.sortedByDescending { it.sum() }
        return sorted[0].sum() + sorted[1].sum() + sorted[2].sum()
    }
}