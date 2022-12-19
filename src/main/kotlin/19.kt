enum class OreType {
    ORE,
    CLAY,
    OBSIDIAN,
    GEODE
}

data class OreBlueprint(
    val id: Int,
    val robotCosts: Map<OreType, Map<OreType, Int>>,
    val maxRobotsOfEachType: Map<OreType, Int>
)

data class OreState(
    val blueprint: OreBlueprint,
    val resources: Map<OreType, Int>,
    val robots: Map<OreType, Int>,
    val pendingRobot: OreType?
)

// 970 is wrong...
// 1062 is too low
class Day19 : Solver {
    override val day = 19

    private val blueprints = readStringList("19").map(::parseBlueprint)

    override fun partA(): Any {
        return blueprints.sumOf {
            it.id * scoreBlueprint(it, 24)
        }
    }

    override fun partB(): Any {
        val results = blueprints.subList(0, 3).map { scoreBlueprint(it, 32) }
        // println(results)
        return results.product()
    }

    private fun scoreBlueprint(blueprint: OreBlueprint, maxTime: Int): Int {
        // println("Scoring blueprint ${blueprint.id}...")
        val state = OreState(blueprint, OreType.values().associateWith { 0 }, mapOf(OreType.ORE to 1), null)
        var states = listOf(state)
        var time = 0
        while (time < maxTime) {
            states = takeAllSteps(states)
            // println("$time: ${states.size}")
            time++
        }

        return states.maxOf { it.resources.getOrDefault(OreType.GEODE, 0) }
    }

    private fun takeAllSteps(states: List<OreState>): List<OreState> {
        val nextSteps = states.flatMap(::makeAllChoices).map(::gainResources).distinct()
        return filterOutByMaxGeodeBots(filterOutByMaxObsidianBots(nextSteps))
    }

    /**
     * If we're more than 3 obsidian bot behind the best so far, we're done
     */
    private fun filterOutByMaxObsidianBots(states: List<OreState>): List<OreState> {
        val max = states.maxOf { it.robots.getOrDefault(OreType.OBSIDIAN, 0) }
        if (max > 0) {
            // println("Max geode robots: $max")
        }
        return states.filter { it.robots.getOrDefault(OreType.OBSIDIAN, 0) >= max - 3 }
    }

    /**
     * If we're more than 1 geode bot behind the best so far, we're done
     */
    private fun filterOutByMaxGeodeBots(states: List<OreState>): List<OreState> {
        val max = states.maxOf { it.robots.getOrDefault(OreType.GEODE, 0) }
        if (max > 0) {
            // println("Max geode robots: $max")
        }
        return states.filter { it.robots.getOrDefault(OreType.GEODE, 0) >= max - 1 }
    }

    private fun makeAllChoices(state: OreState): List<OreState> {
        val options = mutableListOf<OreState>()
        val affordable = OreType.values().filter { type ->
            shouldBuyRobot(state, type, state.blueprint.robotCosts.getValue(type))
        }

        if (affordable.contains(OreType.GEODE)) {
            options.add(buyRobot(state, OreType.GEODE))
        } else {
            affordable.forEach {
                options.add(buyRobot(state, it))
            }
        }

        if (!affordable.contains(OreType.GEODE)) {
            options.add(state)
        }

        return options
    }

    private fun shouldBuyRobot(state: OreState, type: OreType, costs: Map<OreType, Int>): Boolean {
        val currentCount = state.robots.getOrDefault(type, 0)
        val potentiallyNeed = state.blueprint.maxRobotsOfEachType.getValue(type)

        val shouldGet = type == OreType.GEODE || currentCount < potentiallyNeed

        val canAfford = costs.all { (oreType, amount) ->
            state.resources.getOrDefault(oreType, 0) >= amount
        }

        return canAfford && shouldGet
    }

    private fun buyRobot(state: OreState, type: OreType): OreState {
        val costs = state.blueprint.robotCosts.getValue(type)

        val newResources = state.resources.mapValues { (oreType, amount) ->
            amount - costs.getOrDefault(oreType, 0)
        }

        return state.copy(pendingRobot = type, resources = newResources)
    }

    private fun gainResources(state: OreState): OreState {
        val newResources = state.resources.mapValues { (oreType, amount) ->
            val robots = state.robots[oreType]
            if (robots != null) {
                amount + robots
            } else amount
        }

        val pendingBot = state.pendingRobot
        val newRobots = if (pendingBot == null) state.robots else {
            val existing = state.robots.getOrDefault(pendingBot, 0)
            state.robots.plus(pendingBot to existing + 1)
        }

        return state.copy(pendingRobot = null, resources = newResources, robots = newRobots)
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