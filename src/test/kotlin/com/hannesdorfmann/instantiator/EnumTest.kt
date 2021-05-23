package com.hannesdorfmann.instantiator

import org.junit.Test

class EnumTest {

    enum class SomeEnum{
        A,
        B,
        C
    }

    @Test
    fun `generating enum works`(){
        val x : SomeEnum = instance()
        println(x)
    }
}