data class CpuState(
    val queuedInstructions: List<Int?>,
    val pendingInstructions: List<Int?>,
    val register: Int,
    val cycle: Int,
    val processingAdd: Boolean,
    val pixels: List<String>
)

class Day10 : Solver {
    override val day = 10

    private val input = readStringList("10")
    private val programLength = 2 * input.count { it.startsWith("addx") } + input.count { it.startsWith("noop") }

    override fun partA() =
        processCpuStates()
            .filter { (20..programLength step 40).contains(it.cycle) }
            .sumOf { it.cycle * it.register }

    override fun partB() = processCpuStates().last().getPixelImage()

    private fun parseInstruction(instruction: String) =
        if (instruction.startsWith("addx")) {
            instruction.split(" ")[1].toInt()
        } else null

    private fun processCpuStates() =
        (0 until programLength).runningFold(initialCpuState()) { cpuState, _ -> cpuState.doTick() }

    private fun initialCpuState() = CpuState(emptyList(), input.map(::parseInstruction), 1, 1, false, emptyList())

    private fun CpuState.getPixelImage() = pixels.chunked(40).joinToString("\n", "\n") { it.joinToString("") }

    private fun CpuState.doTick(): CpuState {
        val newInstruction = pendingInstructions.firstOrNull()

        val queuedInstruction = queuedInstructions.firstOrNull()
        val newProcessingAdd = queuedInstruction != null && !processingAdd

        val remainingQueuedInstructions = if (!newProcessingAdd) queuedInstructions.drop(1) else queuedInstructions

        val newRegister = if (newProcessingAdd) {
            register + queuedInstruction!!
        } else register

        val newPixel = if ((register - 1..register + 1).contains((cycle - 1) % 40)) {
            "#"
        } else {
            "."
        }

        return CpuState(
            remainingQueuedInstructions + newInstruction,
            pendingInstructions.drop(1),
            newRegister,
            cycle + 1,
            newProcessingAdd,
            pixels + newPixel
        )
    }
}