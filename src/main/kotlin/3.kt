class Day3 : Solver {
    override val day = 3

    private val input = readStringList("3")

    override fun partA() = input.map(::findItemInBothCompartments).sum()

    override fun partB(): Int {
        val groups = input.chunked(3)
        val scores = groups.map(::findGroupItem)
        return scores.sum()
    }

    private fun findGroupItem(group: List<String>): Int {
        val sets = group.map(::rucksackToLetterSet)
        val intersection = sets[0].intersect(sets[1]).intersect(sets[2])
        if (intersection.size > 1) {
            throw Error("Ooops")
        }

        return getScore(intersection)
    }

    private fun findItemInBothCompartments(rucksack: String): Int {
        val length = rucksack.length
        val firstHalf = rucksack.substring(0, length / 2).split("").toSet().filter { it.isNotEmpty() }
        val secondHalf = rucksack.substring(length / 2, length).split("").toSet().filter { it.isNotEmpty() }

        val intersection = firstHalf.intersect(secondHalf)
        if (intersection.size > 1) {
            throw Error("Ooops")
        }

        val character = intersection.first().toCharArray()[0]
        if (character.isUpperCase()) {
            return character.code - 38
        } else {
            return character.code - 96
        }
    }

    private fun getScore(thing: Set<String>): Int {
        val character = thing.first().toCharArray()[0]
        if (character.isUpperCase()) {
            return character.code - 38
        } else {
            return character.code - 96
        }
    }

    private fun rucksackToLetterSet(rucksack: String) = rucksack.split("").filter { it.isNotEmpty() }.toSet()
}