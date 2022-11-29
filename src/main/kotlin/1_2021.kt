class Day1_2021 : Solver {
    override val day = 1

    private val input = readIntegerList("1_2021")

    override fun partA() = countConsecutiveDecreases(input)

    override fun partB() = countConsecutiveDecreases(input.windowed(3).map { it.sum() })

    fun countConsecutiveDecreases(list: List<Int>) =
        list.windowed(2).count { it[1] > it[0] }
}