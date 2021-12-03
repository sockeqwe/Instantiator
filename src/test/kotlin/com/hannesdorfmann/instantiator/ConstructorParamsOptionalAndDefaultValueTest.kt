package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ConstructorParamsOptionalAndDefaultValueTest {

    @Test
    fun `constructor params are optional and null at the same time (and both enabled) then default values are used`() {
        val config = InstantiatorConfig(useNull = true, useDefaultArguments = true)

        val instance = instance<ConstructorWithOptionalAndDefaultValue>(config)

        Assertions.assertEquals("def", instance.s)
        Assertions.assertEquals(-42, instance.i)
    }

    @Test
    fun `some constructor params are optional and null at the same time (and both enabled) then default values are used`() {
        val config = InstantiatorConfig(useNull = true, useDefaultArguments = true)

        val instance = instance<ConstructorWithSomeOptionalAndDefaultValue>(config)

        Assertions.assertNull(instance.s)
        Assertions.assertEquals(-42, instance.i)
    }

    data class ConstructorWithOptionalAndDefaultValue(
        val s: String? = "def",
        val i: Int? = -42
    )

    data class ConstructorWithSomeOptionalAndDefaultValue(
        val s: String?,
        val i: Int? = -42
    )
}
