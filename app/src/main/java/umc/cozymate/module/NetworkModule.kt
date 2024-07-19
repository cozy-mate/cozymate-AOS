package umc.cozymate.module

import androidx.core.location.LocationRequestCompat.Quality
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Request
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Quality
    @Retention(AnnotationRetention.BINARY)
    annotation class BaseRetrofit

    const val NETWORK_EXCEPTION_OFFLINE_CASE = "network status is offline"
    const val NETWORK_EXCEPTION_BODY_IS_NULL = "result.json body is null"

    @Provides
    @Singleton
    @BaseRetrofit
    fun provideOKHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val closeInterceptor = Interceptor { chain ->
            val request: Request =
                chain.request().newBuilder().addHeader("Connection", "close").build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .connectTimeout(40, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addNetworkInterceptor(closeInterceptor)
            .retryOnConnectionFailure(false)
            .build()
    }

    @Provides
    @Singleton
    @BaseRetrofit
    fun provideRetrofit(
        @BaseRetrofit okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(base_url)

            .build()
    }
}
