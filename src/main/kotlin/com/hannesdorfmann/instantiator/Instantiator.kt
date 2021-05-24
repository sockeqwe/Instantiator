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
private val wildcardMutableListType = MutableList::class.createType(arguments = listOf(KTypeProjection.STAR))


internal class Instantiator(config: InstantiatorConfig) {

    private val instanceFactory: MutableMap<KType, InstanceFactory<Any?>> = config.instanceFactory

    private fun fillList(genericsType : KType) : List<Any> {
        return (1..10).map { createInstance(genericsType) as Any }
    }

    private fun <T : Any> createInstance(type: KType): T {

        // Types with Generics
        if (type.arguments.size == 1) {
            // Dealing with special cases such as collections
            val valueType = type.arguments[0].type!!
            when {
                type.isSubtypeOf(wildcardMutableListType) -> return fillList(valueType).toMutableList() as T
                type.isSubtypeOf(wildcardListType) -> return fillList(valueType) as T
            }
        }


        return fromInstanceFactoryIfAvailbaleOtherwise(type) {
            val classifier = type.classifier
            if (type.classifier != null && classifier is KClass<*>) {
                createInstance(type.classifier as KClass<T>)
            } else {
                throw RuntimeException("Could not construct instance of $type")
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

    internal fun <T : Any> createInstance(clazz: KClass<T>): T {

        // Singleton objects
        val singletonObject = clazz.objectInstance
        if (singletonObject != null) {
            return singletonObject
        }

        val type = clazz.createType()
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

inline fun <reified T : Any> instance(config: InstantiatorConfig = InstantiatorConfig()): T =
    T::class.instance(config)

fun <T : Any> KClass<T>.instance(config: InstantiatorConfig = InstantiatorConfig()): T =
    Instantiator(config).createInstance(this)