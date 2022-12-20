class Day20 : Solver {
    override val day = 20

    private val input = readIntegerList("20")
    private val decryptionKey = 811589153

    private data class Number(val value: Long, val originalIx: Int)

    override fun partA(): Any {
        val originalOrder = input.map { it.toLong() }
        val newList = originalOrder.mapIndexed { ix, no -> Number(no, ix) }.toMutableList()

        originalOrder.indices.forEach { originalIx ->
            // println("Moving $number")
            val currentIx = newList.indexOfFirst { it.originalIx == originalIx }
            val element = newList.find { it.originalIx == originalIx }!!
            var newIx = ((currentIx + element.value) % (input.size - 1)).toInt()
            if (newIx <= 0) {
                newIx += (input.size - 1)
            }

            newList.removeAt(currentIx)
            newList.add(newIx, Number(element.value, originalIx))
        }

        // println(newList)
        val zeroIndex = newList.indexOfFirst { it.value == 0L }
        return newList[(zeroIndex + 1000) % newList.size].value + newList[(zeroIndex + 2000) % newList.size].value + newList[(zeroIndex + 3000) % newList.size].value
    }

    override fun partB(): Any {
        val originalOrder = input.map { it.toLong() * decryptionKey }
        val newList = originalOrder.mapIndexed { ix, no -> Number(no, ix) }.toMutableList()

        repeat(10) {
            originalOrder.indices.forEach { originalIx ->
                // println("Moving $number")
                val currentIx = newList.indexOfFirst { it.originalIx == originalIx }
                val element = newList.find { it.originalIx == originalIx }!!
                var newIx = ((currentIx + element.value) % (input.size - 1)).toInt()
                if (newIx <= 0) {
                    newIx += (input.size - 1)
                }

                newList.removeAt(currentIx)
                newList.add(newIx, Number(element.value, originalIx))
            }

            println(newList.map { it.value })
        }


        val zeroIndex = newList.indexOfFirst { it.value == 0L }
        return newList[(zeroIndex + 1000) % newList.size].value + newList[(zeroIndex + 2000) % newList.size].value + newList[(zeroIndex + 3000) % newList.size].value
    }
}