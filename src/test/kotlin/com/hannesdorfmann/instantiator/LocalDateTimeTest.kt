package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

class LocalDateTimeTest {

    @Test
    fun `instantiating LocalDateTime directly works`() {
        val x = instance<LocalDateTime>()
        println(x)
    }


    @Test
    fun `Instant as constructor parameter works`() {
        val x = instance<LocalDateTimeInConstructor>()
        println(x)
    }

    @Test
    fun `nullalble Instant is supported`() {
        val x: NullableLocalDateTimeInConstructor = instance(
            InstantiatorConfig(useNull = false)
        )
        println(x) // can be null or non-null value. Depends on random
    }

    data class LocalDateTimeInConstructor(val date: LocalDateTime)
    data class NullableLocalDateTimeInConstructor(val date: LocalDateTime?)
}