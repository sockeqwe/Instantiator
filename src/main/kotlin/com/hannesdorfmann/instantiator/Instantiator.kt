package com.hannesdorfmann.instantiator

import kotlin.random.Random
import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmName

internal typealias InstanceFactory<T> = () -> T

private val wildcardListType = List::class.createType(arguments = listOf(KTypeProjection.STAR))
private val wildcardCollectionType = Collection::class.createType(arguments = listOf(KTypeProjection.STAR))
private val wildcardSetType = Set::class.createType(arguments = listOf(KTypeProjection.STAR))
private val wildcardMapType = Map::class.createType(arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR))


class Instantiator(config: InstantiatorConfig) {

    private val instanceFactory: MutableMap<KType, InstanceFactory<Any?>> = config.instanceFactory

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

    fun <T : Any> createInstance(type: KType): T {

        // Types with Generics
        if (type.arguments.size == 1) {
            // Dealing with special cases such as collections
            val genericsType = type.arguments[0].type!!
            when {
                type.isSubtypeOf(wildcardListType) -> return fillList(genericsType) as T
                type.isSubtypeOf(wildcardSetType) -> return fillSet(genericsType) as T
                type.isSubtypeOf(wildcardCollectionType) -> return fillList(genericsType) as T
            }
        } else if (type.arguments.size == 2) {
            val genericsType1 = type.arguments[0].type!!
            val genericsType2 = type.arguments[1].type!!

            when {
                type.isSubtypeOf(wildcardMapType) -> return fillMap(genericsType1, genericsType2) as T
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


    private fun <T> fromInstanceFactoryIfAvailbaleOtherwise(type: KType, alternative: () -> T): T {
        val factory: InstanceFactory<T>? = instanceFactory[type] as InstanceFactory<T>?
        return if (factory != null) {
            factory()
        } else {
            alternative()
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
                        "in the ${InstantiatorConfig::class.simpleName}."
            )
        }
        if (clazz.isSubclassOf(Enum::class)) {
            return fromInstanceFactoryIfAvailbaleOtherwise(type) {
                val enumConstants = Class.forName(clazz.jvmName).enumConstants
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

                val primaryConstructorParameters: Map<KParameter, Any?> =
                    primaryConstructor.parameters.associate { parameter ->
                        parameter to createInstance(parameter.type)
                    }

                if (primaryConstructorParameters.isEmpty()) {
                    primaryConstructor.call()
                } else {
                    primaryConstructor.callBy(primaryConstructorParameters)
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

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any> instance(config: InstantiatorConfig = InstantiatorConfig()): T =
    Instantiator(config).createInstance(typeOf<T>())
