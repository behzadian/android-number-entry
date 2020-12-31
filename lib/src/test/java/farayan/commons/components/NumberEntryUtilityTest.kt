package farayan.commons.components

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class NumberEntryUtilityTest {
    @Test
    fun decimalsCountWithoutAnyDecimalPoint() {
        assertEquals(0, NumberEntryUtility.decimalsCount("1"));
    }

    @Test
    fun decimalsCountWithoutAnyDecimalPointEndingWithDot() {
        assertEquals(0, NumberEntryUtility.decimalsCount("1."));
    }

    @Test
    fun decimalsCountWith1() {
        assertEquals(1, NumberEntryUtility.decimalsCount("1.1"));
    }

    @Test
    fun decimalsCountWith2() {
        assertEquals(2, NumberEntryUtility.decimalsCount("1.01"));
    }

    @Test
    fun decimalsCountWithZeroNatural() {
        assertEquals(2, NumberEntryUtility.decimalsCount("0.01"));
    }

    @Test
    fun decimalsCountWithNegative() {
        assertEquals(2, NumberEntryUtility.decimalsCount("-0.01"));
    }
}