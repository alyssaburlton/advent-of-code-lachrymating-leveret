class Day5 : Solver {
    override val day = 5

    private val input = readGroupedList("5")
    private val stacks = parseStacks(input[0])
    private val instructions = input[1]

    override fun partA() =
        instructions.fold(stacks, ::processInstructionA).let(::toTopCrateString)

    override fun partB() =
        instructions.fold(stacks, ::processInstructionB).let(::toTopCrateString)

    private fun toTopCrateString(stacks: List<List<String>>) =
        stacks.joinToString("") { it.last() }

    private fun processInstructionA(stacks: List<List<String>>, instruction: String) =
        processInstruction(stacks, instruction, true)

    private fun processInstructionB(stacks: List<List<String>>, instruction: String) =
        processInstruction(stacks, instruction, false)

    private fun processInstruction(
        stacks: List<List<String>>,
        instruction: String,
        reverse: Boolean
    ): List<List<String>> {
        val match = Regex("move (\\d+) from (\\d+) to (\\d+)").find(instruction)!!
        val (amountToMove, from, to) = match.destructured.toList().map(String::toInt)

        val items = stacks[from - 1].takeLast(amountToMove)
        val thingsToMove = if (reverse) items.reversed() else items

        return stacks.mapIndexed { index, stack ->
            when (index) {
                from - 1 -> stack.dropLast(thingsToMove.size)
                to - 1 -> stack + thingsToMove
                else -> stack
            }
        }
    }

    private fun parseStacks(rawGrid: List<String>) = parseGrid(rawGrid)
        .columns
        .filter { it.last().isNotBlank() }
        .map { column -> column.dropLast(1).filter { it.isNotBlank() }.reversed() }
}