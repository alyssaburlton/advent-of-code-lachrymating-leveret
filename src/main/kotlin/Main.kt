val mode = SolverMode.REAL

val solvers = listOf(
//    Day1(mode),
//    Day2(mode),
//    Day3(mode),
//    Day4(mode),
//    Day5(mode),
//    Day6(mode),
//    Day7(mode),
//    Day8(mode),
//    Day9(mode),
//    Day10(mode),
//    Day11(mode),
//    Day12(mode),
//    Day13(mode),
//    Day14(mode),
//    Day15(mode),
    Day16(mode),
//    Day17(mode),
//    Day18(mode),
//    Day19(mode),
//    Day20(mode),
//    Day21(mode),
//    Day22(mode),
//    Day23(mode),
//    Day24(mode),
//    Day25(mode)
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