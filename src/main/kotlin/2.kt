class Day2 : Solver {
    override val day = 2

    private val input = readStringList("2")
    private val rpc = listOf("A", "B", "C")

    override fun partA() = input.map(::getScore).sum()

    private fun getScore(strat: String): Int {
        val values = strat.split(" ")

        val myMove = when (values[1]) {
            "X" -> 1
            "Y" -> 2
            else -> 3
        }

        val score = when (strat) {
            "A X", "B Y", "C Z" -> 3
            "A Y", "B Z", "C X" -> 6
            else -> 0
        }

        return myMove + score
    }

    override fun partB() = input.map(::getScoreB).sum()

    private fun getScoreB(strat: String): Int {
        val values = strat.split(" ")
        val play = values[1]

        val score = when (play) {
            "X" -> 0
            "Y" -> 3
            else -> 6
        }

        val oppIx = rpc.indexOf(values[0])
        val myMove = when (play) {
            "X" -> (oppIx + 2) % 3
            "Y" -> oppIx
            else -> (oppIx + 1) % 3
        }

        return myMove + score + 1
    }
}