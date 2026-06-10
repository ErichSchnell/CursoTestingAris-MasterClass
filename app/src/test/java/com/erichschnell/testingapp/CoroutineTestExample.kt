package com.erichschnell.testingapp

import kotlinx.coroutines.test.runTest
import org.junit.Test

class CoroutineTestExample {
    private suspend fun suma(
        a: Int,
        b: Int,
    ): Int = a + b

    @Test
    fun testSuma() =
        runTest {
            val result = suma(1, 2)
            assert(result == 3)
        }
}
