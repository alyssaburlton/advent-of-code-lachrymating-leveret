enum class SolverMode {
    EXAMPLE,
    REAL
}

abstract class Solver(val day: Int, val mode: SolverMode) {
    val filename = if (mode == SolverMode.EXAMPLE) "${day}e" else "$day"

    abstract fun partA(): Any?
    abstract fun partB(): Any?
}