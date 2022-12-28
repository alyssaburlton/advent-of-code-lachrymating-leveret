class Day3(mode: SolverMode) : Solver(3, mode) {
    private val input = readStringList(filename)

    override fun partA() = input.map(::splitIntoCompartments).sumOf(this::intersectAndScore)

    override fun partB() = input.chunked(3).sumOf(this::intersectAndScore)

    private fun splitIntoCompartments(rucksack: String) = rucksack.chunked(rucksack.length / 2)

    private fun intersectAndScore(group: List<String>) =
        group
            .map(::rucksackToLetterSet)
            .reduce(Set<Char>::intersect)
            .only()
            .let(::scoreItem)

    private fun scoreItem(item: Char) = (('a'..'z') + ('A'..'Z')).indexOf(item) + 1

    private fun rucksackToLetterSet(rucksack: String) = rucksack.toCharArray().toSet()
}