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
        if (itemOne == itemTwo) {
            return 0
        }

        itemOne ?: return -1
        itemTwo ?: return 1

        return when (itemOne) {
            is Int -> itemOne.compareInPacket(itemTwo)
            is List<*> -> itemOne.compareInPacket(itemTwo)
            else -> throw Error("Unexpected type: $itemOne")
        }
    }

    private fun Int.compareInPacket(other: Any) =
        if (other is Int) compareTo(other) else listOf(this).compareInPacket(other)

    private fun List<*>.compareInPacket(other: Any): Int {
        if (other !is List<*>) return compareInPacket(listOf(other))

        val maxSize = max(size, other.size)
        val first = padWith(maxSize, null)
        val second = other.padWith(maxSize, null)
        return first.zip(second).map { compare(it.first, it.second) }.firstOrNull { it != 0 } ?: 0
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