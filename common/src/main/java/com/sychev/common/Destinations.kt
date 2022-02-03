package com.sychev.common

typealias Destinations = Map<Class<out EntryPoint>, @JvmSuppressWildcards EntryPoint>

inline fun <reified T : EntryPoint> Destinations.find(): T =
    findOrNull() ?: error("Unable to find '${T::class.java}' destination.")

inline fun <reified T : EntryPoint> Destinations.findOrNull(): T? = this[T::class.java] as? T