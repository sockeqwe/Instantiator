package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions
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



    @Test
    fun `null is set for values if config is set to useNull = true`() {
        val c: ClassWithAllOptionals = instance(InstantiatorConfig(useNull = true))
        assertEquals(ClassWithAllOptionals(null, null, null, null, null, null, null, null, null), c)
    }

    @Test
    fun `concrete value is set for values if config is set to useNull = false`() {
        val c: ClassWithAllOptionals = instance(InstantiatorConfig(useNull = false))
        Assertions.assertNotNull(c.i)
        Assertions.assertNotNull(c.f)
        Assertions.assertNotNull(c.d)
        Assertions.assertNotNull(c.b)
        Assertions.assertNotNull(c.by)
        Assertions.assertNotNull(c.s)
        Assertions.assertNotNull(c.c)
        Assertions.assertNotNull(c.l)
        Assertions.assertNotNull(c.sh)
    }

    data class ClassWithAllOptionals(
        val i: Int?,
        val f: Float?,
        val d: Double?,
        val b: Boolean?,
        val by: Byte?,
        val s: String?,
        val c: Char?,
        val l: Long?,
        val sh: Short?
    )

    data class ClassWithDefaults(val i: Int, val s: String = "someString")
}