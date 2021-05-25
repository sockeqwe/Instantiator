package com.hannesdorfmann.instantiator

import org.junit.Test

class SetTest {

    @Test
    fun `Set and mutable set in constructor is supported`(){
        val x : SetWrapper = instance()
        println(x)
    }

    @Test
    fun `Set is computed directly`(){
        val x : Set<ListTest.Item> = instance()
        println(x)
    }

    @Test
    fun `MutableSet is computed directly`(){
        val x : MutableSet<ListTest.Item> = instance()
        println(x)
    }

    data class SetWrapper(val i : Int, val set: Set<ListTest.Item>, val mutableSet: MutableSet<ListTest.Item>)
}