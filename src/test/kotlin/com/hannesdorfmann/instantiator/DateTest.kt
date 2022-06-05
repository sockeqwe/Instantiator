package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Test
import java.util.Date

class DateTest {

    @Test
    fun `instantiating date directly works`(){
        val date = instance<Date>()
        println(date)
    }


    @Test
    fun `date as constructor parameter works`(){
        val x = instance<DateInConstructor>()
        println(x)
    }
    
    data class DateInConstructor(val date: Date)
}