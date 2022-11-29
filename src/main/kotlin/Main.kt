val solvers = listOf(
    Day1_2021()
)

fun main(args: Array<String>) {
    solvers.forEach { solver ->
        println("${solver.day}A: ${solver.partA()}")
        println("${solver.day}B: ${solver.partB()}")
        println()
    }
}