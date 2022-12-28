data class CpuState(
    val pendingInstructions: List<Int>,
    val register: Int,
    val cycle: Int,
    val pixels: List<String>
)

class Day10(mode: SolverMode) : Solver(10, mode) {
    private val input = readStringList(filename)
    private val programLength = initialCpuState().pendingInstructions.size

    override fun partA() =
        processCpuStates()
            .filter { (20..programLength step 40).contains(it.cycle) }
            .sumOf { it.cycle * it.register }

    override fun partB() = processCpuStates().last().getPixelImage()

    private fun parseInstruction(instruction: String) =
        if (instruction.startsWith("addx")) {
            listOf(0, instruction.split(" ")[1].toInt())
        } else listOf(0)

    private fun processCpuStates() =
        (0 until programLength).runningFold(initialCpuState()) { cpuState, _ -> cpuState.doTick() }

    private fun initialCpuState() = CpuState(input.flatMap(::parseInstruction), 1, 1, emptyList())

    private fun CpuState.getPixelImage() = pixels.chunked(40).joinToString("\n", "\n") { it.joinToString("") }

    private fun CpuState.doTick(): CpuState {
        val newPixel = if ((register - 1..register + 1).contains((cycle - 1) % 40)) "#" else " "

        return CpuState(
            pendingInstructions.drop(1),
            register + pendingInstructions.first(),
            cycle + 1,
            pixels + newPixel
        )
    }
}