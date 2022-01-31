package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Test


class TripleTest {

    @Test
    fun `Triple in constructor is supported`() {
        data class TripleWrapper(
            val i: Int,
            val Triple: Triple<Int, String, Double>,
        )

        val x: TripleWrapper = instance()
        println(x)
    }

    @Test
    fun `Triple with primitive nullable values is supported`() {
        data class TripleWrapper(
            val Triple: Triple<Int?, String?, Double?>,
        )

        val x: TripleWrapper = instance()
        println(x)
    }

    @Test
    fun `Triple with non primitive values is supported`() {
        data class TripleWrapper(
            val Triple: Triple<ListTest.Item, ListTest.Item, ListTest.Item>,
        )

        val x: TripleWrapper = instance()
        println(x)
    }

    @Test
    fun `Triple with non primitive nullable values is supported`() {
        data class TripleWrapper(
            val Triple: Triple<ListTest.Item?, ListTest.Item?, ListTest.Item?>,
        )

        val x: TripleWrapper = instance()
        println(x)
    }

    @Test
    fun `nullable Triple as constructor parameter is supported`() {
        data class NullTripleWrapper(
            val Triple: Triple<ListTest.Item, ListTest.Item, ListTest.Item>?,
        )

        val x: NullTripleWrapper = instance(InstantiatorConfig(useNull = false))
        println(x)
    }

    @Test
    fun `Triple with primitive values is generated directly`() {
        val x: Triple<Int, String, Double> = instance()
        println(x)

        // Null test
        val y: Triple<Int?, String?, Double?> = instance()
        println(y)
    }

    @Test
    fun `Triple with each value as class is generated directly`() {
        val x: Triple<ListTest.Item, ListTest.Item, ListTest.Item> = instance()
        println(x)

        // Null test
        val y: Triple<ListTest.Item?, ListTest.Item?, ListTest.Item?> = instance()
        println(y)
    }
}
