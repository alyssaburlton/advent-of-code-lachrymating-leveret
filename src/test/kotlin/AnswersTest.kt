import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AnswersTest {
    @Test
    fun `Day 1`() {
        Day1(SolverMode.REAL).testDay(74711, 209481)
    }

    @Test
    fun `Day 2`() {
        Day2(SolverMode.REAL).testDay(12794, 14979)
    }

    @Test
    fun `Day 3`() {
        Day3(SolverMode.REAL).testDay(7826, 2577)
    }

    @Test
    fun `Day 4`() {
        Day4(SolverMode.REAL).testDay(515, 883)
    }

    @Test
    fun `Day 5`() {
        Day5(SolverMode.REAL).testDay("VPCDMSLWJ", "TPWCGNCCG")
    }

    @Test
    fun `Day 6`() {
        Day6(SolverMode.REAL).testDay(1343, 2193)
    }

    @Test
    fun `Day 7`() {
        Day7(SolverMode.REAL).testDay(1792222, 1112963)
    }

    @Test
    fun `Day 8`() {
        Day8(SolverMode.REAL).testDay(1672, 327180)
    }

    @Test
    fun `Day 9`() {
        Day9(SolverMode.REAL).testDay(6090, 2566)
    }

    @Test
    fun `Day 10`() {
        Day10(SolverMode.REAL).testDay(
            13740, """
#### #  # ###  ###  #### ####  ##  #    
   # #  # #  # #  # #    #    #  # #    
  #  #  # #  # #  # ###  ###  #    #    
 #   #  # ###  ###  #    #    #    #    
#    #  # #    # #  #    #    #  # #    
####  ##  #    #  # #    ####  ##  #### """
        )
    }

    @Test
    fun `Day 11`() {
        Day11(SolverMode.REAL).testDay(99852, 25935263541L)
    }

    @Test
    fun `Day 12`() {
        Day12(SolverMode.REAL).testDay(383, 377)
    }

    @Test
    fun `Day 13`() {
        Day13(SolverMode.REAL).testDay(5580, 26200)
    }

    @Test
    fun `Day 14`() {
        Day14(SolverMode.REAL).testDay(793, 24166)
    }

    @Test
    fun `Day 15`() {
        Day15(SolverMode.REAL).testDay(5142231, 10884459367718L)
    }

    @Test
    fun `Day 16`() {
        Day16(SolverMode.REAL).testDay(1767, 2528)
    }

    @Test
    fun `Day 17`() {
        Day17(SolverMode.REAL).testDay(3211, 1589142857183L)
    }

    @Test
    fun `Day 18`() {
        Day18(SolverMode.REAL).testDay(3530, 2000)
    }

    @Test
    fun `Day 19`() {
        Day19(SolverMode.REAL).testDay(1092, 3542)
    }

    @Test
    fun `Day 20`() {
        Day20(SolverMode.REAL).testDay(4426, 8119137886612L)
    }

    @Test
    fun `Day 21`() {
        Day21(SolverMode.REAL).testDay(87457751482938L, 3221245824363L)
    }

    @Test
    fun `Day 22`() {
        Day22(SolverMode.REAL).testDay(190066, 134170)
    }

    @Test
    fun `Day 23`() {
        Day23(SolverMode.REAL).testDay(4109, 1055)
    }

    @Test
    fun `Day 24`() {
        Day24(SolverMode.REAL).testDay(242, 720)
    }

    @Test
    fun `Day 25`() {
        Day25(SolverMode.REAL).testDay("2=-0=01----22-0-1-10", "Merry Christmas!")
    }

    private fun Solver.testDay(outputA: Any, outputB: Any) {
        partA() shouldBe outputA
        partB() shouldBe outputB
    }
}