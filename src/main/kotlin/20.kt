class Day20 : Solver {
    override val day = 20


    private data class Number(val originalIx: Int, val value: Long)

    private val input = readIntegerList("20").map(Int::toLong).mapIndexed(::Number)
    private val decryptionKey = 811589153

    override fun partA(): Any {
        val newList = input.toMutableList()
        mixList(newList)
        return calculateGroveScore(newList)
    }

    override fun partB(): Any {
        val newList = input.map { Number(it.originalIx, it.value * decryptionKey) }.toMutableList()

        repeat(10) {
            mixList(newList)
        }

        return calculateGroveScore(newList)
    }

    private fun mixList(list: MutableList<Number>) {
        list.indices.forEach { originalIx ->
            val element = list.first { it.originalIx == originalIx }
            val currentIx = list.indexOf(element)
            val newIx = (currentIx + element.value).mod(input.size - 1)

            list.removeAt(currentIx)
            list.add(newIx, element)
        }
    }

    private fun calculateGroveScore(decryptionResult: List<Number>): Long {
        val decryptedFile = decryptionResult.map { it.value }
        val zeroIndex = decryptedFile.indexOf(0L)
        return listOf(1000, 2000, 3000).sumOf { decryptedFile[(zeroIndex + it) % decryptedFile.size] }
    }
}