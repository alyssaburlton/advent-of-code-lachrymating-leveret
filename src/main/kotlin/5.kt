class Day5 : Solver {
    override val day = 5

    private val input = readGroupedList("5")
    private val stacks = parseStacks(input[0])
    private val instructions = input[1]

    override fun partA(): Any {
        val result = instructions.fold(stacks, ::processInstructionA)
        return result.values.map { it.last() }.joinToString("")
    }

    override fun partB(): Any {
        val result = instructions.fold(stacks, ::processInstructionB)
        return result.values.map { it.last() }.joinToString("")
    }

    private fun processInstructionA(stacks: Map<Int, List<Char>>, instruction: String) =
        processInstruction(stacks, instruction, false)

    private fun processInstructionB(stacks: Map<Int, List<Char>>, instruction: String) =
        processInstruction(stacks, instruction, true)

    private fun processInstruction(
        stacks: Map<Int, List<Char>>,
        instruction: String,
        reverse: Boolean
    ): Map<Int, List<Char>> {
        val parts = instruction.split(" ")
        val amountToMove = parts[1].toInt()
        val fromColIx = parts[3].toInt()
        val toColIx = parts[5].toInt()
        val fromCol = stacks[fromColIx]!!
        val toCol = stacks[toColIx]!!

        val items = fromCol.reversed().take(amountToMove)
        val thingsToMove = if (reverse) items.reversed() else items

        val newToCol = toCol + thingsToMove
        val newFromCol = fromCol.dropLast(thingsToMove.size)

        val newMap = stacks.toMutableMap()
        newMap[fromColIx] = newFromCol
        newMap[toColIx] = newToCol
        return newMap.toMap()
    }

    private fun parseStacks(rawGrid: List<String>): Map<Int, List<Char>> {
        val replaced: List<String> =
            rawGrid.map { it.replace("    ", "-").replace("]", "").replace("[", "").replace(" ", "") }

        val columnCount = replaced[0].length
        val rowCount = replaced.size - 1

        val columnMap = (0 until columnCount).associate { col ->
            val list = (0 until rowCount).map { row -> replaced[row][col] }.filter { it != '-' }.reversed()
            (col + 1) to list
        }

        return columnMap
    }


}