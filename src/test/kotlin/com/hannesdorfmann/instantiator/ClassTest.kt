@file:Suppress("UNUSED_VARIABLE", "UNUSED_PARAMETER")
package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class ClassTest {

    @Test
    fun `data class consisting of primitives is generated`() {
        val x: SomeClass = instance()
        println(x)
    }

    @Test
    fun `class with empty constructor is generated`() {
        val x: EmptyConstructorClass = instance()
        println(x)
    }

    @Test
    fun `class with constructor that takes another class is generated`() {
        val x: WrappedClass = instance()
        println(x)
    }

    @Test
    fun `class with private constructor cannot be instantiated`() {
        try {
            val x: ClassWithPrivateConstructor = instance()
            fail("Exception expected but has not been thrown")
        } catch (e: Throwable) {
            val expected = "Cannot create an instance of ${ClassWithPrivateConstructor::class} because " +
                    "primary constructor has private visibility. Constructor must be public."
            assertEquals(expected, e.message)
        }
    }

    @Test
    fun `class with protected constructor cannot be instantiated`() {
        try {
            val x: ClassWithProtectedConstructor = instance()
            fail("Exception expected but has not been thrown")
        } catch (e: Throwable) {
            val expected = "Cannot create an instance of ${ClassWithProtectedConstructor::class} because " +
                    "primary constructor has protected visibility. Constructor must be public."
            assertEquals(expected, e.message)
        }
    }


    data class SomeClass(
        val i: Int,
        val f: Float,
        val d: Double,
        val b: Boolean,
        val by: Byte,
        val s: String,
        val c: Char,
        val l: Long,
        val sh: Short
    )

    class EmptyConstructorClass

    data class WrappedClass(val i: Int, val someClass: SomeClass)

    class ClassWithPrivateConstructor private constructor(i: Int)

    open class ClassWithProtectedConstructor protected constructor(i: Int)
}