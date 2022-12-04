class Day3 : Solver {
    override val day = 3

    private val input = readStringList("3")

    override fun partA() = input.map(::splitIntoCompartments).sumOf(this::scoreOverlappingItem)

    override fun partB() = input.chunked(3).sumOf(this::scoreOverlappingItem)

    private fun splitIntoCompartments(rucksack: String) = rucksack.chunked(rucksack.length / 2)

    private fun scoreOverlappingItem(group: List<String>) =
        group.map(::rucksackToLetterSet).let(this@Day3::intersectAndScore)

    private fun intersectAndScore(rucksacks: List<Set<Char>>) =
        rucksacks
            .fold(rucksacks.first(), Set<Char>::intersect)
            .only()
            .let(::scoreItem)

    private fun scoreItem(item: Char) = (('a'..'z') + ('A'..'Z')).indexOf(item) + 1

    private fun rucksackToLetterSet(rucksack: String) = rucksack.toCharArray().toSet()
}