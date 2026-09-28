package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.di

import android.content.Context
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.db.CollectionDao
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.db.ImageDao
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.repository.ImageRepository
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.network.ApiService
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.db.AppDatabase
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.db.ColoringTemplateDao
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.db.MagicTemplateDao
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(36, TimeUnit.SECONDS)
        .readTimeout(36, TimeUnit.SECONDS)
        .writeTimeout(36, TimeUnit.SECONDS)
        .callTimeout(36, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()


    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(Constant.GITHUB_BASE_URL)
            .client(okHttpClient) // Use the provided okHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "kids_coloring_app_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideColoringTemplateDao(database: AppDatabase): ColoringTemplateDao {
        return database.coloringTemplateDao()
    }

    @Provides
    @Singleton
    fun provideMagicTemplateDao(database: AppDatabase): MagicTemplateDao {
        return database.magicTemplateDao()
    }


    // color by number
    @Provides
    fun provideImageDao(database: AppDatabase): ImageDao = database.imageDao()

    @Provides
    fun provideCollectionDao(database: AppDatabase): CollectionDao = database.collectionDao()


    @Provides
    @Singleton
    fun provideImageRepository(
        apiService: ApiService,
        imageDao: ImageDao,
        collectionDao: CollectionDao,
    ): ImageRepository = ImageRepository(apiService, imageDao, collectionDao)



}