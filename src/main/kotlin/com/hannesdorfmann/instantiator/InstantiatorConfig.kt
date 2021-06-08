package com.hannesdorfmann.instantiator

import kotlin.experimental.and
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.full.createType

private object IntInstanceFactory : InstanceFactory<Int> {
    override val type: KType = Int::class.createType()
    override fun createInstance(): Int = Random.nextInt()
}

private object BooleanInstanceFactory : InstanceFactory<Boolean> {
    override val type: KType = Boolean::class.createType()
    override fun createInstance(): Boolean = Random.nextBoolean()
}

private object FloatInstanceFactory : InstanceFactory<Float> {
    override val type: KType = Float::class.createType()
    override fun createInstance(): Float = Random.nextFloat()
}

private object DoubleInstanceFactory : InstanceFactory<Double> {
    override val type: KType = Double::class.createType()
    override fun createInstance(): Double = Random.nextDouble()
}

private object LongInstanceFactory : InstanceFactory<Long> {
    override val type: KType = Long::class.createType()
    override fun createInstance(): Long = Random.nextLong()
}

private object ShortInstanceFactory : InstanceFactory<Short> {
    override val type: KType = Short::class.createType()
    override fun createInstance(): Short = Random.nextInt(Short.MAX_VALUE.toInt()).toShort()
}

private object ByteInstanceFactory : InstanceFactory<Byte> {
    override val type: KType = Byte::class.createType()
    override fun createInstance(): Byte = Random.nextBytes(1)[0]
}

private object StringInstanceFactory : InstanceFactory<String> {
    override val type: KType = String::class.createType()
    override fun createInstance(): String {
        val bytes = Random.nextBytes(10)
        return (bytes.indices)
            .map { i ->
                charPool[(bytes[i] and 0xFF.toByte() and (charPool.size - 1).toByte()).toInt()]
            }.joinToString("")
    }
}

private object CharInstanceFactory : InstanceFactory<Char> {
    override val type: KType = Char::class.createType()
    override fun createInstance(): Char = charPool[Random.nextInt(charPool.size)]
}

private object IntNullInstanceFactory : InstanceFactory<Int> {
    override val type: KType = Int::class.createType(nullable = true)
    override fun createInstance(): Int = Random.nextInt()
}

private object BooleanNullInstanceFactory : InstanceFactory<Boolean> {
    override val type: KType = Boolean::class.createType(nullable = true)
    override fun createInstance(): Boolean = Random.nextBoolean()
}

private object FloatNullInstanceFactory : InstanceFactory<Float> {
    override val type: KType = Float::class.createType(nullable = true)
    override fun createInstance(): Float = Random.nextFloat()
}

private object DoubleNullInstanceFactory : InstanceFactory<Double> {
    override val type: KType = Double::class.createType(nullable = true)
    override fun createInstance(): Double = Random.nextDouble()
}

private object LongNullInstanceFactory : InstanceFactory<Long> {
    override val type: KType = Long::class.createType(nullable = true)
    override fun createInstance(): Long = Random.nextLong()
}

private object ShortNullInstanceFactory : InstanceFactory<Short> {
    override val type: KType = Short::class.createType(nullable = true)
    override fun createInstance(): Short = Random.nextInt(Short.MAX_VALUE.toInt()).toShort()
}

private object ByteNullInstanceFactory : InstanceFactory<Byte> {
    override val type: KType = Byte::class.createType(nullable = true)
    override fun createInstance(): Byte = Random.nextBytes(1)[0]
}

private object StringNullInstanceFactory : InstanceFactory<String> {
    override val type: KType = String::class.createType(nullable = true)
    override fun createInstance(): String {
        val bytes = Random.nextBytes(10)
        return (bytes.indices)
            .map { i ->
                charPool[(bytes[i] and 0xFF.toByte() and (charPool.size - 1).toByte()).toInt()]
            }.joinToString("")
    }
}

private object CharNullInstanceFactory : InstanceFactory<Char> {
    override val type: KType = Char::class.createType(nullable = true)
    override fun createInstance(): Char = charPool[Random.nextInt(charPool.size)]
}

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')


class InstantiatorConfig(
    val useDefaultArguments: Boolean = true,
    val useNull: Boolean = true,
    vararg factories: InstanceFactory<out Any> = arrayOf(
        IntInstanceFactory,
        IntNullInstanceFactory,
        BooleanInstanceFactory,
        BooleanNullInstanceFactory,
        FloatInstanceFactory,
        FloatNullInstanceFactory,
        DoubleInstanceFactory,
        DoubleNullInstanceFactory,
        StringInstanceFactory,
        StringNullInstanceFactory,
        CharInstanceFactory,
        CharNullInstanceFactory,
        LongInstanceFactory,
        LongNullInstanceFactory,
        ShortInstanceFactory,
        ShortNullInstanceFactory,
        ByteInstanceFactory,
        ByteNullInstanceFactory
    )
) {
    internal val instanceFactory: Map<KType, InstanceFactory<out Any>> = factories.associateBy { it.type }
}

