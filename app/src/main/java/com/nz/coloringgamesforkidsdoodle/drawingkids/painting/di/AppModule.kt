package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.di


import android.content.Context
import android.content.SharedPreferences
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.Constant
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.TinyDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideTinyDB(sharedPreferences: SharedPreferences): TinyDB {
        return TinyDB(sharedPreferences)
    }
}