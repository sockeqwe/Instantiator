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
private val wildcardCollectionNullType = Collection::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true)
private val wildcardSetType = Set::class.createType(arguments = listOf(KTypeProjection.STAR))
private val wildcardSetNullType = Set::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true)
private val wildcardMapType = Map::class.createType(arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR))
private val wildcardMapNullType = Map::class.createType(arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR), nullable = true)

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

    fun <T : Any> createInstance(type: KType): T {

        // Types with Generics
        if (type.arguments.size == 1) {
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
        } else if (type.arguments.size == 2) {
            val genericsType1 = type.arguments[0].type!!
            val genericsType2 = type.arguments[1].type!!

            when {
                type.isSubtypeOf(wildcardMapType) -> return fillMap(genericsType1, genericsType2) as T
                type.isSubtypeOf(wildcardMapNullType) -> return fillMap(genericsType1, genericsType2) as T
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


    private fun <T : Any> fromInstanceFactoryIfAvailbaleOtherwise(type: KType, alternative: () -> T): T {
        val factory: InstantiatorConfig.InstanceFactory<T>? = config.instanceFactory[type] as InstantiatorConfig.InstanceFactory<T>?
        val instance =  factory?.createInstance() ?: alternative()
        return instance
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
                    primaryConstructor.parameters
                        .filter { if (config.useDefaultArguments) !it.isOptional else true}
                        .associateWith { parameter ->
                           val value : T? = if (config.useNull && parameter.type.isMarkedNullable)
                                null
                            else
                                createInstance(parameter.type)
                            value
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
