@file:Suppress("UNUSED_VARIABLE")
package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class SealedClassTest {

    @Test
    fun `sealed class randomly instantiates a subclass`() {
        val x: SealedClass = instance()
        println(x)
    }

    @Test
    fun `sealed class without subclass cannot be instantiated and throws exception`() {
        try {
            val x: SealedRootClassWithoutSubClasses = instance()
            fail("Exception expected but has not been thrown")
        } catch (e: UnsupportedOperationException) {
            val expected = "Sealed classes without any concrete implementation is not supported. " +
                    "Therefore, cannot instantiate ${SealedRootClassWithoutSubClasses::class}"
            assertEquals(expected, e.message)
        }
    }

    @Test
    fun `instantiateSealedSubclasses() works`() {
        val subclasses: List<SealedClass> = instantiateSealedSubclasses()
        assertEquals(2, subclasses.size)
        assertTrue(subclasses[0] is SealedClassImpl)
        assertTrue(subclasses[1] is SealedClassImpl2)
    }

    @Test
    fun `call instantiateSealedSubclasses() on a sealed class without implementations throws exception`() {
        try {
            val subclasses: List<SealedRootClassWithoutSubClasses> = instantiateSealedSubclasses()
            fail("Exception is expected but has not been thrown. Result was $subclasses")
        } catch (e: RuntimeException) {
            val expected =
                "${SealedRootClassWithoutSubClasses::class} is a sealed class or sealed interface but has no implementations of it. " +
                        "Therefore I cannot create a list of instances of the subsclasses"
            assertEquals(expected, e.message)
        }
    }

    @Test
    fun `call instantiateSealedSubclasses() on a non sealed class throws exception`() {
        try {
            val x: List<ListTest.Item> = instantiateSealedSubclasses()
            fail("Exception expected but has not been thrown. Returned $x")
        } catch (e: RuntimeException) {
            val expected = "${ListTest.Item::class} is not a sealed class or sealed interface. " +
                    "If you want to just get an instance call instance() only."
            assertEquals(expected, e.message)
        }
    }

    @Test
    fun `nested sealed classes are supported by instantiateSealedSubclasses()`() {
        val list: List<RootSealedClass> = instantiateSealedSubclasses()
        assertEquals(3, list.size)
        assertTrue(list[0] == RootSealedClassImpl1)
        assertTrue(list[1] is NestedRootSealedClassImpl1)
        assertTrue(list[2] is NestedRootSealedClassImpl2)
    }

    @Test
    fun `nested sealed class with no implementation returns empty list`() {
        val list: List<AnotherRootSealedClass> = instantiateSealedSubclasses()
        assertTrue(list.isEmpty())
    }


    sealed class SealedClass
    data class SealedClassImpl(val i: Int) : SealedClass()
    data class SealedClassImpl2(val s: String) : SealedClass()

    sealed class SealedRootClassWithoutSubClasses

    sealed class RootSealedClass
    object RootSealedClassImpl1 : RootSealedClass()
    sealed class NestedRootSealedClass : RootSealedClass()
    data class NestedRootSealedClassImpl1(val i: Int) : NestedRootSealedClass()
    data class NestedRootSealedClassImpl2(val i: Int) : NestedRootSealedClass()

    sealed class AnotherRootSealedClass
    sealed class NestedAnotherRootSealedClass : AnotherRootSealedClass()
}