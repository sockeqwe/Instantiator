package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Test


class PairTest {

    @Test
    fun `Pair in constructor is supported`() {
        data class PairWrapper(
            val i: Int,
            val pair: Pair<Int, ListTest.Item>,
        )

        val x: PairWrapper = instance()
        println(x)
    }

    @Test
    fun `Pair with non primitive as key is supported`() {
        data class PairWrapper(
            val pair: Pair<ListTest.Item, ListTest.Item>,
        )

        val x: PairWrapper = instance()
        println(x)
    }

    @Test
    fun `Pair with primitive nullable as key is supported`() {
        data class PairWrapper(
            val pair: Pair<Int?, ListTest.Item>
        )

        val x: PairWrapper = instance()
        println(x)
    }

    @Test
    fun `Pair with non primitive nullable as key and nullable value as class is supported`() {
        data class PairWrapper(
            val pair: Pair<ListTest.Item?, ListTest.Item?>
        )

        val x: PairWrapper = instance()
        println(x)
    }

    @Test
    fun `nullable Pair as constructor parameter is supported`() {
        data class NullPairWrapper(
            val pair: Pair<ListTest.Item, ListTest.Item>?,
        )

        val x: NullPairWrapper = instance(InstantiatorConfig(useNull = false))
        println(x)
    }

    @Test
    fun `Pair with primitive as key is generated directly`() {
        val x: Pair<Int, ListTest.Item> = instance()
        println(x)

        // Null test
        val y: Pair<Int?, ListTest.Item?> = instance()
        println(y)
    }

    @Test
    fun `Pair with key as class is generated directly`() {
        val x: Pair<ListTest.Item, ListTest.Item> = instance()
        println(x)

        // Null test
        val y: Pair<ListTest.Item?, ListTest.Item?> = instance()
        println(y)
    }
}
