package club.pisquad.minecraft.csgrenades.grenades.decoy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DecoyHelperTest {
    @Test
    fun rpmToTickDelay() {
        val result1 = DecoyHelper.rpmToTickDelay(60)
        assertEquals(20, result1)

        val result2 = DecoyHelper.rpmToTickDelay(120)
        assertEquals(10, result2)

        val result3 = DecoyHelper.rpmToTickDelay(1)
        assertEquals(20 * 60, result3)
    }

    @Test
    fun `rpmToTickDelay round decimal`() {
        val result1 = DecoyHelper.rpmToTickDelay(666)
        assertEquals(2, result1)
    }

    class GenerateFirePatternTest {
        @Test
        fun `uniform pattern`() {
            val result = DecoyHelper.generateFirePattern(10.0, 1.0, 1.0, 1.0, 1..1).toMutableList()
            val delay = result.removeAt(0)

            assertEquals(10, result.size)

            assert(result.all { it == 20 })
        }

        @Test
        fun `non zero start delay`() {
            val result = DecoyHelper.generateFirePattern(20.0, 2.0, 2.0, 2.0, 1..1, 0.0, 4.0).toMutableList()

            val delay = result.removeAt(0)
            assertEquals(80, delay)
            assertEquals(8, result.size)
            assert(result.all { it == 40 })
        }

        @Test
        fun `reload delay`() {
            val result = DecoyHelper.generateFirePattern(10.0, 1.0, 1.0, 1.0, 1..1, 2.0, 0.0, 1).toMutableList()
            val delay = result.removeAt(0)

            assertEquals(0, delay)
            result.forEach {
                assertEquals(60, it)
            }
        }
    }
}