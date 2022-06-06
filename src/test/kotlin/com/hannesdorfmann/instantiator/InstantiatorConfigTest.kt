package com.hannesdorfmann.instantiator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class InstantiatorConfigTest {

    @Test
    fun `defaults are applied on class constructor if config is set to useDefaultArguments = true`() {
        val x: ClassWithDefaults = instance()
        assertEquals(x, ClassWithDefaults(x.i))
    }

    @Test
    fun `defaults are NOT applied on class constructor if config is set to useDefaultArguments = false`() {
        val x: ClassWithDefaults = instance(InstantiatorConfig(useDefaultArguments = false))
        assertNotEquals(x.s, "someString")
        println(x)
    }


    @Test
    fun `null is set for values if config is set to useNull = true`() {
        val c: ClassWithAllOptionals = instance(InstantiatorConfig(useNull = true))
        assertEquals(ClassWithAllOptionals(null, null, null, null, null, null, null, null, null), c)
    }

    @Test
    fun `concrete value is set for values if config is set to useNull = false`() {
        val c: ClassWithAllOptionals = instance(InstantiatorConfig(useNull = false))
        Assertions.assertNotNull(c.i)
        Assertions.assertNotNull(c.f)
        Assertions.assertNotNull(c.d)
        Assertions.assertNotNull(c.b)
        Assertions.assertNotNull(c.by)
        Assertions.assertNotNull(c.s)
        Assertions.assertNotNull(c.c)
        Assertions.assertNotNull(c.l)
        Assertions.assertNotNull(c.sh)
    }

    @Test
    fun `add() InstanceFactory overrides existing factory and produces a new InstanceConfig instance`() {
        val customString = "String was produced by custom InstanceFactory"
        val customStringInstanceFactory = object : InstantiatorConfig.NonNullableInstanceFactory<String> {
            override val type: KType = String::class.createType()
            override fun createInstance(random: Random): String = customString
        }

        val config1 = InstantiatorConfig(useNull = false, useDefaultArguments = false)
        val config2 = config1.add(customStringInstanceFactory)
        assertNotEquals(customString, instance<String>(config1))
        assertEquals(customString, instance<String>(config2))
        assertEquals(config1.useNull, config2.useNull)
        assertEquals(config1.useDefaultArguments, config2.useDefaultArguments)
        assertEquals(config1.instanceFactory.size, config2.instanceFactory.size)
    }

    @Test
    fun `add() creates a new InstanceFactory and produces a new InstanceConfig instance`() {

        val interfaceInstance = object : TestInterface {}
        val customStringInstanceFactory = object : InstantiatorConfig.NonNullableInstanceFactory<TestInterface> {
            override val type: KType = TestInterface::class.createType()
            override fun createInstance(random: Random): TestInterface = interfaceInstance
        }

        val config1 = InstantiatorConfig(useNull = false, useDefaultArguments = false)
        val config2 = config1.add(customStringInstanceFactory)

        try {
            instance<TestInterface>(config1)
            Assertions.fail("Exception expected to be thrown because config doesn't has InstanceFactory for TestInterface")
        } catch (e: UnsupportedOperationException) {
        }
        assertEquals(interfaceInstance, instance<TestInterface>(config2))
        assertEquals(config1.useNull, config2.useNull)
        assertEquals(config1.useDefaultArguments, config2.useDefaultArguments)
        assertEquals(config1.instanceFactory.size + 1, config2.instanceFactory.size)
    }

    @Test
    internal fun `setting random creates a stable object`() {
        val config1 = InstantiatorConfig(useNull = false, random = Random(0))
        val config2 = InstantiatorConfig(useNull = false, random = Random(0))

        val instance1 = instance<ClassWithAllOptionals>(config1)
        val instance2 = instance<ClassWithAllOptionals>(config2)

        assertEquals(instance1, instance2)
    }

    @Test
    fun `toNullableInstanceFactory() with mode=ALWAYS_NULL return always null`() {
        val nullableIntFactory = IntFactory().toNullableInstanceFactory(
            mode = ToNullableInstaceFactoryMode.ALWAYS_NULL
        )

        assertEquals(Int::class.createType(nullable = true), nullableIntFactory.type)
        for (i in 0..100) {
            assertNull(nullableIntFactory.createInstance(Random))
        }
    }

    @Test
    fun `toNullableInstanceFactory() with mode=NEVER_NULL return always not null value`() {
        val nullableIntFactory = IntFactory().toNullableInstanceFactory(
            mode = ToNullableInstaceFactoryMode.NEVER_NULL
        )

        assertEquals(Int::class.createType(nullable = true), nullableIntFactory.type)
        for (i in 0..100) {
            assertEquals(23, nullableIntFactory.createInstance(Random))
        }
    }

    @Test
    fun `toNullableInstanceFactory() with mode=RANDOM returns randomly null or non-null value`() {
        val nullableIntFactory = IntFactory().toNullableInstanceFactory(
            mode = ToNullableInstaceFactoryMode.RANDOM
        )

        assertEquals(Int::class.createType(nullable = true), nullableIntFactory.type)

        // It is fair to assume that when create 1000 values randomly at least
        // 1 should be null
        // and 1 should be non-null (23)
        val generatedValues = (0..1000).map {
            nullableIntFactory.createInstance(Random)
        }

        assertTrue(generatedValues.contains(23))
        assertTrue(generatedValues.contains(null))

    }


    private class IntFactory : InstantiatorConfig.NonNullableInstanceFactory<Int> {
        override val type: KType = Int::class.createType()

        override fun createInstance(random: Random): Int = 23
    }

    data class ClassWithAllOptionals(
        val i: Int?,
        val f: Float?,
        val d: Double?,
        val b: Boolean?,
        val by: Byte?,
        val s: String?,
        val c: Char?,
        val l: Long?,
        val sh: Short?
    )

    data class ClassWithDefaults(val i: Int, val s: String = "someString")

    interface TestInterface {}
}