package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

class LocalTimeTest {

    @Test
    fun `instantiating LocalTime directly works`() {
        val x = instance<LocalTime>()
        println(x)
    }


    @Test
    fun `Instant as constructor parameter works`() {
        val x = instance<LocalTimeInConstructor>()
        println(x)
    }

    @Test
    fun `nullalble Instant is supported`() {
        val x: NullableLocalTimeInConstructor = instance(
            InstantiatorConfig(useNull = false)
        )
        println(x) // can be null or non-null value. Depends on random
    }

    data class LocalTimeInConstructor(val x: LocalTime)
    data class NullableLocalTimeInConstructor(val x: LocalTime?)
}