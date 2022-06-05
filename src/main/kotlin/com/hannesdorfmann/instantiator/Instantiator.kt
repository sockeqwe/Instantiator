package com.hannesdorfmann.instantiator

import kotlin.random.Random
import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmName

private val wildcardListType = List::class.createType(arguments = listOf(KTypeProjection.STAR))
private val wildcardListNullType = List::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true)
private val wildcardCollectionType = Collection::class.createType(arguments = listOf(KTypeProjection.STAR))
private val wildcardCollectionNullType =
    Collection::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true)
private val wildcardSetType = Set::class.createType(arguments = listOf(KTypeProjection.STAR))
private val wildcardSetNullType = Set::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true)
private val wildcardMapType = Map::class.createType(arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR))
private val wildcardMapNullType =
    Map::class.createType(arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR), nullable = true)
private val wildcardPairType = Pair::class.createType(arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR))
private val wildcardPairNullType =
    Pair::class.createType(arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR), nullable = true)
private val wildcardTripleType =
    Triple::class.createType(arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR, KTypeProjection.STAR))
private val wildcardTripleNullType =
    Triple::class.createType(
        arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR, KTypeProjection.STAR),
        nullable = true
    )

class Instantiator(private val config: InstantiatorConfig) {

    private fun fillList(genericsType: KType): List<Any> {
        return (1..10).map { createInstance(genericsType) as Any }.toMutableList()
    }

    private fun fillSet(genericsType: KType): Set<Any> {
        return (1..10).map { createInstance(genericsType) as Any }.toMutableSet()
    }

    private fun fillMap(keyGenericsType: KType, valueGenericsType: KType): Map<Any, Any> {
        return (1..10).associate { createInstance(keyGenericsType) as Any to createInstance(valueGenericsType) as Any }
            .toMutableMap()
    }

    private fun fillPair(keyGenericsType: KType, valueGenericsType: KType): Pair<Any, Any> {
        return createInstance(keyGenericsType) as Any to createInstance(valueGenericsType) as Any
    }

    private fun fillTriple(
        firstGenericsType: KType,
        secondGenericsType: KType,
        thirdGenericsType: KType
    ): Triple<Any, Any, Any> {
        return Triple(
            createInstance(firstGenericsType) as Any,
            createInstance(secondGenericsType) as Any,
            createInstance(thirdGenericsType) as Any
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> createInstance(type: KType): T? {

        // Types with Generics

        when (type.arguments.size) {
            1 -> {
                // Dealing with special cases such as collections
                val genericsType = type.arguments[0].type!!
                when {
                    type.isSubtypeOf(wildcardListType) -> return fillList(genericsType) as T
                    type.isSubtypeOf(wildcardListNullType) -> return fillList(genericsType) as T
                    type.isSubtypeOf(wildcardSetType) -> return fillSet(genericsType) as T
                    type.isSubtypeOf(wildcardSetNullType) -> return fillSet(genericsType) as T
                    type.isSubtypeOf(wildcardCollectionType) -> return fillList(genericsType) as T
                    type.isSubtypeOf(wildcardCollectionNullType) -> return fillList(genericsType) as T
                }
            }
            2 -> {
                val genericsType1 = type.arguments[0].type!!
                val genericsType2 = type.arguments[1].type!!

                when {
                    type.isSubtypeOf(wildcardMapType) -> return fillMap(genericsType1, genericsType2) as T
                    type.isSubtypeOf(wildcardMapNullType) -> return fillMap(genericsType1, genericsType2) as T
                    type.isSubtypeOf(wildcardPairType) -> return fillPair(genericsType1, genericsType2) as T
                    type.isSubtypeOf(wildcardPairNullType) -> return fillPair(genericsType1, genericsType2) as T
                }
            }
            3 -> {
                val genericsType1 = type.arguments[0].type!!
                val genericsType2 = type.arguments[1].type!!
                val genericsType3 = type.arguments[2].type!!

                when {
                    type.isSubtypeOf(wildcardTripleType) -> return fillTriple(
                        genericsType1,
                        genericsType2,
                        genericsType3
                    ) as T
                    type.isSubtypeOf(wildcardTripleNullType) -> return fillTriple(
                        genericsType1,
                        genericsType2,
                        genericsType3
                    ) as T
                }
            }
        }

        return fromInstanceFactoryIfAvailbaleOtherwise(type) {
            val classifier = type.classifier
            if (type.classifier != null && classifier is KClass<*>) {
                createInstance(type.classifier as KClass<T>)
            } else {
                throw RuntimeException(
                    "Could not create instance of $type. " +
                            "This is typically the case if you want to create an instance of a class that has generics. " +
                            "In that case you need to specify a concrete factory how to create such an instance " +
                            "in the ${InstantiatorConfig::class.simpleName}."
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> fromInstanceFactoryIfAvailbaleOtherwise(type: KType, alternative: () -> T): T? {
        return when (val factory = config.instanceFactory[type]) {
            is InstantiatorConfig.IF<*> -> factory.createInstance(config.random) as T
            is InstantiatorConfig.NullableInstanceFactory<*> -> factory.createInstance(config.random) as T?
            null -> alternative()
        }
    }

    private fun <T : Any> createInstance(clazz: KClass<T>): T {

        // Singleton objects
        val singletonObject = clazz.objectInstance
        if (singletonObject != null) {
            return singletonObject
        }


        val type = try {
            clazz.createType()
        } catch (t: IllegalArgumentException) {
            // A class with generics, so it didnt work
            throw RuntimeException(
                "Could not create instance of $clazz. " +
                        "This is typically the case if you want to create an instance of a class that has generics. " +
                        "In that case you need to specify a concrete factory how to create such an instance " +
                        "in the ${InstantiatorConfig::class.simpleName}.", t
            )
        }
        if (clazz.isSubclassOf(Enum::class)) {
            return fromInstanceFactoryIfAvailbaleOtherwise(type) {
                val enumConstants = Class.forName(clazz.jvmName).enumConstants
                @Suppress("UNCHECKED_CAST")
                enumConstants[Random.nextInt(enumConstants.size)] as T
            }
        }

        if (clazz.isSealed) {
            // TODO random? Make it somehow configurable
            val subclasses = clazz.sealedSubclasses
            if (subclasses.isEmpty()) {
                throw UnsupportedOperationException("Sealed classes without any concrete implementation is not supported. Therefore, cannot instantiate $clazz")
            }
            return createInstance(subclasses[Random.nextInt(subclasses.size)])
        }

        if (!clazz.isAbstract) {
            return fromInstanceFactoryIfAvailbaleOtherwise(type) {
                // classes with empty constructor
                val primaryConstructor = clazz.primaryConstructor ?: throw UnsupportedOperationException(
                    "Can not instantiate an instance of ${clazz} without a primary constructor"
                )

                if (primaryConstructor.visibility == KVisibility.PRIVATE) {
                    throw UnsupportedOperationException(
                        "Cannot create an instance of $clazz because primary " +
                                "constructor has private visibility. Constructor must be public."
                    )
                }

                if (primaryConstructor.visibility == KVisibility.PROTECTED) {
                    throw UnsupportedOperationException(
                        "Cannot create an instance of $clazz because primary " +
                                "constructor has protected visibility. Constructor must be public."
                    )
                }

                if (primaryConstructor.parameters.isEmpty()) {
                    primaryConstructor.call()
                } else {

                    val primaryConstructorParameters: Map<KParameter, Any?> =
                        primaryConstructor.parameters
                            .filter { if (config.useDefaultArguments) !it.isOptional else true }
                            .associateWith { parameter ->
                                val value: T? = if (config.useNull && parameter.type.isMarkedNullable)
                                    null
                                else
                                    createInstance(parameter.type)
                                value
                            }

                    if (primaryConstructorParameters.isEmpty()) {
                        primaryConstructor.callBy(emptyMap())
                    } else {
                        primaryConstructor.callBy(primaryConstructorParameters)
                    }
                }
            }
        } else {
            throw UnsupportedOperationException(
                "Instantiating an abstract class or interface is not supported. " +
                        "Therefore, cannot instantiate instance of $clazz"
            )

        }
    }
}

inline fun <reified T : Any> instance(config: InstantiatorConfig = InstantiatorConfig()): T =
    Instantiator(config).createInstance(typeOf<T>())

inline fun <reified T : Any> instantiateSealedSubclasses(
    config: InstantiatorConfig = InstantiatorConfig()
): List<T> {
    if (T::class.isSealed) {
        val subclasses = T::class.sealedSubclasses
        if (subclasses.isEmpty()) {
            throw RuntimeException(
                "${T::class} is a sealed class or sealed interface but has no implementations " +
                        "of it. Therefore I cannot create a list of instances of the subsclasses"
            )
        } else {
            val instantiator = Instantiator(config)

            // This is some ugly workaround to overcome limitations of recursive calls in inline functions.
            // Don't try this at home kids ;)

            var createSealedSubclassesList: ((sealedClassRoot: KClass<out T>) -> List<T>)? = null
            createSealedSubclassesList = { sealedClassRoot ->
                sealedClassRoot.sealedSubclasses
                    .flatMap {
                        if (it.isSealed) {
                            if (it.sealedSubclasses.isEmpty()) {
                                emptyList()
                            } else {
                                createSealedSubclassesList!!(it)
                            }
                        } else {
                            listOf(instantiator.createInstance(it.createType()))
                        }
                    }
            }

            return createSealedSubclassesList(T::class)
        }
    } else {
        throw RuntimeException(
            "${T::class} is not a sealed class or sealed interface. " +
                    "If you want to just get an instance call instance() only."
        )
    }
}
