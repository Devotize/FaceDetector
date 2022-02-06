package com.sychev.feature.define.clothes.impl.di

import com.sychev.common.di.CommonProvider
import com.sychev.feature.define.clothes.api.DefineClothesProvider
import com.sychev.feature.define.clothes.impl.DefineClothesUseCase
import dagger.Component

@Component(
    dependencies = [CommonProvider::class],
    modules = [DefineClothesModule::class]
)
interface DefineClothesComponent : DefineClothesProvider {
    val defineClothesUseCase: DefineClothesUseCase
}