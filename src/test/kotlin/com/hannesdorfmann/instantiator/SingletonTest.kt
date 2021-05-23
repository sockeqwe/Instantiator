package com.hannesdorfmann.instantiator

import org.junit.Assert
import org.junit.Test

class SingletonTest {

    object SomeSingleton

    @Test
    fun `singleton instance is not instantiated`(){
        val x : SomeSingleton = instance()
        Assert.assertTrue(x === SomeSingleton)
    }
}