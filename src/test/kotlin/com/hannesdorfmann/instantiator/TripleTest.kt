package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Test


class TripleTest {

    @Test
    fun `Triple in constructor are supported`() {
        val x: TripleWrapper = instance()
        println(x)
    }

    @Test
    fun `Triple with non primitive values are supported`() {
        val x: TripleWrapper2 = instance()
        println(x)
    }

    @Test
    fun `Triple with primitive values is generated directly`() {
        val x: Triple<Int, String, Double> = instance()
        println(x)
    }

    @Test
    fun `Triple with each value as class is generated directly`() {
        val x: Triple<ListTest.Item, ListTest.Item, ListTest.Item> = instance()
        println(x)

        // Null test
        val y: Triple<ListTest.Item?, ListTest.Item?, ListTest.Item?> = instance()
        println(y)
    }

    @Test
    fun `nullable Triple as constructor parameter is supported`() {
        val x: NullTripleWrapper = instance(InstantiatorConfig(useNull = false))
        println(x)
    }

    data class TripleWrapper(
        val i: Int,
        val Triple: Triple<Int, String, Double>,
    )

    data class TripleWrapper2(
        val Triple: Triple<ListTest.Item, ListTest.Item, ListTest.Item>,
    )

    data class NullTripleWrapper(
        val Triple: Triple<ListTest.Item, ListTest.Item, ListTest.Item>?,
    )
}
