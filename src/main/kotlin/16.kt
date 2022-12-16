data class Valve(val room: String, val flowRate: Int)
data class MoveState(
    val myValve: PersonState,
    val elephantValve: PersonState?,
    val releasedValves: List<Pair<Int, Valve>>
)

data class PersonState(val currentValve: Valve, val prevValves: Set<Valve>)
data class MoveHash(val personStates: Set<PersonState?>, val released: Set<Pair<Int, Valve>>)

class Day16 : Solver {
    override val day = 16

    private val input = readStringList("16").map(::parseValves)
    private val valvesMap = getRoutedValvesMap(input)
    private val releasableValves = valvesMap.keys.filter { it.flowRate > 0 }

    override fun partA(): Any {
        val startingValve = valvesMap.keys.first { it.room == "AA" }
        val startingPerson = PersonState(startingValve, emptySet())
        val initialState = MoveState(startingPerson, null, emptyList())
        // return runSimulation(initialState, 30)
        return -1
    }

    override fun partB(): Any {
        val startingValve = valvesMap.keys.first { it.room == "AA" }
        val startingPerson = PersonState(startingValve, emptySet())
        val initialState = MoveState(startingPerson, startingPerson, emptyList())
        return runSimulation(initialState, 26)
    }

    private fun countTotalReleased(move: MoveState): Int {
        return move.releasedValves.sumOf {
            it.first * it.second.flowRate
        }
    }

    private fun canSurpassMax(timeRemaining: Int, move: MoveState, currentMax: Int): Boolean {
        return getTheoreticalMax(timeRemaining, move) > currentMax
    }

    private fun releasableValves(move: MoveState) =
        (releasableValves - move.releasedValves.map { it.second }).sortedByDescending { it.flowRate }

    private fun getTheoreticalMax(timeRemaining: Int, move: MoveState): Int {
        val currentScore = countTotalReleased(move)
        val withElephant = move.elephantValve != null

        val releasable =
            releasableValves(move).mapIndexed { ix, valve ->
                maxOf(
                    valve.flowRate * (timeRemaining - (if (!withElephant) 2 * ix else ix)),
                    0
                )
            }
                .sum()

        return currentScore + releasable
    }

    private fun runSimulation(startingState: MoveState, startingMoves: Int): Int {
        var movesRemaining = startingMoves

        var moves = listOf(startingState)
        var currentMax: Int = 2500
        println("Naive max: $currentMax")

        while (movesRemaining > 0 && moves.isNotEmpty()) {
            movesRemaining--

            moves = takeMoves(movesRemaining, moves)
            val nonDistinct = moves.size
            moves = moves.distinctBy { it.toDistinctHash() }

            val movesPreFilter = moves.size
            val newMax = moves.maxOfOrNull(::countTotalReleased) ?: Int.MIN_VALUE
            currentMax = maxOf(currentMax, newMax)

            println("$nonDistinct -> $movesPreFilter from distinct")
            moves = moves.filter { canSurpassMax(movesRemaining, it, currentMax) }
            val movesPostFilter = moves.size
            println("$movesPreFilter -> $movesPostFilter from max check")
            val runtime = Runtime.getRuntime()
            println("Actual attained so far: $newMax")
            println("Current max: $currentMax")

            println(runtime.freeMemory())
            println(runtime.maxMemory())
            println("")
        }

        return currentMax
    }

    private fun MoveState.toDistinctHash() =
        MoveHash(setOf(myValve, elephantValve), releasedValves.toSet())

    private fun findAMax(initialState: MoveState, startingMoves: Int): Int {
        var movesRemaining = startingMoves
        var moves = listOf(initialState)

        while (movesRemaining > 0) {
            movesRemaining--
            moves = takeMoves(movesRemaining, moves)
            // println(moves.size)
            if (movesRemaining % 5 == 0) {
                moves = listOf(moves.maxBy { move -> countTotalReleased(move) })
            }
            // println(move)
        }

        return moves.maxOf(::countTotalReleased)
    }

    private fun takeMoves(currentTime: Int, currentMoves: List<MoveState>): List<MoveState> {
        return currentMoves.flatMap { takeAllPossibleMoves(currentTime, it) }
    }

    private fun releasedAllValves(move: MoveState) = move.releasedValves.size == releasableValves.size

    private fun takeAllPossibleMoves(currentTime: Int, move: MoveState): List<MoveState> {
        if (releasedAllValves(move)) {
            return listOf(move)
        }

        val (currentValve, prevValves) = move.myValve

        val moves = mutableListOf<MoveState>()

        val couldRelease = currentValve.flowRate > 0 && !move.releasedValves.any { it.second == currentValve }
        if (couldRelease) {
            val newReleased = move.releasedValves.plus(Pair(currentTime, currentValve))
            val newPerson = PersonState(currentValve, emptySet())
            moves.add(move.copy(releasedValves = newReleased, myValve = newPerson))
        }

        val roomsToMoveTo = valvesMap.getValue(currentValve)
        roomsToMoveTo.forEach {
            // Prevent immediate backtracking
            if (!prevValves.contains(it)) {
                moves.add(move.copy(myValve = PersonState(it, prevValves + currentValve)))
            }
        }

        return takeElephantMoves(currentTime, moves)
    }

    private fun takeElephantMoves(currentTime: Int, currentMoves: List<MoveState>): List<MoveState> {
        return currentMoves.flatMap { takeAllPossibleElephantMoves(currentTime, it) }
    }

    private fun takeAllPossibleElephantMoves(currentTime: Int, move: MoveState): List<MoveState> {
        val (currentValve, prevValves) = move.elephantValve ?: return listOf(move)

        val moves = mutableListOf<MoveState>()

        val couldRelease = currentValve.flowRate > 0 && !move.releasedValves.any { it.second == currentValve }
        if (couldRelease) {
            val newReleased = move.releasedValves.plus(Pair(currentTime, currentValve))
            val newPerson = PersonState(currentValve, emptySet())
            moves.add(move.copy(releasedValves = newReleased, elephantValve = newPerson))
        }

        val roomsToMoveTo = valvesMap.getValue(currentValve)
        roomsToMoveTo.forEach {// Prevent immediate backtracking
            if (!prevValves.contains(it)) {
                moves.add(move.copy(elephantValve = PersonState(it, prevValves + currentValve)))
            }
        }

        return moves.toList()
    }

    private fun parseValves(line: String): Pair<Valve, List<String>> {
        val replaced = line.replace(
            "tunnel leads to valve ",
            "tunnels lead to valves "
        )

        val match =
            Regex("Valve ([A-Z]+) has flow rate=(\\d+); tunnels lead to valves ([A-Z ,]+)").find(
                replaced
            )!!


        val (room, flowRate, others) = match.destructured.toList()
        val valve = Valve(room, flowRate.toInt())
        val otherValves = others.split(", ")
        return valve to otherValves
    }

    private fun getRoutedValvesMap(parsed: List<Pair<Valve, List<String>>>): Map<Valve, List<Valve>> {
        val pairs = parsed.map { (valve, others) ->
            val otherValves = others.map { otherRoom -> parsed.first { it.first.room == otherRoom }.first }
            valve to otherValves
        }

        return pairs.toMap()
    }
}