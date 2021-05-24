package com.hannesdorfmann.instantiator

import org.junit.Assert
import org.junit.Test

class SealedClassTest {

    @Test
    fun `sealed class randomly instantiates a subclass`(){
        val x : SealedClass = instance()
        println(x)
    }

    @Test
    fun `sealed class without subclass cannot be instantiated and throws exception`(){
        try {
            val x: SealedRootClassWithoutSubClasses = instance()
            Assert.fail("Exception expected but has not been thrown")
        } catch (e : UnsupportedOperationException){
            val expected = "Sealed classes without any concrete implementation is not supported. " +
                    "Therefore, cannot instantiate ${SealedRootClassWithoutSubClasses::class}"
           Assert.assertEquals(expected, e.message)
        }
    }


    sealed class SealedClass
    class SealedClassImpl(val i : Int) : SealedClass()

    sealed class SealedRootClassWithoutSubClasses
}