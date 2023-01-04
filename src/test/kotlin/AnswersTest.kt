import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AnswersTest {
    @Test
    fun `Day 1`() {
        Day1(SolverMode.EXAMPLE).testDay(24000, 45000)
        Day1(SolverMode.REAL).testDay(74711, 209481)
        Day1(SolverMode.REAL_A).testDay(71471, 211189)
    }

    @Test
    fun `Day 2`() {
        Day2(SolverMode.EXAMPLE).testDay(15, 12)
        Day2(SolverMode.REAL).testDay(12794, 14979)
        Day2(SolverMode.REAL_A).testDay(13446, 13509)
    }

    @Test
    fun `Day 3`() {
        Day3(SolverMode.EXAMPLE).testDay(157, 70)
        Day3(SolverMode.REAL).testDay(7826, 2577)
        Day3(SolverMode.REAL_A).testDay(8088, 2522)
    }

    @Test
    fun `Day 4`() {
        Day4(SolverMode.EXAMPLE).testDay(2, 4)
        Day4(SolverMode.REAL).testDay(515, 883)
        Day4(SolverMode.REAL_A).testDay(595, 952)
    }

    @Test
    fun `Day 5`() {
        Day5(SolverMode.EXAMPLE).testDay("CMZ", "MCD")
        Day5(SolverMode.REAL).testDay("VPCDMSLWJ", "TPWCGNCCG")
        Day5(SolverMode.REAL_A).testDay("TBVFVDZPN", "VLCWHTDSZ")
    }

    @Test
    fun `Day 6`() {
        Day6(SolverMode.EXAMPLE).testDay(7, 19)
        Day6(SolverMode.REAL).testDay(1343, 2193)
        Day6(SolverMode.REAL_A).testDay(1544, 2145)
    }

    @Test
    fun `Day 7`() {
        Day7(SolverMode.EXAMPLE).testDay(95437, 24933642)
        Day7(SolverMode.REAL).testDay(1792222, 1112963)
        Day7(SolverMode.REAL_A).testDay(1453349, 2948823)
    }

    @Test
    fun `Day 8`() {
        Day8(SolverMode.EXAMPLE).testDay(21, 8)
        Day8(SolverMode.REAL).testDay(1672, 327180)
        Day8(SolverMode.REAL_A).testDay(1816, 383520)
    }

    @Test
    fun `Day 9`() {
        Day9(SolverMode.EXAMPLE).testDay(13, 1)
        Day9(SolverMode.REAL).testDay(6090, 2566)
        Day9(SolverMode.REAL_A).testDay(6642, 2765)
    }

    @Test
    fun `Day 10`() {
        Day10(SolverMode.EXAMPLE).testDay(
            13140, """
##  ##  ##  ##  ##  ##  ##  ##  ##  ##  
###   ###   ###   ###   ###   ###   ### 
####    ####    ####    ####    ####    
#####     #####     #####     #####     
######      ######      ######      ####
#######       #######       #######     """
        )

        Day10(SolverMode.REAL).testDay(
            13740, """
#### #  # ###  ###  #### ####  ##  #    
   # #  # #  # #  # #    #    #  # #    
  #  #  # #  # #  # ###  ###  #    #    
 #   #  # ###  ###  #    #    #    #    
#    #  # #    # #  #    #    #  # #    
####  ##  #    #  # #    ####  ##  #### """
        )

        Day10(SolverMode.REAL_A).testDay(
            13760, """
###  #### #  # ####  ##  ###  #### #### 
#  # #    # #     # #  # #  # #    #    
#  # ###  ##     #  #    #  # ###  ###  
###  #    # #   #   #    ###  #    #    
# #  #    # #  #    #  # #    #    #    
#  # #    #  # ####  ##  #    #### #    """
        )
    }

    @Test
    fun `Day 11`() {
        Day11(SolverMode.EXAMPLE).testDay(10605, 2713310158)
        Day11(SolverMode.REAL).testDay(99852, 25935263541)
        Day11(SolverMode.REAL_A).testDay(120056, 21816744824)
    }

    @Test
    fun `Day 12`() {
        Day12(SolverMode.EXAMPLE).testDay(31, 29)
        Day12(SolverMode.REAL).testDay(383, 377)
        Day12(SolverMode.REAL_A).testDay(472, 465)
    }

    @Test
    fun `Day 13`() {
        Day13(SolverMode.EXAMPLE).testDay(13, 140)
        Day13(SolverMode.REAL).testDay(5580, 26200)
        Day13(SolverMode.REAL_A).testDay(5013, 25038)
    }

    @Test
    fun `Day 14`() {
        Day14(SolverMode.EXAMPLE).testDay(24, 93)
        Day14(SolverMode.REAL).testDay(793, 24166)
        Day14(SolverMode.REAL_A).testDay(672, 26831)
    }

    @Test
    fun `Day 15`() {
        Day15(SolverMode.EXAMPLE).testDay(26, 56000011)
        Day15(SolverMode.REAL).testDay(5142231, 10884459367718)
        Day15(SolverMode.REAL_A).testDay(4827924, 12977110973564)
    }

    @Test
    fun `Day 16`() {
        Day16(SolverMode.EXAMPLE).testDay(1651, 1707)
        Day16(SolverMode.REAL).testDay(1767, 2528)
        Day16(SolverMode.REAL_A).testDay(2087, 2591)
    }

    @Test
    fun `Day 17`() {
        Day17(SolverMode.EXAMPLE).testDay(3068, 1514285714288)
        Day17(SolverMode.REAL).testDay(3211, 1589142857183)
        Day17(SolverMode.REAL_A).testDay(3219, 1582758620701)
    }

    @Test
    fun `Day 18`() {
        Day18(SolverMode.EXAMPLE).testDay(64, 58)
        Day18(SolverMode.REAL).testDay(3530, 2000)
        Day18(SolverMode.REAL_A).testDay(4450, 2564)
    }

    @Test
    fun `Day 19`() {
        Day19(SolverMode.EXAMPLE).testDay(33, 3410)
        Day19(SolverMode.REAL).testDay(1092, 3542)
        Day19(SolverMode.REAL_A).testDay(1565, 10672)
    }

    @Test
    fun `Day 20`() {
        Day20(SolverMode.EXAMPLE).testDay(3, 1623178306)
        Day20(SolverMode.REAL).testDay(4426, 8119137886612L)
        Day20(SolverMode.REAL_A).testDay(8764, 535648840980)
    }

    @Test
    fun `Day 21`() {
        Day21(SolverMode.EXAMPLE).testDay(152, 301)
        Day21(SolverMode.REAL).testDay(87457751482938L, 3221245824363L)
        Day21(SolverMode.REAL_A).testDay(309248622142100, 3757272361782)
    }

    @Test
    fun `Day 22`() {
        Day22(SolverMode.EXAMPLE).testDay(6032, "Not implemented")
        Day22(SolverMode.REAL).testDay(190066, 134170)
        Day22(SolverMode.REAL_A).testDay(197160, 145065)
    }

    @Test
    fun `Day 23`() {
        Day23(SolverMode.EXAMPLE).testDay(110, 20)
        Day23(SolverMode.REAL).testDay(4109, 1055)
        Day23(SolverMode.REAL_A).testDay(4241, 1079)
    }

    @Test
    fun `Day 24`() {
        Day24(SolverMode.EXAMPLE).testDay(18, 54)
        Day24(SolverMode.REAL).testDay(242, 720)
        Day24(SolverMode.REAL_A).testDay(257, 828)
    }

    @Test
    fun `Day 25`() {
        Day25(SolverMode.EXAMPLE).testDay("2=-1=0", "Merry Christmas!")
        Day25(SolverMode.REAL).testDay("2=-0=01----22-0-1-10", "Merry Christmas!")
        Day25(SolverMode.REAL_A).testDay("2-0=11=-0-2-1==1=-22", "Merry Christmas!")
    }

    private fun Solver.testDay(outputA: Any, outputB: Any) {
        partA() shouldBe outputA
        partB() shouldBe outputB
    }
}