data class MockFile(val size: Int, val name: String, val dir: Boolean)

class Day7 : Solver {
    override val day = 7

    private val input: List<String> = readStringList("7")

    private val TOTAL_SPACE = 70000000
    private val UNUSED_REQUIRED = 30000000

    override fun partA(): Any {
        val map = parseDirectories()
        val sizes = getSizes(map)

        val bigDirs = sizes.filter { it.value <= 100000 }
        return bigDirs.values.sum()
    }

    private fun parseDirectories(): Map<String, List<MockFile>> {
        var pwd = listOf("/")
        val map = mutableMapOf<String, List<MockFile>>()

        input.forEach { inputLine ->
            if (inputLine == "$ cd /") {
                pwd = listOf("/")
            } else if (inputLine == "$ cd ..") {
                pwd = pwd.dropLast(1)
            } else if (inputLine.startsWith("$ cd")) {
                val newDir = inputLine.split(" ").last()
                pwd = (pwd + newDir)
            } else if (inputLine == "$ ls") {
                // nothing
            } else if (inputLine.startsWith("dir")) {
                val pwdString = pwd.joinToString(">")
                val name = inputLine.split(" ").last()
                val file = MockFile(0, name, true)
                val currentList = map.getOrDefault(pwdString, emptyList())
                map[pwdString] = currentList + file
            } else {
                val pwdString = pwd.joinToString(">")
                val (size, name) = inputLine.split(" ")
                val file = MockFile(size.toInt(), name, false)
                val currentList = map.getOrDefault(pwdString, emptyList())
                map[pwdString] = currentList + file
            }
        }

        return map
    }

    private fun getSizes(dirMap: Map<String, List<MockFile>>): Map<String, Int> {
        return dirMap.mapValues { entry -> getSize(dirMap, entry.key) }
    }

    private fun getSize(dirMap: Map<String, List<MockFile>>, dir: String): Int {
        val flatFiles = dirMap.getValue(dir)
        val fileSize = flatFiles.filter { !it.dir }.sumOf { it.size }
        val subDirectories = flatFiles.filter { it.dir }
        return fileSize + subDirectories.sumOf { getSize(dirMap, dir + ">" + it.name) }
    }

    override fun partB(): Any {
        val map = parseDirectories()
        val sizes = getSizes(map)

        val spaceRemaining = TOTAL_SPACE - sizes.getValue("/")
        val spaceToBeFreed = UNUSED_REQUIRED - spaceRemaining

        sizes.values.filter { it > spaceToBeFreed }.min()
        return sizes.values.filter { it > spaceToBeFreed }.min()
    }
}