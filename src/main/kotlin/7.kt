data class MockFile(val size: Int, val name: String, val dir: Boolean)

private const val TOTAL_SPACE = 70000000
private const val UNUSED_REQUIRED = 30000000

class Day7 : Solver {
    override val day = 7

    private val input: List<String> = readStringList("7")

    override fun partA() = parseDirectorySizes()
        .filter { it.value <= 100000 }
        .values
        .sum()

    override fun partB(): Any {
        val sizeMap = parseDirectorySizes()

        val spaceRemaining = TOTAL_SPACE - sizeMap.getValue("/")
        val spaceToBeFreed = UNUSED_REQUIRED - spaceRemaining
        return sizeMap.values.filter { it > spaceToBeFreed }.min()
    }

    private fun parseDirectorySizes(): Map<String, Int> {
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
                val name = inputLine.split(" ").last()
                addFile(map, pwd, MockFile(0, name, true))
            } else {
                val (size, name) = inputLine.split(" ")
                addFile(map, pwd, MockFile(size.toInt(), name, false))
            }
        }

        return getSizes(map)
    }

    private fun addFile(map: MutableMap<String, List<MockFile>>, pwd: List<String>, file: MockFile) {
        val pwdString = pwd.joinToString(">")
        val currentList = map.getOrDefault(pwdString, emptyList())
        map[pwdString] = currentList + file
    }

    private fun getSizes(dirMap: Map<String, List<MockFile>>) =
        dirMap.mapValues { entry -> getSize(dirMap, entry.key) }

    private fun getSize(dirMap: Map<String, List<MockFile>>, dir: String): Int {
        val flatFiles = dirMap.getValue(dir)
        val fileSize = flatFiles.sumOf { it.size }
        val subDirectories = flatFiles.filter { it.dir }
        return fileSize + subDirectories.sumOf { getSize(dirMap, dir + ">" + it.name) }
    }
}