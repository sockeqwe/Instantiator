package com.hannesdorfmann.instantiator

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import kotlin.experimental.and
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.full.createType

private object IntInstanceFactory : InstantiatorConfig.IF<Int> {
    override val type: KType = Int::class.createType()
    override fun createInstance(random: Random): Int = random.nextInt()
}

private object BooleanInstanceFactory : InstantiatorConfig.IF<Boolean> {
    override val type: KType = Boolean::class.createType()
    override fun createInstance(random: Random): Boolean = random.nextBoolean()
}

private object FloatInstanceFactory : InstantiatorConfig.IF<Float> {
    override val type: KType = Float::class.createType()
    override fun createInstance(random: Random): Float = random.nextFloat()
}

private object DoubleInstanceFactory : InstantiatorConfig.IF<Double> {
    override val type: KType = Double::class.createType()
    override fun createInstance(random: Random): Double = random.nextDouble()
}

private object LongInstanceFactory : InstantiatorConfig.IF<Long> {
    override val type: KType = Long::class.createType()
    override fun createInstance(random: Random): Long = random.nextLong()
}

private object ShortInstanceFactory : InstantiatorConfig.IF<Short> {
    override val type: KType = Short::class.createType()
    override fun createInstance(random: Random): Short = random.nextInt(Short.MAX_VALUE.toInt()).toShort()
}

private object ByteInstanceFactory : InstantiatorConfig.IF<Byte> {
    override val type: KType = Byte::class.createType()
    override fun createInstance(random: Random): Byte = random.nextBytes(1)[0]
}

private object StringInstanceFactory : InstantiatorConfig.IF<String> {
    override val type: KType = String::class.createType()
    override fun createInstance(random: Random): String {
        val bytes = random.nextBytes(10)
        return (bytes.indices)
            .map { i ->
                charPool[(bytes[i] and 0xFF.toByte() and (charPool.size - 1).toByte()).toInt()]
            }.joinToString("")
    }
}

private object CharInstanceFactory : InstantiatorConfig.IF<Char> {
    override val type: KType = Char::class.createType()
    override fun createInstance(random: Random): Char = charPool[random.nextInt(charPool.size)]
}

private object IntNullInstanceFactory : InstantiatorConfig.IF<Int> {
    override val type: KType = Int::class.createType(nullable = true)
    override fun createInstance(random: Random): Int = random.nextInt()
}

private object BooleanNullInstanceFactory : InstantiatorConfig.IF<Boolean> {
    override val type: KType = Boolean::class.createType(nullable = true)
    override fun createInstance(random: Random): Boolean = random.nextBoolean()
}

private object FloatNullInstanceFactory : InstantiatorConfig.IF<Float> {
    override val type: KType = Float::class.createType(nullable = true)
    override fun createInstance(random: Random): Float = random.nextFloat()
}

private object DoubleNullInstanceFactory : InstantiatorConfig.IF<Double> {
    override val type: KType = Double::class.createType(nullable = true)
    override fun createInstance(random: Random): Double = random.nextDouble()
}

private object LongNullInstanceFactory : InstantiatorConfig.IF<Long> {
    override val type: KType = Long::class.createType(nullable = true)
    override fun createInstance(random: Random): Long = random.nextLong()
}

private object ShortNullInstanceFactory : InstantiatorConfig.IF<Short> {
    override val type: KType = Short::class.createType(nullable = true)
    override fun createInstance(random: Random): Short = random.nextInt(Short.MAX_VALUE.toInt()).toShort()
}

private object ByteNullInstanceFactory : InstantiatorConfig.IF<Byte> {
    override val type: KType = Byte::class.createType(nullable = true)
    override fun createInstance(random: Random): Byte = random.nextBytes(1)[0]
}

private object StringNullInstanceFactory : InstantiatorConfig.IF<String> {
    override val type: KType = String::class.createType(nullable = true)
    override fun createInstance(random: Random): String {
        val bytes = random.nextBytes(10)
        return (bytes.indices)
            .map { i ->
                charPool[(bytes[i] and 0xFF.toByte() and (charPool.size - 1).toByte()).toInt()]
            }.joinToString("")
    }
}

private object CharNullInstanceFactory : InstantiatorConfig.IF<Char> {
    override val type: KType = Char::class.createType(nullable = true)
    override fun createInstance(random: Random): Char = charPool[random.nextInt(charPool.size)]
}

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

private object DateInstanceFactory : InstantiatorConfig.IF<Date> {
    override val type: KType = Date::class.createType()

    override fun createInstance(random: Random): Date = Date(random.nextLong())
}

private object DateNullInstanceFactory : InstantiatorConfig.IF<Date> {
    override val type: KType = Date::class.createType(nullable = true)

    override fun createInstance(random: Random): Date = Date(random.nextLong())
}

private object InstantInstanceFactory : InstantiatorConfig.IF<Instant> {

    override val type: KType = Instant::class.createType()

    override fun createInstance(random: Random): Instant = Instant.ofEpochMilli(random.nextLong())
}

private object InstantNullableInstanceFactory : InstantiatorConfig.IF<Instant> {

    override val type: KType = Instant::class.createType(nullable = true)

    override fun createInstance(random: Random): Instant = Instant.ofEpochMilli(random.nextLong())
}

private object LocalDateTimeInstanceFactory : InstantiatorConfig.IF<LocalDateTime> {

    override val type: KType = LocalDateTime::class.createType()

    override fun createInstance(random: Random): LocalDateTime =
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(random.nextLong()),
            ZoneId.of(ZoneId.getAvailableZoneIds().random(random))
        )
}

private object LocalDateTimeNullableInstanceFactory : InstantiatorConfig.IF<LocalDateTime> {

    override val type: KType = LocalDateTime::class.createType(nullable = true)

    override fun createInstance(random: Random): LocalDateTime =
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(random.nextLong()),
            ZoneId.of(ZoneId.getAvailableZoneIds().random(random))
        )
}

class InstantiatorConfig(
    val useDefaultArguments: Boolean = true,
    val useNull: Boolean = true,
    val random: Random = Random,
    vararg factories: InstanceFactory = DEFAULT_INSTANCE_FACTORIES
) {

    internal val instanceFactory: Map<KType, InstanceFactory> = factories.associateBy { it.type }

    /**
     * Adds a copy of this [InstantiatorConfig] and then adds the [InstanceFactory] to the new config.
     */
    fun add(vararg factories: InstanceFactory): InstantiatorConfig = InstantiatorConfig(
        useDefaultArguments = this.useDefaultArguments,
        useNull = this.useNull,
        factories = (this.instanceFactory.values + factories).toTypedArray()
    )

    operator fun <T : Any> plus(factory: InstanceFactory): InstantiatorConfig = add(factory)


    companion object {
        val DEFAULT_INSTANCE_FACTORIES: Array<InstanceFactory> = arrayOf(
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
            ByteNullInstanceFactory,
            DateInstanceFactory,
            DateNullInstanceFactory,
            InstantInstanceFactory,
            InstantNullableInstanceFactory,
            LocalDateTimeInstanceFactory,
            LocalDateTimeNullableInstanceFactory
        )
    }

    sealed interface InstanceFactory {
        val type: KType
    }

    interface IF<T : Any> : InstanceFactory {
        fun createInstance(random: Random): T
    }


    interface NullableInstanceFactory<T> : InstanceFactory {
        fun createInstance(random: Random): T?
    }
}

