package com.hannesdorfmann.instantiator

import org.junit.Test

class MapTest {

    @Test
    fun `Map and MutableMap in constructor is supported`(){
        val x : MapWrapper = instance()
        println(x)
    }


    @Test
    fun `Map and MutableMap with non primitives as key is supported`(){
        val x : MapWrapper2 = instance()
        println(x)
    }

    data class MapWrapper(val i : Int, val map: Map<Int, ListTest.Item>, val mutableMap: MutableMap<String, ListTest.Item>)
    data class MapWrapper2(val map: Map<ListTest.Item, ListTest.Item>, val mutableMap: MutableMap<ListTest.Item, ListTest.Item>)
}
