data class Valve(val room: String, val flowRate: Int)
data class VolcanoState(
    val myValve: Valve,
    val elephantValve: Valve?,
    val releasedValves: Set<Pair<Int, Valve>>
)

data class VolcanoStateHash(val personStates: Set<Valve?>, val released: Set<Pair<Int, Valve>>)

private const val NAIVE_SEARCH_MAX_TO_KEEP = 1000

class Day16 : Solver {
    override val day = 16

    private val valvesMap = readStringList("16").map(::parseValves).let(::getRoutedValvesMap)
    private val releasableValves = valvesMap.keys.filter { it.flowRate > 0 }
    private val startingValve = valvesMap.keys.first { it.room == "AA" }

    override fun partA() =
        runSimulation(initialState(false), 29)

    override fun partB() =
        runSimulation(initialState(true), 25)

    private fun initialState(withElephant: Boolean) =
        VolcanoState(startingValve, if (withElephant) startingValve else null, emptySet())

    private fun canReleaseValve(state: VolcanoState, valve: Valve) =
        valve.flowRate > 0 && !state.releasedValves.any { it.second == valve }

    private fun releasableValves(state: VolcanoState) =
        releasableValves.filter { canReleaseValve(state, it) }

    private fun countTotalReleased(state: VolcanoState) = state.releasedValves.sumOf {
        it.first * it.second.flowRate
    }

    private fun canSurpassMax(timeRemaining: Int, state: VolcanoState, currentMax: Int) =
        getTheoreticalMax(timeRemaining, state) > currentMax

    private fun getTheoreticalMax(timeRemaining: Int, currentState: VolcanoState): Int {
        val currentScore = countTotalReleased(currentState)
        val currentValves = listOfNotNull(currentState.myValve, currentState.elephantValve)
        val canReleaseNow = currentValves.any { canReleaseValve(currentState, it) }

        val firstReleaseTime = if (canReleaseNow) timeRemaining else timeRemaining - 1
        val optimisticReleaseTimes = (firstReleaseTime downTo 0 step 2)

        return currentScore + releasableValves(currentState)
            .sortedByDescending { it.flowRate }
            .zip(optimisticReleaseTimes)
            .sumOf { it.first.flowRate * it.second }
    }

    private fun findNaiveMaximum(initialState: VolcanoState, startingTime: Int) =
        exploreRecursively(listOf(initialState), 0, startingTime) { states, _, _ ->
            states.sortedByDescending(::countTotalReleased).take(NAIVE_SEARCH_MAX_TO_KEEP)
        }

    private fun runSimulation(startingState: VolcanoState, startingTime: Int) =
        exploreRecursively(
            listOf(startingState),
            findNaiveMaximum(startingState, startingTime),
            startingTime
        ) { states, highScore, timeRemaining ->
            states.filter { canSurpassMax(timeRemaining, it, highScore) }
        }

    private tailrec fun exploreRecursively(
        states: List<VolcanoState>,
        highScore: Int,
        timeRemaining: Int,
        stateReducer: (List<VolcanoState>, Int, Int) -> List<VolcanoState>
    ): Int {
        if (timeRemaining == 0 || states.isEmpty()) {
            return highScore
        }

        val newStates = takeMoves(timeRemaining, states)
        val newHighScore = maxOf(highScore, newStates.maxOf(::countTotalReleased))
        return exploreRecursively(
            stateReducer(newStates, newHighScore, timeRemaining),
            newHighScore,
            timeRemaining - 1,
            stateReducer
        )
    }

    private fun VolcanoState.toDistinctHash() =
        VolcanoStateHash(setOf(myValve, elephantValve), releasedValves)

    private fun takeMoves(timeRemaining: Int, states: List<VolcanoState>) =
        states.flatMap { takeAllPossibleMoves(timeRemaining, it) }.distinctBy { it.toDistinctHash() }

    private fun releasedAllValves(state: VolcanoState) = state.releasedValves.size == releasableValves.size

    private fun takeAllPossibleMoves(timeRemaining: Int, state: VolcanoState): List<VolcanoState> {
        if (releasedAllValves(state)) {
            return listOf(state)
        }

        val myMoves = getValidMoves(state, timeRemaining, state.myValve, ::updateMyState)
        return state.elephantValve?.let { elephantValve ->
            myMoves.flatMap {
                getValidMoves(
                    it,
                    timeRemaining,
                    elephantValve,
                    ::updateElephantState
                )
            }
        } ?: myMoves
    }

    private fun getValidMoves(
        state: VolcanoState,
        timeRemaining: Int,
        currentValve: Valve,
        personUpdater: (VolcanoState, Valve) -> VolcanoState
    ): List<VolcanoState> {
        val movements = valvesMap
            .getValue(currentValve)
            .map { newValve -> personUpdater(state, newValve) }

        return if (canReleaseValve(state, currentValve)) {
            val newReleased = state.releasedValves.plus(Pair(timeRemaining, currentValve))
            movements + state.copy(releasedValves = newReleased)
        } else {
            movements
        }
    }

    private fun updateMyState(state: VolcanoState, myValve: Valve): VolcanoState =
        state.copy(myValve = myValve)

    private fun updateElephantState(state: VolcanoState, elephantValve: Valve): VolcanoState =
        state.copy(elephantValve = elephantValve)

    private fun parseValves(line: String): Pair<Valve, List<String>> {
        val match =
            Regex("Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z ,]+)").find(line)!!

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