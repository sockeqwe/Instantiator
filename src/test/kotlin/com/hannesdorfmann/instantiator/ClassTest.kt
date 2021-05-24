package com.hannesdorfmann.instantiator

import org.junit.Test

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

}