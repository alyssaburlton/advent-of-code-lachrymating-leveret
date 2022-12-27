data class Valve(val room: String, val flowRate: Int)
data class VolcanoState(
    val myValve: Valve,
    val elephantValve: Valve?,
    val releasedValves: Set<Pair<Int, Valve>>
)

data class VolcanoStateHash(val personStates: Set<Valve?>, val released: Set<Pair<Int, Valve>>)

private const val NAIVE_SEARCH_MAX_TO_KEEP = 500

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

    /**
     * Leaving a valve to open a neighbour before coming back costs at least 3 turns.
     * Leaving a valve to open a further valve before coming back costs at least 5 turns.
     *
     * If there is not another unopened valve with a value > this number of turns, this isn't worth it
     */
    private fun mustReleaseValve(state: VolcanoState, valve: Valve) =
        valve.flowRate > (state.releasableNeighbours(valve).maxOfOrNull { it.flowRate - 3 }
            ?: 0) && valve.flowRate > releasableValves(state).maxOf { it.flowRate } - 5

    private fun releasableValves(state: VolcanoState) =
        releasableValves.filter { canReleaseValve(state, it) }

    private fun VolcanoState.releasableNeighbours(valve: Valve) =
        valvesMap.getValue(valve).filter { canReleaseValve(this, it) }

    private fun countTotalReleased(state: VolcanoState) = state.releasedValves.sumOf {
        it.first * it.second.flowRate
    }

    private fun canSurpassMax(timeRemaining: Int, state: VolcanoState, currentMax: Int) =
        getTheoreticalMax(timeRemaining, state) > currentMax

    private fun getTheoreticalMax(timeRemaining: Int, currentState: VolcanoState): Int {
        val currentScore = countTotalReleased(currentState)
        val currentValves = listOfNotNull(currentState.myValve, currentState.elephantValve)
        val canReleaseNow = currentValves.any { canReleaseValve(currentState, it) }
        val releasableNeighbours = currentValves.flatMap { currentState.releasableNeighbours(it) }

        val firstReleaseTime =
            if (canReleaseNow) timeRemaining else if (releasableNeighbours.isNotEmpty()) timeRemaining - 1 else timeRemaining - 2
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
            startingTime,
            true
        ) { states, highScore, timeRemaining ->
            states.filter { canSurpassMax(timeRemaining, it, highScore) }
        }

    private tailrec fun exploreRecursively(
        states: List<VolcanoState>,
        highScore: Int,
        timeRemaining: Int,
        log: Boolean = false,
        stateReducer: (List<VolcanoState>, Int, Int) -> List<VolcanoState>,
    ): Int {
        if (timeRemaining == 0 || states.isEmpty()) {
            return highScore
        }

        val newStates = takeMoves(timeRemaining, states)
        if (log) println(newStates.size)
        val newHighScore = maxOf(highScore, newStates.maxOf(::countTotalReleased))
        return exploreRecursively(
            stateReducer(newStates, newHighScore, timeRemaining),
            newHighScore,
            timeRemaining - 1,
            log,
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

        return if (!canReleaseValve(state, currentValve)) {
            movements
        } else {
            val newReleased = state.releasedValves.plus(Pair(timeRemaining, currentValve))
            val releasedState = state.copy(releasedValves = newReleased)

            if (mustReleaseValve(state, currentValve)) {
                listOf(releasedState)
            } else {
                movements + releasedState
            }
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