package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class InstantiatorConfigTest {

    @Test
    fun `defaults are applied on class constructor if config is set to useDefaultArguments = true`() {
        val x: ClassWithDefaults = instance()
        assertEquals(x, ClassWithDefaults(x.i))
    }

    @Test
    fun `defaults are NOT applied on class constructor if config is set to useDefaultArguments = false`() {
        val x: ClassWithDefaults = instance(InstantiatorConfig(useDefaultArguments = false))
        assertNotEquals(x.s, "someString")
        println(x)
    }


    data class ClassWithDefaults(val i: Int, val s: String = "someString")
}