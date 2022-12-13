import kotlin.math.max

class Day13 : Solver {
    override val day = 13

    private val input = readGroupedList("13").map { pair -> pair.map(::parsePacket) }
    private val dividerPackets = listOf("[[2]]", "[[6]]").map(::parsePacket)

    override fun partA() = input.mapIndexed { index, packet ->
        if (inCorrectOrder(packet)) index + 1 else 0
    }.sum()

    override fun partB(): Any {
        val newInput = input.flatten() + dividerPackets
        val sorted = newInput.sortedWith(::compare)
        return dividerPackets.map { sorted.indexOf(it) + 1 }.product()
    }

    fun inCorrectOrder(inputPair: List<Any>) = compare(inputPair[0], inputPair[1]) == -1

    private fun compare(itemOne: Any?, itemTwo: Any?): Int {
        if (itemOne == null && itemTwo == null) {
            return 0
        }

        if (itemOne == null) {
            return -1
        }

        if (itemTwo == null) {
            return 1
        }

        if (itemOne is Int && itemTwo is Int) {
            return itemOne.compareTo(itemTwo)
        }

        if (itemOne is List<*> && itemTwo is List<*>) {
            val maxSize = max(itemOne.size, itemTwo.size)
            val first = itemOne.padWith(maxSize, null)
            val second = itemTwo.padWith(maxSize, null)

            val itemComparisons = first.zip(second).map { compare(it.first, it.second) }.filterNot { it == 0 }
            return itemComparisons.firstOrNull() ?: 0
        }

        if (itemOne is Int) {
            return compare(listOf(itemOne), itemTwo)
        }

        return compare(itemOne, listOf(itemTwo))
    }

    fun parsePacket(packetString: String) = parsePacket(packetString, 0)

    private fun parsePacket(packetString: String, nesting: Int): Any {
        if (packetString.isEmpty()) {
            return emptyList<Any>()
        }

        val intValue = packetString.toIntOrNull()
        if (intValue != null) {
            return intValue
        }

        val topLevelCommas = findTopLevelCommas(packetString)
        if (topLevelCommas.isNotEmpty()) {
            return (topLevelCommas + packetString.length).mapIndexed { commaNumber, commaIx ->
                val previousCommaIx = topLevelCommas.getOrNull(commaNumber - 1) ?: -1
                parsePacket(packetString.substring(previousCommaIx + 1, commaIx))
            }
        }

        val substring = packetString.substring(1, packetString.length - 1)
        val result = parsePacket(substring, nesting + 1)

        return if (nesting > 0 && result is Int) listOf(listOf(result)) else if (nesting > 0 || result is Int) listOf(
            result
        ) else result
    }

    private fun findTopLevelCommas(str: String) =
        str.foldIndexed(Pair(emptyList<Int>(), 0)) { ix, (commaIndices, nesting), character ->
            if (character == '[') {
                commaIndices to nesting + 1
            } else if (character == ']') {
                commaIndices to nesting - 1
            } else if (character == ',' && nesting == 0) {
                (commaIndices + ix) to 0
            } else {
                commaIndices to nesting
            }
        }.first
}