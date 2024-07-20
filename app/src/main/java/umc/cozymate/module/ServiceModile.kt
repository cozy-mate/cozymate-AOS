package umc.cozymate.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ServiceModile {
    private inline fun <reified  T> Retrofit.buildService(): T{
        return this.create(T::class.java)
    }

    
}