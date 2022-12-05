class Day5 : Solver {
    override val day = 5

    private val input = readGroupedList("5")
    private val stacks = parseStacks(input[0])
    private val instructions = input[1]

    override fun partA(): Any {
        val result = instructions.fold(stacks, ::processInstructionA)
        return result.joinToString("") { it.last() }
    }

    override fun partB(): Any {
        val result = instructions.fold(stacks, ::processInstructionB)
        return result.joinToString("") { it.last() }
    }

    private fun processInstructionA(stacks: List<List<String>>, instruction: String) =
        processInstruction(stacks, instruction, false)

    private fun processInstructionB(stacks: List<List<String>>, instruction: String) =
        processInstruction(stacks, instruction, true)

    private fun processInstruction(
        stacks: List<List<String>>,
        instruction: String,
        reverse: Boolean
    ): List<List<String>> {
        val parts = instruction.split(" ")
        val amountToMove = parts[1].toInt()
        val fromColIx = parts[3].toInt() - 1
        val toColIx = parts[5].toInt() - 1
        val fromCol = stacks[fromColIx]
        val toCol = stacks[toColIx]

        val items = fromCol.reversed().take(amountToMove)
        val thingsToMove = if (reverse) items.reversed() else items

        val newToCol = toCol + thingsToMove
        val newFromCol = fromCol.dropLast(thingsToMove.size)

        val newMap = stacks.toMutableList()
        newMap[fromColIx] = newFromCol
        newMap[toColIx] = newToCol
        return newMap.toList()
    }

    private fun parseStacks(rawGrid: List<String>) = parseGrid(rawGrid)
        .columns()
        .filter { it.last().isNotBlank() }
        .map { column -> column.dropLast(1).filter { it.isNotBlank() }.reversed() }
}