package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

class ZonedDateTimeTest {

    @Test
    fun `instantiating ZonedDateTime directly works`() {
        val x = instance<ZonedDateTime>()
        println(x)
    }


    @Test
    fun `ZonedDateTime as constructor parameter works`() {
        val x = instance<ZonedDateTimeInConstructor>()
        println(x)
    }

    @Test
    fun `nullalble ZonedDateTime is supported`() {
        val x: NullableZonedDateTimeInConstructor = instance(
            InstantiatorConfig(useNull = false)
        )
        println(x) // can be null or non-null value. Depends on random
    }

    data class ZonedDateTimeInConstructor(val x: ZonedDateTime)
    data class NullableZonedDateTimeInConstructor(val x: ZonedDateTime?)
}