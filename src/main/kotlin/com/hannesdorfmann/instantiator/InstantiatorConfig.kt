package com.hannesdorfmann.instantiator

import kotlin.experimental.and
import kotlin.random.Random

data class InstantiatorConfig(
    val intGenerator: () -> Int = ::defaultIntGenerator,
    val stringGenerator: () -> String = ::defaultStringGenerator,
    val charGenerator: () -> Char = ::defaultCharGenerator,
    val booleanGenerator: () -> Boolean = ::defaultBooleanGenerator,
    val doubleGenerator: () -> Double = ::defaultDoubleGenerator,
    val floatGenerator: () -> Float = ::defaultFloatGenerator,
    val longGenerator: () -> Long = ::defaultLongGenerator,
    val shortGenerator: () -> Short = ::defaultShortGenerator,
    val byteGenerator: () -> Byte = ::defaultByteGenerator,
) {

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