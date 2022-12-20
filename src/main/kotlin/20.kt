class Day20 : Solver {
    override val day = 20

    private data class Number(val originalIx: Int, val value: Long)

    private val input = readIntegerList("20").map(Int::toLong).mapIndexed(::Number)
    private val decryptionKey = 811589153

    override fun partA() = calculateGroveScore(mixList(input))

    override fun partB(): Any {
        val newList = input.map { Number(it.originalIx, it.value * decryptionKey) }

        return (1..10).fold(newList) { list, _ ->
            mixList(list)
        }.let(::calculateGroveScore)
    }

    private fun mixList(list: List<Number>) =
        list.indices.fold(list) { currentList, originalIx ->
            val currentIx = currentList.indexOfFirst { it.originalIx == originalIx }
            val element = currentList[currentIx]
            val newIx = (currentIx + element.value).mod(input.size - 1)

            val without = currentList.subList(0, currentIx) + currentList.subList(currentIx + 1, currentList.size)
            without.subList(0, newIx) + element + without.subList(newIx, without.size)
        }

    private fun calculateGroveScore(decryptionResult: List<Number>): Long {
        val decryptedFile = decryptionResult.map { it.value }
        val zeroIndex = decryptedFile.indexOf(0L)
        return listOf(1000, 2000, 3000).sumOf { decryptedFile[(zeroIndex + it) % decryptedFile.size] }
    }
}