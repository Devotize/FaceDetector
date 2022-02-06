package com.sychev.feature.define.clothes.impl.di

import android.content.Context
import com.sychev.feature.define.clothes.api.ClothesDefiner
import com.sychev.feature.define.clothes.impl.ClothesDefinerImpl
import com.sychev.feature.define.clothes.impl.DefineClothesUseCase
import dagger.Module
import dagger.Provides

@Module
object DefineClothesModule {

    @Provides
    fun provideClothesDefiner(context: Context): ClothesDefiner = ClothesDefinerImpl(context)

    @Provides
    fun provideDefineClothesUseCase(clothesDefiner: ClothesDefiner) =
        DefineClothesUseCase(clothesDefiner)

}