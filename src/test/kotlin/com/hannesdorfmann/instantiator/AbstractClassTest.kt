package com.hannesdorfmann.instantiator

import org.junit.Assert
import org.junit.Test

class AbstractClassTest {


    @Test
    fun `abstract class are not supported and throw exception`() {
        try {
            val x: AbstractClass = instance()
            Assert.fail("Exception expected but has not been thrown")
        } catch (e: UnsupportedOperationException) {
            val expected = "Instantiating an abstract class or interface is not supported. " +
                    "Therefore, cannot instantiate instance of ${AbstractClass::class}"
            Assert.assertEquals(expected, e.message)
        }
    }

    abstract class AbstractClass(val i: Int) {
        abstract fun aMethod()
    }

    class AbstractClassImpl(val s: String, i: Int) : AbstractClass(i) {
        override fun aMethod() {}
    }
}