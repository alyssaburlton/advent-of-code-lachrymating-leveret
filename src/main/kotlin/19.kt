enum class OreType {
    ORE,
    CLAY,
    OBSIDIAN,
    GEODE
}

data class OreBlueprint(
    val id: Int,
    val robotCosts: Map<OreType, Map<OreType, Int>>,
    val maxCostOfEachType: Map<OreType, Int>
)

data class OreState(
    val blueprint: OreBlueprint,
    val resources: Map<OreType, Int>,
    val robots: Map<OreType, Int>,
    val pendingRobot: OreType?
)

private const val MAX_STATES_TO_KEEP = 250

class Day19(mode: SolverMode) : Solver(19, mode) {
    private val blueprints = readStringList(filename).map(::parseBlueprint)

    override fun partA() = blueprints.sumOf {
        it.id * scoreBlueprint(it, 24)
    }

    override fun partB() = blueprints.take(3).map { scoreBlueprint(it, 32) }.product()

    private fun scoreBlueprint(blueprint: OreBlueprint, maxTime: Int): Int {
        val initialState = OreState(blueprint, OreType.values().associateWith { 0 }, mapOf(OreType.ORE to 1), null)
        return processStates(listOf(initialState), maxTime).maxOf {
            it.oreCount(OreType.GEODE)
        }
    }

    private tailrec fun processStates(
        currentStates: List<OreState>,
        timeRemaining: Int
    ): List<OreState> {
        if (timeRemaining == 0) {
            return currentStates
        }

        val allSteps = takeAllSteps(currentStates, timeRemaining)
        val pruned = allSteps.sortedByDescending(::naiveScore).take(MAX_STATES_TO_KEEP)

        return processStates(pruned, timeRemaining - 1)
    }

    private fun takeAllSteps(states: List<OreState>, time: Int) =
        states.flatMap { makeAllChoices(it, time) }.map { gainResources(it, time) }.distinct()

    private fun makeAllChoices(state: OreState, timeRemaining: Int): List<OreState> {
        val purchasable = OreType.values().filter { type ->
            shouldBuyRobot(state, type, timeRemaining)
        }.toSet()

        return producePurchaseStates(state, purchasable) + produceDoNothingState(state, purchasable)
    }

    private fun producePurchaseStates(state: OreState, purchasable: Set<OreType>) =
        if (purchasable.contains(OreType.GEODE)) {
            listOf(buyRobot(state, OreType.GEODE))
        } else {
            purchasable.map { buyRobot(state, it) }
        }

    private fun produceDoNothingState(state: OreState, purchasable: Set<OreType>) =
        if (shouldConsiderDoingNothing(state, purchasable)) {
            listOf(state)
        } else emptyList()

    private fun shouldConsiderDoingNothing(state: OreState, affordable: Set<OreType>): Boolean {
        if (affordable.contains(OreType.GEODE)) {
            return false
        }

        return affordable.isEmpty() || !oreIsPlentiful(state, affordable)
    }

    private fun oreIsPlentiful(state: OreState, affordable: Set<OreType>): Boolean {
        val minCostThisTurn = affordable.minOf { state.blueprint.robotCosts[it]!![OreType.ORE]!! }

        val currentOre = state.oreCount(OreType.ORE)
        val maxCost = state.blueprint.maxCostOfEachType.getValue(OreType.ORE)
        val production = state.robotCount(OreType.ORE)
        return currentOre - minCostThisTurn + production >= maxCost
    }

    private fun shouldBuyRobot(state: OreState, type: OreType, timeRemaining: Int) =
        canAffordRobot(state, type) && mightNeedMoreRobots(state, type, timeRemaining)

    private fun canAffordRobot(state: OreState, robot: OreType): Boolean {
        val costs = state.blueprint.robotCosts.getValue(robot)
        return costs.all { (oreType, amount) ->
            state.oreCount(oreType) >= amount
        }
    }

    private fun OreState.oreCount(oreType: OreType): Int =
        resources.getOrDefault(oreType, 0)

    private fun OreState.robotCount(robotType: OreType): Int =
        robots.getOrDefault(robotType, 0)

    private fun mightNeedMoreRobots(state: OreState, robotType: OreType, timeRemaining: Int): Boolean {
        if (robotType == OreType.GEODE) {
            return true
        }

        // Enough ore to buy on every future turn
        val potentialMaxResourceNeeded = state.blueprint.maxCostOfEachType.getValue(robotType) * (timeRemaining - 1)

        val amountWeWillGenerate = state.robotCount(robotType) * timeRemaining
        val amountWeHave = state.oreCount(robotType)

        val diff = potentialMaxResourceNeeded - (amountWeWillGenerate + amountWeHave)
        return diff > 0
    }

    private fun buyRobot(state: OreState, type: OreType): OreState {
        val costs = state.blueprint.robotCosts.getValue(type)

        val newResources = state.resources.mapValues { (oreType, amount) ->
            amount - costs.getOrDefault(oreType, 0)
        }

        return state.copy(pendingRobot = type, resources = newResources)
    }

    private fun gainResources(state: OreState, timeRemaining: Int): OreState {
        val newResources = state.resources.mapValues { (oreType, amount) ->
            val newAmount = amount + state.robotCount(oreType)

            // Throw away resources that we definitely don't need, so we can .distinct() out more "equivalent" states
            val maxNeeded = calculateMaxNeeded(state, oreType, timeRemaining)
            minOf(newAmount, maxNeeded)
        }

        val pendingBot = state.pendingRobot
        val newRobots = if (pendingBot == null) state.robots else {
            val existing = state.robotCount(pendingBot)
            state.robots.plus(pendingBot to existing + 1)
        }

        return state.copy(pendingRobot = null, resources = newResources, robots = newRobots)
    }

    private fun calculateMaxNeeded(state: OreState, oreType: OreType, timeRemaining: Int): Int {
        if (oreType == OreType.GEODE) {
            return Int.MAX_VALUE
        }

        val maxRobotsNeeded = timeRemaining - 1

        // Take into account how many we'll generate. Offset by 1 more turn than the above, in case we need to buy the very next turn
        val oreToGenerate = (maxRobotsNeeded - 1) * state.robotCount(oreType)
        return maxRobotsNeeded * state.blueprint.maxCostOfEachType.getValue(oreType) - oreToGenerate
    }

    private fun parseBlueprint(blueprintString: String): OreBlueprint {
        val match =
            Regex("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian").find(
                blueprintString
            )!!

        val ints = match.destructured.toList().map(String::toInt)

        val robotCosts = mapOf(
            OreType.ORE to mapOf(OreType.ORE to ints[1]),
            OreType.CLAY to mapOf(OreType.ORE to ints[2]),
            OreType.OBSIDIAN to mapOf(OreType.ORE to ints[3], OreType.CLAY to ints[4]),
            OreType.GEODE to mapOf(OreType.ORE to ints[5], OreType.OBSIDIAN to ints[6])
        )

        val individualRobotCosts = robotCosts.values
        val maxRobotsOfEachType = OreType.values().map { oreType ->
            oreType to individualRobotCosts.maxOf { it.getOrDefault(oreType, 0) }
        }

        return OreBlueprint(
            ints[0],
            robotCosts,
            maxRobotsOfEachType.toMap()
        )
    }

    private fun naiveScore(state: OreState) =
        (10000000 * state.robotCount(OreType.GEODE)) +
                (1000000 * state.oreCount(OreType.GEODE)) +
                (10000 * state.robotCount(OreType.OBSIDIAN)) +
                (1000 * state.oreCount(OreType.OBSIDIAN)) +
                (10 * state.robotCount(OreType.CLAY)) +
                (1 * state.oreCount(OreType.CLAY))
}