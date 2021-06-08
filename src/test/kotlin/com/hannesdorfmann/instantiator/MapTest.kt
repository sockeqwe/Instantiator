package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Test


class MapTest {

    @Test
    fun `Map and MutableMap in constructor are supported`() {
        val x: MapWrapper = instance()
        println(x)
    }


    @Test
    fun `Map and MutableMap with non primitives as key are supported`() {
        val x: MapWrapper2 = instance()
        println(x)
    }

    @Test
    fun `Map with Primitiv as key is generated directly`() {
        val x: Map<Int, ListTest.Item> = instance()
        println(x)
    }

    @Test
    fun `Map with key as class is generated directly`() {
        val x: Map<ListTest.Item, ListTest.Item> = instance()
        println(x)

        // Null test
        val y: Map<ListTest.Item?, ListTest.Item?> = instance()
        println(y)
    }

    @Test
    fun `MutableMap with key as class is generated directly`() {
        val x: MutableMap<ListTest.Item, ListTest.Item> = instance()
        println(x)
    }

    @Test
    fun `nullable MutableMap as constructor parameter is supported`() {
        val x: NullMapWrapper = instance(InstantiatorConfig(useNull = false))
        println(x)
    }

    data class MapWrapper(
        val i: Int,
        val map: Map<Int, ListTest.Item>,
        val mutableMap: MutableMap<String, ListTest.Item>
    )

    data class MapWrapper2(
        val map: Map<ListTest.Item, ListTest.Item>,
        val mutableMap: MutableMap<ListTest.Item, ListTest.Item>
    )

    data class NullMapWrapper(
        val map: Map<ListTest.Item, ListTest.Item>?,
        val mutableMap: MutableMap<ListTest.Item, ListTest.Item>?
    )
}
