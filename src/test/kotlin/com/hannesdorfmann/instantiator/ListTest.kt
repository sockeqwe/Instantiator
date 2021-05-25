package com.hannesdorfmann.instantiator

import org.junit.Test

class ListTest {

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

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `generate list of primitives directly as a return type`(){
        println(instance<List<Int>>())
        println(instance<List<Long>>())
        println(instance<List<String>>())
        println(instance<List<Double>>())
        println(instance<List<Char>>())
        println(instance<List<Float>>())
        println(instance<List<Byte>>())
        println(instance<List<Boolean>>())
        println(instance<List<Short>>())
    }

    @Test
    fun `generated list of class directly as a return type`(){
        val x : List<Item> = instance()
        println(x)
    }

    @Test
    fun `generated mutablelist of class directly as a return type`(){
        val x : MutableList<Item> = instance()
        println(x)
    }


    data class ClassWithListParam(val i : Int, val ints : List<Int>, val items : List<Item>)
    data class ClassWithMutableListParam(val i : Int, val ints : MutableList<Int>, val items : MutableList<Item>)

    data class Item(val s : String, val b : Boolean)
}