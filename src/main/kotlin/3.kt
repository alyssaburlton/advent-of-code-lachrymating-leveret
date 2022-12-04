class Day3 : Solver {
    override val day = 3

    private val input = readStringList("3")
    private val itemsByPriority = (('a'..'z') + ('A'..'Z')).toList()

    override fun partA() = input.map(::findItemInBothCompartments).sum()

    override fun partB() = input.chunked(3).map(::findGroupItem).sum()

    private fun findItemInBothCompartments(rucksack: String): Int {
        val (firstHalf, secondHalf) = rucksack.chunked(rucksack.length / 2).map(::rucksackToLetterSet)
        val intersection = firstHalf.intersect(secondHalf)
        return getScore(intersection)
    }

    private fun findGroupItem(group: List<String>): Int {
        val sets = group.map(::rucksackToLetterSet)
        val intersection = sets.fold(sets.first()) { acc, strings ->
            acc.intersect(strings)
        }

        return getScore(intersection)
    }

    private fun getScore(rucksackIntersection: Set<Char>): Int {
        val character = rucksackIntersection.only()
        return itemsByPriority.indexOf(character) + 1
    }

    private fun rucksackToLetterSet(rucksack: String) = rucksack.toCharArray().toSet()
}