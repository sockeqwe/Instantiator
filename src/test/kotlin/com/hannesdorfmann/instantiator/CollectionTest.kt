package com.hannesdorfmann.instantiator

import org.junit.Test

class CollectionTest {

    @Test
    fun `Collection and MutableCollection in constructor is supported`(){
        val x : CollectionWrapper = instance()
        println(x)
    }

    data class CollectionWrapper(val i : Int, val collection: Collection<ListTest.Item>, val mutableCollection: MutableCollection<ListTest.Item>)
}
