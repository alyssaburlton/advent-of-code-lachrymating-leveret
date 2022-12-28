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

class Day19(mode: SolverMode) : Solver(19, mode) {
    private val blueprints = readStringList(filename).map(::parseBlueprint)

    override fun partA() = blueprints.sumOf {
        it.id * scoreBlueprint(it, 24)
    }

    override fun partB() = blueprints.take(3).map { scoreBlueprint(it, 32) }.product()

    private fun scoreBlueprint(blueprint: OreBlueprint, maxTime: Int): Int {
        val initialState = OreState(blueprint, OreType.values().associateWith { 0 }, mapOf(OreType.ORE to 1), null)
        val resultingStates = processStates(listOf(initialState), maxTime)
        return resultingStates.maxOfOrNull { it.resources.getOrDefault(OreType.GEODE, 0) } ?: 0
    }

    private tailrec fun processStates(currentStates: List<OreState>, timeRemaining: Int): List<OreState> {
        if (timeRemaining == 0) {
            return currentStates
        }

        val allSteps = takeAllSteps(currentStates, timeRemaining)
        val pruned = pruneBadStates(allSteps, timeRemaining)

        return processStates(pruned, timeRemaining - 1)
    }

    private fun pruneBadStates(states: List<OreState>, timeRemaining: Int): List<OreState> {
        if (states.isEmpty()) return states

        val guaranteed = getGuaranteedMinimum(states, timeRemaining)
        return states.filter { getOptimisticMaximum(it, timeRemaining) >= maxOf(guaranteed, 1) }
    }

    private fun getGuaranteedMinimum(states: List<OreState>, timeRemaining: Int) =
        states.maxOf { getGuaranteedMinimum(it, timeRemaining) }

    private fun getGuaranteedMinimum(state: OreState, timeRemaining: Int): Int {
        val totallyGuaranteed =
            state.resources.getValue(OreType.GEODE) + (timeRemaining * state.robotCount(OreType.GEODE))

        val turnsUntilNextGeode = turnsUntilCanAffordNextGeodeBot(state, timeRemaining)
        val additional = if (turnsUntilNextGeode > 0) {
            (timeRemaining - turnsUntilNextGeode)
        } else 0

        return totallyGuaranteed + additional
    }

    private fun getOptimisticMaximum(state: OreState, timeRemaining: Int): Int {
        val guaranteed = getGuaranteedMinimum(state, timeRemaining)

        val obsidianPenalty = computeObsidianPenalty(state)
        val geodePenalty = if (!canAffordRobot(state, OreType.GEODE)) 1 else 0
        // it takes a turn to purchase a geode robot even if we can afford right away, and purchasing on the last turn achieves nothing
        val fixedPenalty = 2

        val optimisticTimeRemaining = timeRemaining - obsidianPenalty - geodePenalty - fixedPenalty

        // Assume we buy a geode robot for every turn that's remaining. Then we get the nth triangle number of extra geodes
        val extras = if (optimisticTimeRemaining > 0) nthTriangleNumber(optimisticTimeRemaining) else 0
        return guaranteed + extras
    }

    /**
     * If we have no obsidian robots, then the optimal path is for us to purchase them every turn until we hit the
     * triangle number >= how much it costs for a geode robot.
     *
     * Assume we can afford to do this next turn, or this turn if we can afford Obsidian now.
     */
    private fun computeObsidianPenalty(state: OreState) =
        if (state.robotCount(OreType.OBSIDIAN) == 0) {
            val obsidianCost = state.blueprint.robotCosts[OreType.GEODE]!![OreType.OBSIDIAN]!!
            val triangle = findFirstTriangleNumberGreaterThan(obsidianCost) + 1
            if (canAffordRobot(state, OreType.OBSIDIAN)) {
                triangle
            } else {
                triangle + 1
            }
        } else 0

    private fun findFirstTriangleNumberGreaterThan(required: Int) =
        (1..100).first { number -> nthTriangleNumber(number) >= required }

    private fun nthTriangleNumber(n: Int) = (n * (n + 1)) / 2

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

        val currentOre = state.resources.getValue(OreType.ORE)
        val maxCost = state.blueprint.maxCostOfEachType.getValue(OreType.ORE)
        val production = state.robots.getValue(OreType.ORE)
        return currentOre - minCostThisTurn + production >= maxCost
    }

    private fun shouldBuyRobot(state: OreState, type: OreType, timeRemaining: Int) =
        canAffordRobot(state, type) && mightNeedMoreRobots(state, type, timeRemaining)

    private fun turnsUntilCanAffordNextGeodeBot(state: OreState, totalTime: Int): Int {
        val states = (1..totalTime).runningFold(state) { currentState, _ ->
            gainResources(currentState, 100)
        }

        return states.indexOfFirst { canAffordRobot(it, OreType.GEODE) } + 1
    }

    private fun canAffordRobot(state: OreState, robot: OreType): Boolean {
        val costs = state.blueprint.robotCosts.getValue(robot)
        return costs.all { (oreType, amount) ->
            state.resources.getOrDefault(oreType, 0) >= amount
        }
    }

    private fun OreState.robotCount(robotType: OreType): Int =
        robots.getOrDefault(robotType, 0)

    private fun mightNeedMoreRobots(state: OreState, robotType: OreType, timeRemaining: Int): Boolean {
        if (robotType == OreType.GEODE) {
            return true
        }

        // Enough ore to buy on every future turn
        val potentialMaxResourceNeeded = state.blueprint.maxCostOfEachType.getValue(robotType) * (timeRemaining - 1)

        val amountWeWillGenerate = state.robotCount(robotType) * timeRemaining
        val amountWeHave = state.resources.getValue(robotType)

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
        val oreToGenerate = (maxRobotsNeeded - 1) * state.robots.getOrDefault(oreType, 0)
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
}