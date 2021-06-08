package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Test

class CollectionTest {

    @Test
    fun `Collection and MutableCollection in constructor is supported`(){
        val x : CollectionWrapper = instance()
        println(x)
    }

    @Test
    fun `Nullable Collection in constructor is supported`(){
        val x : CollectionNullWrapper = instance(InstantiatorConfig(useNull = false))
        println(x)
    }

    data class CollectionWrapper(val i : Int, val collection: Collection<ListTest.Item>, val mutableCollection: MutableCollection<ListTest.Item>)
    data class CollectionNullWrapper(val i : Int, val collection: Collection<ListTest.Item>?, val mutableCollection: MutableCollection<ListTest.Item>?)
}
