package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Date

class DateTest {

    @Test
    fun `instantiating date directly works`() {
        val date = instance<Date>()
        println(date)
    }


    @Test
    fun `date as constructor parameter works`() {
        val x = instance<DateInConstructor>()
        println(x)
    }

    @Test
    fun `nullalble date is supported`() {
        val x: NullableDateInConstructor = instance()
        Assertions.assertNull(x.date)
    }

    data class DateInConstructor(val date: Date)
    data class NullableDateInConstructor(val date: Date?)
}