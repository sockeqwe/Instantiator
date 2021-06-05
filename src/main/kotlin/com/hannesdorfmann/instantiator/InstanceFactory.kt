package com.hannesdorfmann.instantiator

import kotlin.reflect.KType

interface InstanceFactory<T : Any> {
    val type: KType
    fun createInstance(): T
}
