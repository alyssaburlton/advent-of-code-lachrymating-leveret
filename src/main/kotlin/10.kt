class Day10 : Solver {
    override val day = 10

    private val input = readStringList("10")

    override fun partA(): Any {
        var register = 1
        var sum = 0

        var startedAddX = false

        val pendingInstructions = mutableListOf<Int?>()

        val programLength = 280

        val interestingCycles = (20..programLength step 40)

        (0 until programLength).forEach { ix ->
            val instruction = if (ix < input.size) input[ix] else null

            val cycle = ix + 1
            // println("Starting cycle $cycle, register = $register")

            // Start
            pendingInstructions.add(parseInstruction(instruction))



            // During
            // println("During cycle $cycle, instructions = $pendingInstructions")

            if (interestingCycles.contains(cycle)) {
                sum += (cycle * register)
                println("Cycle $cycle: $register = ${cycle * register}. Sum = $sum")
            }

            // End
            val pendingInstruction = pendingInstructions[0]
            if (pendingInstruction == null) {
                pendingInstructions.removeAt(0)
            } else if (startedAddX) {
                register += pendingInstruction
                pendingInstructions.removeAt(0)
                startedAddX = false
            } else {
                startedAddX = true
            }


            // println("Finished cycle $cycle, register = $register")
        }
        return ""
    }

    private fun parseInstruction(instruction: String?): Int? {
        if (instruction != null && instruction.startsWith("addx")) {
            return  instruction.split(" ")[1].toInt()
        }

        return null
    }

    override fun partB(): Any {
        var register = 1
        var sum = 0

        var startedAddX = false

        val pendingInstructions = mutableListOf<Int?>()
        val pixels = mutableListOf<String>()

        val programLength = 240

        val interestingCycles = (20..programLength step 40)

        (0 until programLength).forEach { ix ->
            val instruction = if (ix < input.size) input[ix] else null

            val cycle = ix + 1
            // println("Starting cycle $cycle, register = $register")

            // Start
            pendingInstructions.add(parseInstruction(instruction))



            // During
            // draw sprite

            if ((register-1..register+1).contains(ix % 40)) {
                pixels.add("#")
            } else {
                pixels.add(".")
            }

            if (interestingCycles.contains(cycle)) {
                sum += (cycle * register)
                println("Cycle $cycle: $register = ${cycle * register}. Sum = $sum")
            }

            // End
            val pendingInstruction = pendingInstructions[0]
            if (pendingInstruction == null) {
                pendingInstructions.removeAt(0)
            } else if (startedAddX) {
                register += pendingInstruction
                pendingInstructions.removeAt(0)
                startedAddX = false
            } else {
                startedAddX = true
            }


            // println("Finished cycle $cycle, register = $register")
        }

        pixels.chunked(40).map { it.joinToString("") }.forEach { println(it) }
        return ""
    }
}