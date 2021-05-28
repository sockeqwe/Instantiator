package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class SingletonTest {

    object SomeSingleton

    @Test
    fun `singleton instance is not re-instantiated`(){
        val x : SomeSingleton = instance()
        assertTrue(x === SomeSingleton)
    }
}