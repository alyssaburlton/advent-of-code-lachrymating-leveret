class Day20Fast(mode: SolverMode) : Solver(20, mode) {
    private data class Number(val originalIx: Int, val value: Long)

    private val input = readIntegerList(filename).map(Int::toLong).mapIndexed(::Number)
    private val decryptionKey = 811589153

    override fun partA() = calculateGroveScore(mixList(input))

    override fun partB(): Any {
        val newList = input.map { Number(it.originalIx, it.value * decryptionKey) }

        return (1..10).fold(newList) { list, _ ->
            mixList(list)
        }.let(::calculateGroveScore)
    }

    private fun mixList(list: List<Number>): List<Number> {
        val result = list.toMutableList()
        list.indices.forEach { originalIx ->
            val currentIx = result.indexOfFirst { it.originalIx == originalIx }
            val element = result.removeAt(currentIx)
            val newIx = (currentIx + element.value).mod(input.size - 1)
            result.add(newIx, element)
        }

        return result.toList()
    }

    private fun calculateGroveScore(decryptionResult: List<Number>): Long {
        val decryptedFile = decryptionResult.map { it.value }
        val zeroIndex = decryptedFile.indexOf(0L)
        return listOf(1000, 2000, 3000).sumOf { decryptedFile[(zeroIndex + it) % decryptedFile.size] }
    }
}