package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Test

class SetTest {

    @Test
    fun `Set and MutableSet in constructor is supported`(){
        val x : SetWrapper = instance()
        println(x)
    }

    @Test
    fun `Set is computed directly`(){
        val x : Set<ListTest.Item> = instance()
        println(x)

        // Null test
        val y : Set<ListTest.Item?> = instance()
        println(y)
    }

    @Test
    fun `MutableSet is computed directly`(){
        val x : MutableSet<ListTest.Item> = instance()
        println(x)
    }

    @Test
    fun `Set and MutableSet that can be null as constructor parameter are supported`(){
        val x : SetNullWrapper = instance(InstantiatorConfig(useNull = false))
        println(x)
    }

    data class SetWrapper(val i : Int, val set: Set<ListTest.Item>, val mutableSet: MutableSet<ListTest.Item>)
    data class SetNullWrapper(val i : Int, val set: Set<ListTest.Item>?, val mutableSet: MutableSet<ListTest.Item>?)
}