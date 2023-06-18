package jp.co.yumemi.android.code_check.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.yumemi.android.code_check.constants.Constants.BASE_URL
import jp.co.yumemi.android.code_check.network.GithubApiService
import jp.co.yumemi.android.code_check.repository.GithubItemRepository
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Dagger module that provides network-related dependencies for the application.
 *
 * This module is responsible for providing dependencies such as the base URL, converter factory, OkHttpClient,
 * Retrofit instance, GithubApiService, and GithubItemRepository.
 *
 */
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideBaseUrl(): String {
        return BASE_URL
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        return okHttpClient.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
    ): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(converterFactory)
            .client(okHttpClient)

        return retrofit.build()
    }

    @Singleton
    @Provides
    fun provideItemApiService(retrofit: Retrofit): GithubApiService {
        return retrofit.create(GithubApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideItemRepository(itemApiService: GithubApiService): GithubItemRepository {
        return GithubItemRepository(itemApiService)
    }
}