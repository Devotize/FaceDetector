package com.sychev.feature.define.gender.impl.di

import android.content.Context
import com.sychev.feature.deafine.gender.api.GenderDefiner
import com.sychev.feature.define.gender.impl.DefineGenderUseCase
import com.sychev.feature.define.gender.impl.GenderDefinerImpl
import dagger.Module
import dagger.Provides

@Module
object DefineGenderModule {

    @Provides
    fun provideGenderDefiner(context: Context): GenderDefiner = GenderDefinerImpl.getInstance(context)

    @Provides
    fun provideDefineGenderUseCase(definer: GenderDefiner) = DefineGenderUseCase(definer)

}