class Day6(mode: SolverMode) : Solver(6, mode) {
    private val input: String = readString(filename)

    override fun partA() = findFirstPacket(4)

    override fun partB() = findFirstPacket(14)

    private fun findFirstPacket(size: Int) = input
        .toCharArray().toList()
        .windowed(size)
        .indexOfFirst { it.distinct().size == size } + size
}