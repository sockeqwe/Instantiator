package com.hannesdorfmann.instantiator

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import kotlin.experimental.and
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.withNullability

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

private object IntInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<Int> {
    override val type: KType = Int::class.createType()
    override fun createInstance(random: Random): Int = random.nextInt()
}

private object BooleanInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<Boolean> {
    override val type: KType = Boolean::class.createType()
    override fun createInstance(random: Random): Boolean = random.nextBoolean()
}

private object FloatInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<Float> {
    override val type: KType = Float::class.createType()
    override fun createInstance(random: Random): Float = random.nextFloat()
}

private object DoubleInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<Double> {
    override val type: KType = Double::class.createType()
    override fun createInstance(random: Random): Double = random.nextDouble()
}

private object LongInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<Long> {
    override val type: KType = Long::class.createType()
    override fun createInstance(random: Random): Long = random.nextLong()
}

private object ShortInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<Short> {
    override val type: KType = Short::class.createType()
    override fun createInstance(random: Random): Short = random.nextInt(Short.MAX_VALUE.toInt()).toShort()
}

private object ByteInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<Byte> {
    override val type: KType = Byte::class.createType()
    override fun createInstance(random: Random): Byte = random.nextBytes(1)[0]
}

private object StringInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<String> {
    override val type: KType = String::class.createType()
    override fun createInstance(random: Random): String {
        val bytes = random.nextBytes(10)
        return (bytes.indices)
            .map { i ->
                charPool[(bytes[i] and 0xFF.toByte() and (charPool.size - 1).toByte()).toInt()]
            }.joinToString("")
    }
}

private object CharInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<Char> {
    override val type: KType = Char::class.createType()
    override fun createInstance(random: Random): Char = charPool[random.nextInt(charPool.size)]
}

private object DateInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<Date> {
    override val type: KType = Date::class.createType()

    override fun createInstance(random: Random): Date = Date(random.nextLong())
}

private object InstantInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<Instant> {

    override val type: KType = Instant::class.createType()

    override fun createInstance(random: Random): Instant =
        Instant.ofEpochMilli(random.nextLong(4102441200000)) // 2100-01-01
}


private object LocalDateTimeInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<LocalDateTime> {

    override val type: KType = LocalDateTime::class.createType()

    override fun createInstance(random: Random): LocalDateTime =
        LocalDateTime.ofInstant(
            InstantInstanceFactory.createInstance(random),
            ZoneId.of(ZoneId.getAvailableZoneIds().random(random))
        )
}

private object LocalDateInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<LocalDate> {

    override val type: KType = LocalDate::class.createType()

    override fun createInstance(random: Random): LocalDate =
        LocalDateTimeInstanceFactory.createInstance(random).toLocalDate()
}

private val IntNullInstanceFactory = IntInstanceFactory.toNullableInstanceFactory()
private val BooleanNullInstanceFactory = BooleanInstanceFactory.toNullableInstanceFactory()
private val FloatNullInstanceFactory = FloatInstanceFactory.toNullableInstanceFactory()
private val DoubleNullInstanceFactory = DoubleInstanceFactory.toNullableInstanceFactory()
private val LongNullInstanceFactory = LongInstanceFactory.toNullableInstanceFactory()
private val ShortNullInstanceFactory = ShortInstanceFactory.toNullableInstanceFactory()
private val ByteNullInstanceFactory = ByteInstanceFactory.toNullableInstanceFactory()
private val StringNullInstanceFactory = StringInstanceFactory.toNullableInstanceFactory()
private val CharNullInstanceFactory = CharInstanceFactory.toNullableInstanceFactory()
private val DateNullInstanceFactory = DateInstanceFactory.toNullableInstanceFactory()
private val InstantNullableInstanceFactory = InstantInstanceFactory.toNullableInstanceFactory()
private val LocalDateTimeNullableInstanceFactory = LocalDateTimeInstanceFactory.toNullableInstanceFactory()
private val LocalDateNullableInstanceFactory = LocalDateInstanceFactory.toNullableInstanceFactory()

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

    operator fun plus(factory: InstanceFactory): InstantiatorConfig = add(factory)


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
            LocalDateTimeNullableInstanceFactory,
            LocalDateInstanceFactory,
            LocalDateNullableInstanceFactory
        )
    }

    sealed interface InstanceFactory {
        val type: KType
    }

    interface NonNullableInstanceFactory<T : Any> : InstanceFactory {
        fun createInstance(random: Random): T
    }


    interface NullableInstanceFactory<T> : InstanceFactory {
        fun createInstance(random: Random): T?
    }
}

/**
 * Used by [toNullableInstanceFactory]
 */
enum class ToNullableInstaceFactoryMode {
    /**
     * It randomly decides if value should be null or not
     */
    RANDOM,

    /**
     * It specifies that the returned instance of this
     * [com.hannesdorfmann.instantiator.InstantiatorConfig.NullableInstanceFactory] is always `null`
     */
    ALWAYS_NULL,

    /**
     * It specifies that the returned instance of this
     * [com.hannesdorfmann.instantiator.InstantiatorConfig.NullableInstanceFactory] is never `null`, so always an
     * non-null instance is returned for sure.
     */
    NEVER_NULL
}


/**
 * This is a little utility function that helps to convert a regular
 * [com.hannesdorfmann.instantiator.InstantiatorConfig.NonNullableInstanceFactory] to a
 * [com.hannesdorfmann.instantiator.InstantiatorConfig.NullableInstanceFactory].
 *
 * It does so by delegating the true value creating to the original
 * [com.hannesdorfmann.instantiator.InstantiatorConfig.NonNullableInstanceFactory]
 */
fun <T : Any> InstantiatorConfig.NonNullableInstanceFactory<T>.toNullableInstanceFactory(mode: ToNullableInstaceFactoryMode = ToNullableInstaceFactoryMode.RANDOM): InstantiatorConfig.NullableInstanceFactory<T> {
    val self = this

    return object : InstantiatorConfig.NullableInstanceFactory<T> {
        override val type: KType = self.type.withNullability(true)
        override fun createInstance(random: Random): T? =
            when (mode) {
                ToNullableInstaceFactoryMode.RANDOM -> if (random.nextBoolean()) self.createInstance(random) else null
                ToNullableInstaceFactoryMode.ALWAYS_NULL -> null
                ToNullableInstaceFactoryMode.NEVER_NULL -> self.createInstance(random)
            }
    }
}
