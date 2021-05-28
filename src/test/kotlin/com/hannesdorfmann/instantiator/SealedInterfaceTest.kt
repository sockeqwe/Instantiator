package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail

class SealedInterfaceTest {

    @Test
    fun `sealed interface randomly instantiates a subclass`() {
        val x: SealedClassTest.SealedClass = instance()
        println(x)
    }

    @Test
    fun `sealed interface without subclass cannot be instantiated and throws exception`() {
        try {
            val x: SealedInterfaceWithoutSubClasses = instance()
            fail("Exception expected but has not been thrown. Returned $x")
        } catch (e: UnsupportedOperationException) {
            val expected = "Sealed classes without any concrete implementation is not supported. " +
                    "Therefore, cannot instantiate ${SealedInterfaceWithoutSubClasses::class}"
            assertEquals(expected, e.message)
        }
    }

    @Test
    fun `instantiateSealedSubclasses() works`() {
        val subclasses: List<SealedInterface> = instantiateSealedSubclasses()
        assertEquals(2, subclasses.size)
        assertTrue(subclasses[0] is SealedInterfaceImpl1)
        assertTrue(subclasses[1] is SealedInterfaceImpl2)
    }

    @Test
    fun `call instantiateSealedSubclasses() on a sealed interface without implementations throws exception`() {
        try {
            val subclasses: List<SealedInterfaceWithoutSubClasses> = instantiateSealedSubclasses()
            fail("Exception is expected but has not been thrown. Result was $subclasses")
        } catch (e: RuntimeException) {
            val expected =
                "${SealedInterfaceWithoutSubClasses::class} is a sealed class or sealed interface but has no implementations of it. " +
                        "Therefore I cannot create a list of instances of the subsclasses"
            assertEquals(expected, e.message)
        }
    }

    @Test
    fun `nested sealed classes are supported by instantiateSealedSubclasses()`(){
        val list : List<RootSealedInterface> = instantiateSealedSubclasses()
        assertEquals(3, list.size)
        assertTrue(list[0] == RootSealedClassImpl1)
        assertTrue(list[1] is NestedRootSealedInterfaceImpl1)
        assertTrue(list[2] is NestedRootSealedInterfaceImpl2)
    }

    @Test
    fun `nested sealed class with no implementation returns empty list`(){
        val list : List<AnotherRootSealedInterface> = instantiateSealedSubclasses()
        assertTrue(list.isEmpty())
    }


    sealed interface SealedInterface
    data class SealedInterfaceImpl1(val i: Int) : SealedInterface
    data class SealedInterfaceImpl2(val s: String) : SealedInterface

    sealed interface SealedInterfaceWithoutSubClasses

    sealed interface RootSealedInterface
    object RootSealedClassImpl1 : RootSealedInterface
    sealed interface NestedRootSealedInterface : RootSealedInterface
    data class NestedRootSealedInterfaceImpl1(val i : Int) : NestedRootSealedInterface
    data class NestedRootSealedInterfaceImpl2(val i : Int) : NestedRootSealedInterface

    sealed interface AnotherRootSealedInterface
    sealed interface NestedAnotherRootSealedClass : AnotherRootSealedInterface
}