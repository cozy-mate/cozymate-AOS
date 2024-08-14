package umc.cozymate.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import umc.cozymate.data.api.ChatService
import umc.cozymate.data.api.RoommateService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    private inline fun <reified T> Retrofit.buildService(): T{
        return this.create(T::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApi(@NetworkModule.BaseRetrofit retrofit: Retrofit): ChatService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideRoommateApi(@NetworkModule.BaseRetrofit retrofit: Retrofit): RoommateService{
        return retrofit.buildService()
    }
}