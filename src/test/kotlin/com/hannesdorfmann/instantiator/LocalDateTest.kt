package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

class LocalDateTest {

    @Test
    fun `instantiating LocalDate directly works`() {
        val x = instance<LocalDate>()
        println(x)
    }


    @Test
    fun `LocalDate as constructor parameter works`() {
        val x = instance<LocalDateInConstructor>()
        println(x)
    }

    @Test
    fun `nullalble Instant is supported`() {
        val x: NullableLocalDateInConstructor = instance(
            InstantiatorConfig(useNull = false)
        )
        println(x) // can be null or non-null value. Depends on random
    }

    data class LocalDateInConstructor(val date: LocalDate)
    data class NullableLocalDateInConstructor(val date: LocalDate?)
}