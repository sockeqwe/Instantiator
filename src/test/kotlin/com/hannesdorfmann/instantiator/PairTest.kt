package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Test


class PairTest {

    @Test
    fun `Pair in constructor are supported`() {
        val x: PairWrapper = instance()
        println(x)
    }

    @Test
    fun `Pair with non primitive as key are supported`() {
        val x: PairWrapper2 = instance()
        println(x)
    }

    @Test
    fun `Pair with primitive as key is generated directly`() {
        val x: Pair<Int, ListTest.Item> = instance()
        println(x)
    }

    @Test
    fun `Pair with key as class is generated directly`() {
        val x: Pair<ListTest.Item, ListTest.Item> = instance()
        println(x)

        // Null test
        val y: Pair<ListTest.Item?, ListTest.Item?> = instance()
        println(y)
    }

    @Test
    fun `nullable pair as constructor parameter is supported`() {
        val x: NullPairWrapper = instance(InstantiatorConfig(useNull = false))
        println(x)
    }

    data class PairWrapper(
        val i: Int,
        val pair: Pair<Int, ListTest.Item>,
    )

    data class PairWrapper2(
        val pair: Pair<ListTest.Item, ListTest.Item>,
    )

    data class NullPairWrapper(
        val pair: Pair<ListTest.Item, ListTest.Item>?,
    )
}
