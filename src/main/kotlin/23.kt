class Day23 : Solver {
    override val day = 23

    private val input = readStringGrid("23")

    override fun partA(): Any {
//        val elves = input.map.filter { it.value == "#" }.keys.toMutableSet()
//
//        input.print()
//
//        repeat(10) { round ->
//            // println()
//            // println()
//            val proposedPositions = mutableMapOf<Point, MutableList<Point>>()
//            elves.forEach { elf ->
//                val proposal = proposePosition(elf, elves, round)
//
//                if (proposal != null) {
//                    val current = proposedPositions.getOrPut(proposal, ::mutableListOf)
//                    current.add(elf)
//                }
//            }
//
//            val validResults = proposedPositions.filter { it.value.size == 1 }
//            val oldElves = validResults.values.flatten().toSet()
//            val newElves = validResults.keys
//
//            elves.removeAll(oldElves)
//            elves.addAll(newElves)
//
//            // val map = elves.associateWith { "#" }
//            // Grid(map).print()
//        }
//
//        println(elves)
//
//
//        val xBounds = (elves.maxOf { it.x } - elves.minOf { it.x }) + 1
//        val yBounds = (elves.maxOf { it.y } - elves.minOf { it.y }) + 1
//
//        return (xBounds * yBounds) - elves.size
        return ""
    }

    private fun proposePosition(elf: Point, elves: Set<Point>, roundNumber: Int): Point? {
        val neighbours = elf.neighboursWithDiagonals()
        if (elves.intersect(neighbours).isEmpty()) {
            // Don't move
            return null
        }

        val northernPoints = neighbours.filter { it.y < elf.y }
        val southernPoints = neighbours.filter { it.y > elf.y }
        val westernPoints = neighbours.filter { it.x < elf.x }
        val easternPoints = neighbours.filter { it.x > elf.x }

        val movements = listOf(
            move(elf, northernPoints, elves),
            move(elf, southernPoints, elves),
            move(elf, westernPoints, elves),
            move(elf, easternPoints, elves)
        )

        val indices = (roundNumber..roundNumber + 3).map { it.mod(4) }
        return indices.firstNotNullOfOrNull { movements[it] }
    }

    private fun move(elf: Point, points: List<Point>, elves: Set<Point>): Point? {
        if (elves.intersect(points).isEmpty()) {
            return points.filter { it.stepDistance(elf) == 1 }.only()
        }

        return null
    }

    override fun partB(): Any {
        var elves = input.map.filter { it.value == "#" }.keys.toSet()
        val originalSize = elves.size

        var elfMoved = true
        var round = 0

        while (elfMoved) {
            val proposedPositions = mutableMapOf<Point, MutableList<Point>>()
            val elvesNotMoved = mutableSetOf<Point>()
            elves.forEach { elf ->
                val proposal = proposePosition(elf, elves.filter { it.stepDistance(elf) <= 2 }.toSet(), round)

                if (proposal != null) {
                    val current = proposedPositions.getOrPut(proposal, ::mutableListOf)
                    current.add(elf)
                } else {
                    elvesNotMoved.add(elf)
                }
            }

            val otherElvesNotMoved = proposedPositions.filter { it.value.size > 1 }.values.flatten().toSet()
            val validResults = proposedPositions.filter { it.value.size == 1 }
            val newElves = validResults.keys

            println("Done round $round, elves moved: ${newElves.size} / $originalSize")

            elfMoved = validResults.isNotEmpty()
            round += 1

            elves = elvesNotMoved + otherElvesNotMoved + newElves
            check(elves.size == originalSize)
        }

        return round
    }
}