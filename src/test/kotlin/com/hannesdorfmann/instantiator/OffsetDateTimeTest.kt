package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

class OffsetDateTimeTest {

    @Test
    fun `instantiating OffsetDateTime directly works`() {
        val x = instance<OffsetDateTime>()
        println(x)
    }


    @Test
    fun `OffsetDateTime as constructor parameter works`() {
        val x = instance<OffsetDateTimeInConstructor>()
        println(x)
    }

    @Test
    fun `nullalble OffsetDateTime is supported`() {
        val x: NullableOffsetDateTimeInConstructor = instance(
            InstantiatorConfig(useNull = false)
        )
        println(x) // can be null or non-null value. Depends on random
    }

    data class OffsetDateTimeInConstructor(val x: OffsetDateTime)
    data class NullableOffsetDateTimeInConstructor(val x: OffsetDateTime?)
}