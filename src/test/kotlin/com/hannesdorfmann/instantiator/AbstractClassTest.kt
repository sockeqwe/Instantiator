package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test


class AbstractClassTest {


    @Test
    fun `abstract class are not supported and throw exception`() {
        try {
            val x: AbstractClass = instance()
            fail("Exception expected but has not been thrown")
        } catch (e: UnsupportedOperationException) {
            val expected = "Instantiating an abstract class or interface is not supported. " +
                    "Therefore, cannot instantiate instance of ${AbstractClass::class}"
            assertEquals(expected, e.message)
        }
    }

    abstract class AbstractClass(val i: Int) {
        abstract fun aMethod()
    }

    class AbstractClassImpl(val s: String, i: Int) : AbstractClass(i) {
        override fun aMethod() {}
    }
}