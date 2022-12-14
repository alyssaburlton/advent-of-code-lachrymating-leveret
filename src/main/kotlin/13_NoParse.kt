class Day13NoParse : Solver {
    override val day = 13

    private val input by lazy { parsePackets(readGroupedList("13").flatten()) }
    private val dividerPackets = listOf(listOf(listOf(2)), listOf(listOf(6)))

    override fun partA() = input.chunked(2).mapIndexed { index, packet ->
        if (Day13().inCorrectOrder(packet)) index + 1 else 0
    }.sum()

    override fun partB(): Any {
        val newInput = input + dividerPackets
        val sorted = newInput.sortedWith(Day13()::compare)
        return dividerPackets.map { sorted.indexOf(it) + 1 }.product()
    }

    private fun parsePackets(packetStrings: List<String>): List<List<Any>> {
        val kotlinStrings =
            packetStrings.map(::packetToKotlinString).mapIndexed { ix, listStr -> "private fun list$ix() = $listStr" }
                .joinToString("\n")

        val allListsString = packetStrings.indices.joinToString(",") { "list$it()" }

        val listClass = """class ListClass {
            $kotlinStrings
            fun allLists() = listOf($allListsString)
        }
        """.trimMargin()

        val clazz = injectDynamicClass("ListClass", listClass)
        val instance = clazz.getDeclaredConstructor().newInstance()
        return clazz.getMethod("allLists").invoke(instance) as List<List<Any>>
    }

    private fun packetToKotlinString(packetString: String) =
        packetString.replace("[", "listOf(").replace("]", ")").replace("listOf()", "listOf<Any>()")
}