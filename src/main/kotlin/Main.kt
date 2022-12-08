val solvers = listOf(
    Day1(),
    Day2(),
    Day3(),
    Day4(),
    Day5(),
    Day6(),
    Day7(),
    Day8()
)

fun main() {
    solvers.forEach { solver ->
        val timerA = DurationTimer()
        val resultA = solver.partA()
        println("(${timerA.duration()}ms) ${solver.day}A: $resultA")

        val timerB = DurationTimer()
        val resultB = solver.partB()
        println("(${timerB.duration()}ms) ${solver.day}B: $resultB")
        println()
    }
}