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

class Day19 : Solver {
    override val day = 19

    private val blueprints = readStringList("19").map(::parseBlueprint)

    override fun partA() = blueprints.sumOf {
        it.id * scoreBlueprint(it, 24)
    }

    override fun partB() = blueprints.take(3).map { scoreBlueprint(it, 32, true) }.product()

    private fun scoreBlueprint(blueprint: OreBlueprint, maxTime: Int, log: Boolean = false): Int {
        if (log) println("Scoring blueprint ${blueprint.id}...")
        val state = OreState(blueprint, OreType.values().associateWith { 0 }, mapOf(OreType.ORE to 1), null)
        var states = listOf(state)
        var timeRemaining = maxTime
        while (timeRemaining > 0) {
            states = takeAllSteps(states, timeRemaining)
            if (log) println(
                "$timeRemaining: ${states.size}, max geodes: ${
                    states.maxOf {
                        it.resources.getOrDefault(
                            OreType.GEODE,
                            0
                        )
                    }
                }"
            )
            timeRemaining--
        }

        if (log) println()

        return states.maxOf { it.resources.getOrDefault(OreType.GEODE, 0) }
    }

    private fun takeAllSteps(states: List<OreState>, time: Int): List<OreState> {
        val nextSteps = states.flatMap(::makeAllChoices).map { gainResources(it, time) }.distinct()
        return filterOutByMaxGeodeBots(nextSteps)
    }

    /**
     * If we're more than 1 geode bot behind the best so far, we're done
     */
    private fun filterOutByMaxGeodeBots(states: List<OreState>): List<OreState> {
        val max = states.maxOf { it.robots.getOrDefault(OreType.GEODE, 0) }
        return states.filter { it.robots.getOrDefault(OreType.GEODE, 0) >= max - 1 }
    }

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

    private fun shouldBuyRobot(state: OreState, type: OreType): Boolean {
        val costs = state.blueprint.robotCosts.getValue(type)
        val canAfford = costs.all { (oreType, amount) ->
            state.resources.getOrDefault(oreType, 0) >= amount
        }

        return canAfford && getMaxRobotsWorthBuying(state, type) > 0
    }

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
            val robots = state.robots[oreType]
            val newAmount = if (robots != null) {
                amount + robots
            } else amount

            // Throw away resources that we definitely don't need, so we can .distinct() out more "equivalent" states
            val maxNeeded = calculateMaxNeeded(state, oreType, timeRemaining)
            minOf(newAmount, maxNeeded)
        }

        val pendingBot = state.pendingRobot
        val newRobots = if (pendingBot == null) state.robots else {
            val existing = state.robots.getOrDefault(pendingBot, 0)
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

        val maxTurnsToBuyRobots = minOf(timeRemaining - 1, maxRobotsToBuy)
        return maxTurnsToBuyRobots * state.blueprint.maxCostOfEachType.getValue(oreType)
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