package com.sychev.common.di

import com.sychev.common.EntryPoint
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
annotation class EntryPointKey(val value: KClass<out EntryPoint>)
