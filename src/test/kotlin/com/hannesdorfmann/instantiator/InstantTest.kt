package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant

class InstantTest {

    @Test
    fun `instantiating Instant directly works`() {
        val date = instance<Instant>()
        println(date)
    }


    @Test
    fun `Instant as constructor parameter works`() {
        val x = instance<InstantInConstructor>()
        println(x)
    }

    @Test
    fun `nullalble Instant is supported`() {
        val x: NullableInstantInConstructor = instance(InstantiatorConfig(useNull = false))
        Assertions.assertNotNull(x.date)
    }

    data class InstantInConstructor(val date: Instant)
    data class NullableInstantInConstructor(val date: Instant?)
}