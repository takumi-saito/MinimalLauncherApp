package com.kireaji.minimallauncherapp.di

import android.content.Context
import com.kireaji.minimallauncherapp.AppUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppUseCase(@ApplicationContext context: Context): AppUseCase {
        return AppUseCase(context)
    }
}