package com.hannesdorfmann.instantiator

import org.junit.Assert
import org.junit.Test

class InterfaceTest {

    @Test
    fun `interface are not supported and throws exception`() {
        try {
            val x: AnInterface = instance()
            Assert.fail("Exception expected but has not been thrown")
        } catch (e: UnsupportedOperationException) {
            val expected =
                "Instantiating an abstract class or interface is not supported. " +
                        "Therefore, cannot instantiate instance of ${AnInterface::class}"
            Assert.assertEquals(expected, e.message)
        }
    }

    interface AnInterface
}