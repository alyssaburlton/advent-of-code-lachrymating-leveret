data class Valve(val room: String, val flowRate: Int)
data class MoveState(
    val myValve: PersonState,
    val elephantValve: PersonState?,
    val releasedValves: Set<Pair<Int, Valve>>
)

data class PersonState(val currentValve: Valve, val prevValves: Set<Valve>)
data class MoveHash(val personStates: Set<PersonState?>, val released: Set<Pair<Int, Valve>>)

class Day16 : Solver {
    override val day = 16

    private val input = readStringList("16").map(::parseValves)
    private val valvesMap = getRoutedValvesMap(input)
    private val releasableValves = valvesMap.keys.filter { it.flowRate > 0 }
    private val startingValve = valvesMap.keys.first { it.room == "AA" }

    override fun partA(): Any {
        val initialState = MoveState(initialPersonState(), null, emptySet())
        return runSimulation(initialState, 30)
    }

    override fun partB(): Any {
        val initialState = MoveState(initialPersonState(), initialPersonState(), emptySet())
        return runSimulation(initialState, 26)
    }

    private fun initialPersonState() = PersonState(startingValve, emptySet())

    private fun countTotalReleased(move: MoveState) = move.releasedValves.sumOf {
        it.first * it.second.flowRate
    }

    private fun canSurpassMax(timeRemaining: Int, move: MoveState, currentMax: Int) =
        getTheoreticalMax(timeRemaining, move) > currentMax

    private fun getTheoreticalMax(timeRemaining: Int, move: MoveState): Int {
        val currentScore = countTotalReleased(move)
        val canReleaseNow =
            canReleaseValve(move, move.myValve.currentValve) || (move.elephantValve != null && canReleaseValve(
                move,
                move.elephantValve.currentValve
            ))

        val firstReleaseTime = if (canReleaseNow) timeRemaining else timeRemaining - 1
        val optimisticReleaseTimes = (firstReleaseTime downTo 0 step 2)

        val zipped = optimisticReleaseTimes zip releasableValves(move).sortedByDescending { it.flowRate }
        return currentScore + zipped.sumOf { it.first * it.second.flowRate }
    }

    private fun canReleaseValve(move: MoveState, valve: Valve) =
        valve.flowRate > 0 && !move.releasedValves.any { it.second == valve }

    private fun releasableValves(move: MoveState) =
        (releasableValves - move.releasedValves.map { it.second })

    private fun runSimulation(startingState: MoveState, startingMoves: Int): Int {
        var movesRemaining = startingMoves

        var moves = listOf(startingState)
        var currentMax: Int = findAMax(startingState, startingMoves)
        println("Naive max: $currentMax")

        while (movesRemaining > 0 && moves.isNotEmpty()) {
            movesRemaining--

            moves = takeMoves(movesRemaining, moves)
            val newMax = moves.maxOfOrNull(::countTotalReleased) ?: Int.MIN_VALUE
            currentMax = maxOf(currentMax, newMax)
            moves = moves.filter { canSurpassMax(movesRemaining, it, currentMax) }
            println("$movesRemaining: ${moves.size} states, max score so far is $currentMax")
        }

        return currentMax
    }

    private fun MoveState.toDistinctHash() =
        MoveHash(setOf(myValve, elephantValve), releasedValves)

    private fun findAMax(initialState: MoveState, startingMoves: Int): Int {
        var movesRemaining = startingMoves
        var moves = listOf(initialState)
        var currentMax = 0

        while (movesRemaining > 0 && moves.isNotEmpty()) {
            movesRemaining--
            moves = takeMoves(movesRemaining, moves)
            currentMax = maxOf(currentMax, countTotalReleased(moves.first()))
            if (moves.size > 1000) {
                moves = moves.sortedByDescending { move -> countTotalReleased(move) }.subList(0, 1000)
            }
        }

        return currentMax
    }

    private fun takeMoves(currentTime: Int, currentMoves: List<MoveState>): List<MoveState> {
        return currentMoves.flatMap { takeAllPossibleMoves(currentTime, it) }.distinctBy { it.toDistinctHash() }
    }

    private fun releasedAllValves(move: MoveState) = move.releasedValves.size == releasableValves.size

    private fun takeAllPossibleMoves(currentTime: Int, move: MoveState): List<MoveState> {
        if (releasedAllValves(move)) {
            return listOf(move)
        }

        val (currentValve, prevValves) = move.myValve

        val moves = mutableListOf<MoveState>()

        if (canReleaseValve(move, currentValve)) {
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
        if (canReleaseValve(move, currentValve)) {
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