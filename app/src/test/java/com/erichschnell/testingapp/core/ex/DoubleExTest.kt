package com.erichschnell.testingapp.core.ex

import com.erichschnell.testingapp.presentation.core.ex.roundTo2Decimals
import org.junit.Assert.assertEquals
import org.junit.Test

class DoubleExTest {
    @Test
    fun `roundTo2Decimals should round a double to 2 decimal places`() {
        assertEquals(3.23, 3.23456673.roundTo2Decimals(), 0.0)
        assertEquals(9.56, 9.5553.roundTo2Decimals(), 0.0)
        assertEquals(6.56, 6.555673.roundTo2Decimals(), 0.0)
        assertEquals(1.03, 1.026673.roundTo2Decimals(), 0.0)
        assertEquals(4.86, 4.85826673.roundTo2Decimals(), 0.0)
    }
}
