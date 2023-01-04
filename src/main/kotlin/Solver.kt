enum class SolverMode {
    EXAMPLE,
    REAL,
    REAL_A
}

// if (mode == SolverMode.EXAMPLE) "${day}e" else "$day"

abstract class Solver(val day: Int, val mode: SolverMode) {
    val filename = when (mode) {
        SolverMode.EXAMPLE -> "${day}e"
        SolverMode.REAL -> "$day"
        SolverMode.REAL_A -> "${day}a"
    }

    abstract fun partA(): Any?
    abstract fun partB(): Any?
}