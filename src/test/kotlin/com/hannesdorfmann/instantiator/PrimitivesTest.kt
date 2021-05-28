package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Test

class PrimitivesTest {

    @Test
    fun `int is generated`() {
        val x: Int = instance()
        println(x)
    }

    @Test
    fun `float is generated`() {
        val x: Float = instance()
        println(x)
    }

    @Test
    fun `char is generated`() {
        val x: Char = instance()
        println(x)
    }

    @Test
    fun `string is generated`() {
        val x: String = instance()
        println(x)
    }

    @Test
    fun `double is generated`() {
        val x: Double = instance()
        println(x)
    }

    @Test
    fun `long is generated`() {
        val x: Long = instance()
        println(x)
    }

    @Test
    fun `boolean is generated`() {
        val x: Boolean = instance()
        println(x)
    }

    @Test
    fun `short is generated`() {
        val x: Short = instance()
        println(x)
    }

    @Test
    fun `byte is generated`() {
        val x: Byte = instance()
        println(x)
    }
}