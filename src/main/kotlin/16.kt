data class Valve(val room: String, val flowRate: Int)
data class MoveState(val currentValve: Valve, val releasedValves: Map<Int, Valve>)

class Day16 : Solver {
    override val day = 16

    private val input = readStringList("16").map(::parseValves)
    private val valvesMap = getRoutedValvesMap(input)
    private val releasableValves = valvesMap.keys.filter { it.flowRate > 0 }

    override fun partA(): Any {
        var movesRemaining = 29
        val startingValve = valvesMap.keys.first { it.room == "AA" }
        val initialState = MoveState(startingValve, emptyMap())

        var moves = listOf(initialState)
        var currentMax: Int? = null

        while (movesRemaining > 0 && moves.isNotEmpty()) {
            println("$movesRemaining, total states ${moves.size}")
            moves = takeMoves(movesRemaining, moves).distinct()
            currentMax = moves.maxOfOrNull(::countTotalReleased)
            moves = moves.filter { canSurpassMax(movesRemaining - 1, it, currentMax) }
            movesRemaining--
        }

        return currentMax!!
    }

    private fun countTotalReleased(move: MoveState): Int {
        return move.releasedValves.entries.sumOf {
            it.key * it.value.flowRate
        }
    }

    private fun canSurpassMax(timeRemaining: Int, move: MoveState, currentMax: Int?): Boolean {
        currentMax ?: return true

        val toRelease = (releasableValves - move.releasedValves.values).sortedByDescending { it.flowRate }

        val currentScore = countTotalReleased(move)

        val releasable = toRelease.mapIndexed { ix, valve -> valve.flowRate * (timeRemaining - ix) }.sum()
        return currentScore + releasable > currentMax
    }

    override fun partB(): Any {
        return ""
    }

    private fun takeMoves(currentTime: Int, currentMoves: List<MoveState>): List<MoveState> {
        return currentMoves.flatMap { takeAllPossibleMoves(currentTime, it) }
    }

    private fun releasedAllValves(move: MoveState) = move.releasedValves.values.size == releasableValves.size

    private fun takeAllPossibleMoves(currentTime: Int, move: MoveState): List<MoveState> {
        if (releasedAllValves(move)) {
            return listOf(move)
        }

        val currentValve = move.currentValve

        val moves = mutableListOf<MoveState>()

        val couldRelease = currentValve.flowRate > 0 && !move.releasedValves.containsValue(currentValve)
        if (couldRelease) {
            val newReleased = move.releasedValves.plus(Pair(currentTime, currentValve))
            moves.add(move.copy(releasedValves = newReleased))
        }

        val roomsToMoveTo = valvesMap.getValue(currentValve)
        roomsToMoveTo.forEach {
            moves.add(move.copy(currentValve = it))
        }

        return moves.toList()
    }

    private fun parseValves(line: String): Pair<Valve, List<String>> {
        val replaced = line.replace(
            "tunnel leads to valve ",
            "tunnels lead to valves "
        )
        println("Parsing $replaced")
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