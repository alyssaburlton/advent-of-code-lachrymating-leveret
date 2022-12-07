data class MockFile(val size: Int, val name: String, val dir: Boolean)
data class IndexedOutput(val index: Int, val output: String)

private const val TOTAL_SPACE = 70000000
private const val UNUSED_REQUIRED = 30000000

class Day7 : Solver {
    override val day = 7

    private val input: List<String> = readStringList("7")
    private val output = input.mapIndexed(::IndexedOutput)

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

    private fun parseDirectorySizes() =
        output
        .filter { !isCommand(it) }
        .groupBy(::computeDirectory)
        .mapValues { entry -> entry.value.map(::parseFile) }
        .let(::getSizes)

    private fun computeDirectory(file: IndexedOutput) =
        output
            .filter(::isCommand)
            .filter { it.index < file.index && it.output != "$ ls" }
            .fold(listOf("/")) { pwd, output ->
                when (val cdArgument = output.output.split(" ").last()) {
                    ".." -> pwd.dropLast(1)
                    "/" -> listOf("/")
                    else -> pwd + cdArgument
                }
            }.joinToString(">")

    private fun parseFile(fileOutput: IndexedOutput): MockFile {
        val outputParts = fileOutput.output.split(" ")
        return when (outputParts[0]) {
            "dir" -> MockFile(0, outputParts[1], true)
            else -> MockFile(outputParts[0].toInt(), outputParts[1], false)
        }
    }

    private fun isCommand(indexedOutput: IndexedOutput) = indexedOutput.output.startsWith("$")

    private fun getSizes(dirMap: Map<String, List<MockFile>>) =
        dirMap.mapValues { entry -> getSize(dirMap, entry.key) }

    private fun getSize(dirMap: Map<String, List<MockFile>>, dir: String): Int {
        val flatFiles = dirMap.getValue(dir)
        val fileSize = flatFiles.sumOf { it.size }
        val subDirectories = flatFiles.filter { it.dir }
        return fileSize + subDirectories.sumOf { getSize(dirMap, dir + ">" + it.name) }
    }
}