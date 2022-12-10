data class CpuState(
    val queuedInstructions: List<Int?>,
    val pendingInstructions: List<Int?>,
    val register: Int,
    val cycle: Int,
    val processingAdd: Boolean,
    val pixels: List<String>
)

const val PROGRAM_LENGTH = 240

class Day10 : Solver {
    override val day = 10

    private val input = readStringList("10")

    private fun initialCpuState() = CpuState(emptyList(), input.map(::parseInstruction), 1, 1, false, emptyList())

    override fun partA(): Any {
        val cpuStates =
            (0 until PROGRAM_LENGTH).runningFold(initialCpuState()) { cpuState, _ -> cpuState.doTick() }
        return cpuStates.filter { (20..PROGRAM_LENGTH step 40).contains(it.cycle) }.sumOf { it.cycle * it.register }
    }

    override fun partB() =
        (0 until PROGRAM_LENGTH).fold(initialCpuState()) { cpuState, _ -> cpuState.doTick() }.getPixelImage()

    private fun parseInstruction(instruction: String) =
        if (instruction.startsWith("addx")) {
            instruction.split(" ")[1].toInt()
        } else null

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