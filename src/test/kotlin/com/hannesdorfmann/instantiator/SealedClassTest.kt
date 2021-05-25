package com.hannesdorfmann.instantiator

import org.junit.Assert
import org.junit.Test

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
            Assert.fail("Exception expected but has not been thrown")
        } catch (e: UnsupportedOperationException) {
            val expected = "Sealed classes without any concrete implementation is not supported. " +
                    "Therefore, cannot instantiate ${SealedRootClassWithoutSubClasses::class}"
            Assert.assertEquals(expected, e.message)
        }
    }

    @Test
    fun `instantiateSealedSubclasses() works`() {
        val subclasses: List<SealedClass> = instantiateSealedSubclasses()
        Assert.assertEquals(2, subclasses.size)
        Assert.assertTrue(subclasses[0] is SealedClassImpl)
        Assert.assertTrue(subclasses[1] is SealedClassImpl2)
    }

    @Test
    fun `call instantiateSealedSubclasses() on a sealed class without implementations throws exception`() {
        try {
            val subclasses: List<SealedRootClassWithoutSubClasses> = instantiateSealedSubclasses()
            Assert.fail("Exception is expected but has not been thrown. Result was $subclasses")
        } catch (e: RuntimeException) {
            val expected =
                "${SealedRootClassWithoutSubClasses::class} is a sealed class or sealed interface but has no implementations of it. " +
                        "Therefore I cannot create a list of instances of the subsclasses"
            Assert.assertEquals(expected, e.message)
        }
    }

    @Test
    fun `call instantiateSealedSubclasses() on a non sealed class throws exception`() {
        try {
            val x: List<ListTest.Item> = instantiateSealedSubclasses()
            Assert.fail("Exception expected but has not been thrown. Returned $x")
        } catch (e: RuntimeException) {
            val expected = "${ListTest.Item::class} is not a sealed class or sealed interface. " +
                    "If you want to just get an instance call instance() only."
            Assert.assertEquals(expected, e.message)
        }
    }

    @Test
    fun `nested sealed classes are supported by instantiateSealedSubclasses()`(){
        val list : List<RootSealedClass> = instantiateSealedSubclasses()
        Assert.assertEquals(3, list.size)
        Assert.assertTrue(list[0] == RootSealedClassImpl1)
        Assert.assertTrue(list[1] is NestedRootSealedClassImpl1)
        Assert.assertTrue(list[2] is NestedRootSealedClassImpl2)
    }

    @Test
    fun `nested sealed class with no implementation returns empty list`(){
        val list : List<AnotherRootSealedClass> = instantiateSealedSubclasses()
        Assert.assertTrue(list.isEmpty())
    }


    sealed class SealedClass
    data class SealedClassImpl(val i: Int) : SealedClass()
    data class SealedClassImpl2(val s: String) : SealedClass()

    sealed class SealedRootClassWithoutSubClasses

    sealed class RootSealedClass
    object RootSealedClassImpl1 : RootSealedClass()
    sealed class NestedRootSealedClass : RootSealedClass()
    data class NestedRootSealedClassImpl1(val i : Int) : NestedRootSealedClass()
    data class NestedRootSealedClassImpl2(val i : Int) : NestedRootSealedClass()

    sealed class AnotherRootSealedClass
    sealed class NestedAnotherRootSealedClass : AnotherRootSealedClass()
}