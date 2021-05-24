package com.hannesdorfmann.instantiator

import org.junit.Test

class SetTest {

    @Test
    fun `Set and mutable set in constructor is supported`(){
        val x : SetWrapper = instance()
        println(x)
    }

    data class SetWrapper(val i : Int, val set: Set<ListTest.Item>, val mutableSet: MutableSet<ListTest.Item>)
}