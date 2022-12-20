val solvers = listOf(
    Day1(),
//    Day2(),
//    Day3(),
//    Day4(),
//    Day5(),
//    Day6(),
//    Day7(),
//    Day8(),
//    Day9(),
//    Day10(),
//    Day11(),
//    Day12(),
//    Day13(),
//    Day14(),
//    Day15(),
//    Day16(),
//    Day17(),
//    Day18(),
//    Day19(),
    Day20()
)

const val RESET_COLOR = "\u001b[0m"
const val ITALIC = "\u001b[3m"
const val RED = "\u001b[31m"
const val GREEN = "\u001b[32m"
const val AMBER = "\u001b[33m"


fun main() {
    solvers.forEach { solver ->
        timedSolution(solver, "A") { solver.partA() }
        timedSolution(solver, "B") { solver.partB() }
        println()
    }
}

private fun timedSolution(solver: Solver, part: String, solverFn: Solver.() -> Any?) {
    val timer = DurationTimer()
    val result = solverFn(solver)
    val duration = timer.duration()
    println("$ITALIC${colour(duration)}(${duration}ms)$RESET_COLOR ${solver.day}$part: $result")
}

private fun colour(duration: Long): String = if (duration < 500) GREEN else if (duration < 5000) AMBER else RED