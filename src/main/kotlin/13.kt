import kotlin.math.abs

class Day13 : Solver {
    override val day = 13

    private val input = readGroupedList("13")

    override fun partA(): Any {
        return input.mapIndexed { index, strings ->
            if (inCorrectOrder(strings)) index + 1 else 0
        }.sum()
    }

    override fun partB(): Any {
        val newInput = (input.flatten() + "[[2]]" + "[[6]]").map(::parseItems)
        val sorted = newInput.sortedWith(::compare)
        println(sorted)

        return (1 + sorted.indexOf(listOf(listOf(2)))) * (1 + sorted.indexOf(listOf(listOf(6))))
    }

    fun inCorrectOrder(inputPair: List<String>): Boolean {
        val first = parseItems(inputPair[0])
        val second = parseItems(inputPair[1])

        return compare(first, second) == -1
    }

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

        // println("Compare $itemOne vs $itemTwo")
        if (itemOne is Int && itemTwo is Int) {
            return itemOne.compareTo(itemTwo)
        }

        if (itemOne is List<*> && itemTwo is List<*>) {
            val sizeDiff = itemOne.size - itemTwo.size
            val padding = List(abs(sizeDiff)) { null }

            val first = if (sizeDiff < 0) itemOne + padding else itemOne
            val second = if (sizeDiff > 0) itemTwo + padding else itemTwo

            if (first.size != second.size) {
                throw Error("I've screwed up padding")
            }

            val itemComparisons = first.zip(second).map { compare(it.first, it.second) }.filterNot { it == 0 }
            return itemComparisons.firstOrNull() ?: 0
        }

        if (itemOne is Int) {
            return compare(listOf(itemOne), itemTwo)
        }

        return compare(itemOne, listOf(itemTwo))
    }

    fun parseItems(packetString: String): Any {
        println("Parsing $packetString")
        return parseItems(packetString, 0)
    }

    private fun parseItems(packetString: String, nesting: Int): Any {
        // println("Parsing $packetString")
        val intValue = packetString.toIntOrNull()
        if (intValue != null) {
            // println("Integer found")
            return intValue
        }

        val topLevelCommas = mutableListOf<Int>()
        var currentNesting = 0
        packetString.forEachIndexed { ix, character ->
            if (character == '[') {
                currentNesting += 1
            } else if (character == ']') {
                currentNesting -= 1
            } else if (character == ',' && currentNesting == 0) {
                topLevelCommas.add(ix)
            }
        }

        if (topLevelCommas.isNotEmpty()) {
            // println("Splitting by commas")
            return (topLevelCommas + packetString.length).mapIndexed { commaNumber, commaIx ->
                val previousCommaIx = topLevelCommas.getOrNull(commaNumber - 1) ?: -1
                parseItems(packetString.substring(previousCommaIx + 1, commaIx))
            }
        }

        // println("Stripping outer brackets")
        val result = if (packetString == "[]") {
            emptyList<Any>()
        } else {
            val substring = packetString.substring(1, packetString.length - 1)
            parseItems(substring, nesting + 1)
        }

        // println("Got $result, nesting = $nesting")
        return if (nesting > 0 && result is Int) listOf(listOf(result)) else if (nesting > 0 || result is Int) listOf(
            result
        ) else result
    }
}