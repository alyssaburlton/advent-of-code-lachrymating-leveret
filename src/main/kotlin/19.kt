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

var maxStatesConsidered = 0

class Day19 : Solver {
    override val day = 19

    private val blueprints = readStringList("19").map(::parseBlueprint)

    override fun partA(): Any {
        val result = blueprints.sumOf {
            it.id * scoreBlueprint(it, 24)
        }

        println("Max considered: $maxStatesConsidered")
        maxStatesConsidered = 0
        return result
    }

    override fun partB(): Any {
        val result = blueprints.take(3).map { scoreBlueprint(it, 32) }.product()
        println("Max considered: $maxStatesConsidered")
        return result
    }

    private fun scoreBlueprint(blueprint: OreBlueprint, maxTime: Int): Int {
        // println()
        // println("Scoring blueprint $blueprint")
        val state = OreState(blueprint, OreType.values().associateWith { 0 }, mapOf(OreType.ORE to 1), null)
        var states = listOf(state)
        var timeRemaining = maxTime
        while (timeRemaining > 0) {
            states = takeAllSteps(states, timeRemaining)
            // val size = states.size
            states = pruneBadStates(states, timeRemaining)

            // println("$timeRemaining: $size -> ${states.size}")
            maxStatesConsidered = maxOf(maxStatesConsidered, states.size)

            timeRemaining--
        }

        return states.maxOfOrNull { it.resources.getOrDefault(OreType.GEODE, 0) } ?: 0
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

        val turnsUntilNextGeode = turnsUntilCanAfford(state, OreType.GEODE, timeRemaining)
        val additional = if (turnsUntilNextGeode > 0) {
            (timeRemaining - turnsUntilNextGeode)
        } else 0

        return totallyGuaranteed + additional
    }

    private fun getOptimisticMaximum(state: OreState, timeRemaining: Int): Int {
        val guaranteed = getGuaranteedMinimum(state, timeRemaining)

        var optimisticTimeRemaining = timeRemaining - 1
        if (state.robotCount(OreType.OBSIDIAN) == 0) {
            val obsidianCost = state.blueprint.robotCosts[OreType.GEODE]!![OreType.OBSIDIAN]!!
            val triangle = findFirstTriangleNumberGreaterThan(obsidianCost)
            // Still need to buy an obsidian robot
            if (canAffordRobot(state, OreType.OBSIDIAN)) {
                // 1 turn to buy, 1 turn to generate at least 1 obsidian
                optimisticTimeRemaining -= (triangle + 1)
            } else {
                // 2 turns to buy, 1 turn to generate at least 1 obsidian
                optimisticTimeRemaining -= (triangle + 2)
            }
        }

        if (!canAffordRobot(state, OreType.GEODE)) {
            // 1 turn to afford, 1 turn to buy
            optimisticTimeRemaining -= 2
        } else {
            // 1 turn to buy
            optimisticTimeRemaining -= 1
        }

        // Assume we buy a geode robot for every turn that's remaining. Then we get the nth triangle number of extra geodes
        val extras = if (optimisticTimeRemaining > 0) nthTriangleNumber(optimisticTimeRemaining) else 0
        return guaranteed + extras
    }

    private fun findFirstTriangleNumberGreaterThan(required: Int): Int {
        (1..100).forEach { number ->
            if (nthTriangleNumber(number) >= required) {
                return number
            }
        }

        return -1
    }

    private fun nthTriangleNumber(n: Int) = (n * (n + 1)) / 2

    private fun takeAllSteps(states: List<OreState>, time: Int) =
        states.flatMap(::makeAllChoices).map { gainResources(it, time) }.distinct()

    private fun makeAllChoices(state: OreState): List<OreState> {
        val options = mutableListOf<OreState>()
        val affordable = OreType.values().filter { type ->
            shouldBuyRobot(state, type)
        }.toSet()

        if (affordable.contains(OreType.GEODE)) {
            options.add(buyRobot(state, OreType.GEODE))
        } else {
            affordable.forEach {
                options.add(buyRobot(state, it))
            }
        }

        if (shouldConsiderDoingNothing(state, affordable)) {
            options.add(state)
        }

        return options
    }

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

    private fun shouldBuyRobot(state: OreState, type: OreType) =
        canAffordRobot(state, type) && getMaxRobotsWorthBuying(state, type) > 0

    private fun turnsUntilCanAfford(state: OreState, robot: OreType, totalTime: Int): Int {
        var time = 1
        var currentState = state
        while (time <= totalTime) {
            if (canAffordRobot(currentState, robot)) {
                return time
            }

            currentState = gainResources(currentState, 100)
            time++
        }

        return -1
    }

    private fun canAffordRobot(state: OreState, robot: OreType): Boolean {
        val costs = state.blueprint.robotCosts.getValue(robot)
        return costs.all { (oreType, amount) ->
            state.resources.getOrDefault(oreType, 0) >= amount
        }
    }

    private fun OreState.robotCount(robotType: OreType): Int =
        robots.getOrDefault(robotType, 0)

    private fun getMaxRobotsWorthBuying(state: OreState, robotType: OreType): Int {
        if (robotType == OreType.GEODE) {
            return 50
        }

        val currentCount = state.robots.getOrDefault(robotType, 0)
        val potentiallyNeed = state.blueprint.maxCostOfEachType.getValue(robotType)
        return potentiallyNeed - currentCount
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

        // Clay is only needed to produce Obsidian robots, and there's a fixed max of these that we need. So if we have
        // more turns remaining than that, we can bound the max clay sooner.
        val maxRobotsToBuy = if (oreType == OreType.CLAY) {
            getMaxRobotsWorthBuying(state, OreType.OBSIDIAN)
        } else Int.MAX_VALUE

        val maxRobotsNeeded = minOf(timeRemaining - 1, maxRobotsToBuy)

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