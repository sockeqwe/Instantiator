package com.hannesdorfmann.instantiator

import kotlin.experimental.and
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.full.createType


class InstantiatorConfig(
    vararg factories: Pair<KType, InstanceFactory<Any>> = arrayOf(
        Int::class.createType() to ::defaultIntGenerator,
        Float::class.createType() to ::defaultFloatGenerator,
        Double::class.createType() to ::defaultDoubleGenerator,
        String::class.createType() to ::defaultStringGenerator,
        Char::class.createType() to ::defaultCharGenerator,
        Boolean::class.createType() to ::defaultBooleanGenerator,
        Long::class.createType() to ::defaultLongGenerator,
        Short::class.createType() to ::defaultShortGenerator,
        Byte::class.createType() to ::defaultByteGenerator
    )
) {

    internal val instanceFactory: MutableMap<KType, InstanceFactory<Any?>> = mutableMapOf()

    init {
        /*
        registerFactory(::defaultIntGenerator)
        registerFactory(::defaultFloatGenerator)
        registerFactory(::defaultDoubleGenerator)
        registerFactory(::defaultStringGenerator)
        registerFactory(::defaultCharGenerator)
        registerFactory(::defaultBooleanGenerator)
        registerFactory(::defaultLongGenerator)
        registerFactory(::defaultShortGenerator)
        registerFactory(::defaultByteGenerator)

         */

        instanceFactory.putAll(factories)
    }
}


private fun defaultIntGenerator(): Int = Random.nextInt()
private fun defaultBooleanGenerator(): Boolean = Random.nextBoolean()
private fun defaultDoubleGenerator(): Double = Random.nextDouble()
private fun defaultFloatGenerator(): Float = Random.nextFloat()
private fun defaultLongGenerator(): Long = Random.nextLong()
private fun defaultShortGenerator(): Short = Short.MAX_VALUE
private fun defaultByteGenerator(): Byte = Random.nextBytes(1)[0]
private fun defaultCharGenerator(): Char = charPool[Random.nextInt(charPool.size)]


private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
private fun defaultStringGenerator(): String {
    val bytes = Random.nextBytes(10)
    return (bytes.indices)
        .map { i ->
            charPool[(bytes[i] and 0xFF.toByte() and (charPool.size - 1).toByte()).toInt()]
        }.joinToString("")
}