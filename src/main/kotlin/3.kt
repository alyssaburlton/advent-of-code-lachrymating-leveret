class Day3 : Solver {
    override val day = 3

    private val input = readStringList("3")
    private val itemsByPriority = (('a'..'z') + ('A'..'Z')).toList()

    override fun partA() = input.map(::findItemInBothCompartments).sum()

    override fun partB() = input.chunked(3).map(::findGroupItem).sum()

    private fun findGroupItem(group: List<String>): Int {
        val sets = group.map(::rucksackToLetterSet)
        val intersection = sets.fold(sets.first()) { acc, strings ->
            acc.intersect(strings)
        }

        return getScore(intersection)
    }

    private fun findItemInBothCompartments(rucksack: String): Int {
        val length = rucksack.length
        val firstHalf = rucksackToLetterSet(rucksack.substring(0, length / 2))
        val secondHalf = rucksackToLetterSet(rucksack.substring(length / 2, length))

        val intersection = firstHalf.intersect(secondHalf)
        return getScore(intersection)
    }

    private fun getScore(rucksackIntersection: Set<String>): Int {
        if (rucksackIntersection.size > 1) {
            throw Error("Ooops")
        }

        val character = rucksackIntersection.first().toCharArray()[0]
        return itemsByPriority.indexOf(character) + 1
    }

    private fun rucksackToLetterSet(rucksack: String) = rucksack.split("").filter { it.isNotEmpty() }.toSet()
}