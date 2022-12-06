class Day6 : Solver {
    override val day = 6

    private val input: String = readString("6")

    override fun partA() = findFirstPacket(4)

    override fun partB() = findFirstPacket(14)

    private fun findFirstPacket(size: Int) = input
        .toCharArray().toList()
        .windowed(size)
        .indexOfFirst { it.distinct().size == size } + size
}