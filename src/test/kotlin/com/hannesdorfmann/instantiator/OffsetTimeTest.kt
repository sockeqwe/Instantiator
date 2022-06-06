package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZonedDateTime
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

class OffsetTimeTest {

    @Test
    fun `instantiating OffsetTime directly works`() {
        val x = instance<OffsetTime>()
        println(x)
    }


    @Test
    fun `OffsetTime as constructor parameter works`() {
        val x = instance<OffsetTimeInConstructor>()
        println(x)
    }

    @Test
    fun `nullalble OffsetTime is supported`() {
        val x: NullableOffsetTimeInConstructor = instance(
            InstantiatorConfig(useNull = false)
        )
        println(x) // can be null or non-null value. Depends on random
    }

    data class OffsetTimeInConstructor(val x: OffsetTime)
    data class NullableOffsetTimeInConstructor(val x: OffsetTime?)
}