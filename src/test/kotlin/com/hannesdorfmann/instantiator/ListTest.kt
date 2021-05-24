package com.hannesdorfmann.instantiator

import org.junit.Ignore
import org.junit.Test

class ListTest {


    @Test
    @Ignore
    fun `list of primitives are generated`(){
        val x : List<Int> = instance()
        println(x)
    }

    @Test
    fun `list as constructor parameter is supported`(){
        val x : ClassWithListParam = instance()
        println(x)
    }

    @Test
    fun `mutablelist as constructor parameter is supported`(){
        val x : ClassWithMutableListParam = instance()
        println(x)
    }


    data class ClassWithListParam(val i : Int, val ints : List<Int>, val items : List<Item>)
    data class ClassWithMutableListParam(val i : Int, val ints : MutableList<Int>, val items : MutableList<Item>)

    data class Item(val s : String, val b : Boolean)
}