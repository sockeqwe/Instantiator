package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class NullSupportTest {

    @Test
    fun `null is set for values if config is set to useNull = true`() {
        val c: ClassWithAllOptionals = instance(InstantiatorConfig(useNull = true))
        assertEquals(ClassWithAllOptionals(null, null, null, null, null, null, null, null, null), c)
    }

    @Test
    fun `concrete value is set for values if config is set to useNull = false`() {
        val c: ClassWithAllOptionals = instance(InstantiatorConfig(useNull = false))
        assertNotNull(c.i)
        assertNotNull(c.f)
        assertNotNull(c.d)
        assertNotNull(c.b)
        assertNotNull(c.by)
        assertNotNull(c.s)
        assertNotNull(c.c)
        assertNotNull(c.l)
        assertNotNull(c.sh)
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
}

