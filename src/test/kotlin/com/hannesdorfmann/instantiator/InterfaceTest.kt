@file:Suppress("UNUSED_VARIABLE")
package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class InterfaceTest {

    @Test
    fun `interface are not supported and throws exception`() {
        try {
            val x: AnInterface = instance()
            fail("Exception expected but has not been thrown")
        } catch (e: UnsupportedOperationException) {
            val expected =
                "Instantiating an abstract class or interface is not supported. " +
                        "Therefore, cannot instantiate instance of ${AnInterface::class}"
            assertEquals(expected, e.message)
        }
    }

    interface AnInterface
}