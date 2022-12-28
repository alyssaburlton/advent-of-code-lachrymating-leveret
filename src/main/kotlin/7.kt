private const val TOTAL_SPACE = 70000000
private const val UNUSED_REQUIRED = 30000000

sealed interface Output {
    val index: Int
}

data class FileOutput(override val index: Int, val size: Int, val name: String, val dir: Boolean) : Output
data class CdOutput(override val index: Int, val cdArg: String) : Output

class Day7(mode: SolverMode) : Solver(7, mode) {
    private val input: List<String> = readStringList(filename)
    private val output = input.mapIndexedNotNull(::parseOutput)

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

    private fun parseOutput(index: Int, outputLine: String): Output? {
        val tokens = outputLine.split(" ")
        return when (tokens[1]) {
            "ls" -> null
            "cd" -> CdOutput(index, tokens[2])
            else -> when (tokens[0]) {
                "dir" -> FileOutput(index, 0, tokens[1], true)
                else -> FileOutput(index, tokens[0].toInt(), tokens[1], false)
            }
        }
    }

    private fun parseDirectorySizes() =
        output
            .filterIsInstance<FileOutput>()
            .groupBy(::computeDirectory)
            .let(::getSizes)

    private fun computeDirectory(file: FileOutput) =
        output
            .filterIsInstance<CdOutput>()
            .filter { it.index < file.index }
            .fold(listOf("/")) { pwd, output ->
                when (output.cdArg) {
                    ".." -> pwd.dropLast(1)
                    "/" -> listOf("/")
                    else -> pwd + output.cdArg
                }
            }.joinToString(">")

    private fun getSizes(dirMap: Map<String, List<FileOutput>>) =
        dirMap.mapValues { entry -> getSize(dirMap, entry.key) }

    private fun getSize(dirMap: Map<String, List<FileOutput>>, dir: String): Int {
        val flatFiles = dirMap.getValue(dir)
        val fileSize = flatFiles.sumOf { it.size }
        val subDirectories = flatFiles.filter { it.dir }
        return fileSize + subDirectories.sumOf { getSize(dirMap, dir + ">" + it.name) }
    }
}