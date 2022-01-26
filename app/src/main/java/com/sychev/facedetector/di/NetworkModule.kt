package com.sychev.facedetector.di

import com.google.gson.GsonBuilder
import com.sychev.facedetector.data.remote.AdminApi
import com.sychev.facedetector.data.remote.CelebDetectionApi
import com.sychev.facedetector.data.remote.ClothesDetectionApi
import com.sychev.facedetector.data.remote.UnsplashApi
import com.sychev.facedetector.data.remote.converter.BrandDtoConverter
import com.sychev.facedetector.data.remote.converter.CelebDtoConverter
import com.sychev.facedetector.data.remote.converter.ClothesDtoConverter
import com.sychev.facedetector.data.remote.model.UnsplashDto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient{

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts:  Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?){}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate>  = arrayOf()
            })

            // Install the all-trusting trust manager
            val  sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            if (trustAllCerts.isNotEmpty() &&  trustAllCerts.first() is X509TrustManager) {
                okHttpClient.sslSocketFactory(sslSocketFactory, trustAllCerts.first() as X509TrustManager)
                okHttpClient.hostnameVerifier { _, _ -> true }
            }

            return okHttpClient.build()
        } catch (e: Exception) {
            return okHttpClient.build()
        }

//        return OkHttpClient().newBuilder()
//            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
//            .build()
    }

    @Singleton
    @Provides
    fun provideCelebDetectionService(okHttpClient: OkHttpClient):CelebDetectionApi {
        return Retrofit.Builder()
            .baseUrl("https://rgsb-face-recognition-ml02.rgsbank.ru:8083/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(CelebDetectionApi::class.java)
    }

    @Singleton
    @Provides
    fun provideClothesDetectionService(okHttpClient: OkHttpClient): ClothesDetectionApi {
        return Retrofit.Builder()
            .baseUrl("http://51.250.16.91:5001/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ClothesDetectionApi::class.java)
    }

    @Singleton
    @Provides
    fun provideClothesAdminApi(okHttpClient: OkHttpClient): AdminApi {
        return Retrofit.Builder()
            .baseUrl("http://51.250.20.12:5002")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(AdminApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDetectedClothesConverter(): ClothesDtoConverter = ClothesDtoConverter()

    @Singleton
    @Provides
    fun provideBrandDtoConverter(): BrandDtoConverter = BrandDtoConverter()

    @Singleton
    @Provides
    fun provideCelebDtoConverter(): CelebDtoConverter = CelebDtoConverter()

    @Singleton
    @Provides
    fun provideUnsplashService(okHttpClient: OkHttpClient): UnsplashApi {
        return Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(UnsplashApi::class.java)
    }

}



















