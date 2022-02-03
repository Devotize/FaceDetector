package com.sychev.feature.define.gender.impl.di

import com.sychev.common.di.CommonProvider
import com.sychev.feature.deafine.gender.api.di.DefineGenderProvider
import com.sychev.feature.define.gender.impl.DefineGenderUseCase
import dagger.Component

@Component(
    dependencies = [CommonProvider::class],
    modules = [DefineGenderModule::class]
)
interface DefineGenderComponent: DefineGenderProvider {
    val defineGenderUseCase: DefineGenderUseCase
}