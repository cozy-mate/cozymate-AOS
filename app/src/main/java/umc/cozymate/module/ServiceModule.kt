package umc.cozymate.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import umc.cozymate.data.api.BlockService
import umc.cozymate.data.api.ChatService
import umc.cozymate.data.api.FavoritesService
import umc.cozymate.data.api.MemberService
import umc.cozymate.data.api.MemberStatPreferenceService
import umc.cozymate.data.api.MemberStatService
import umc.cozymate.data.api.ReportService
import umc.cozymate.data.api.RoleService
import umc.cozymate.data.api.RoomLogService
import umc.cozymate.data.api.RoomService
import umc.cozymate.data.api.RoommateService
import umc.cozymate.data.api.RuleService
import umc.cozymate.data.api.TodoService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    private inline fun <reified T> Retrofit.buildService(): T {
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

    @Provides
    @Singleton
    fun provideRoomApi(@NetworkModule.BaseRetrofit retrofit: Retrofit): RoomService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideRoomLogApi(@NetworkModule.BaseRetrofit retrofit: Retrofit): RoomLogService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideMemberApi(@NetworkModule.BaseRetrofit retrofit: Retrofit): MemberService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideMemberStatApi(@NetworkModule.BaseRetrofit retrofit: Retrofit) : MemberStatService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideMemberStatPreferenceApi(@NetworkModule.BaseRetrofit retrofit: Retrofit): MemberStatPreferenceService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideRuleApi(@NetworkModule.BaseRetrofit retrofit: Retrofit): RuleService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideRoleApi(@NetworkModule.BaseRetrofit retrofit: Retrofit): RoleService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideTodoApi(@NetworkModule.BaseRetrofit retrofit: Retrofit) : TodoService{
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideReportApi(@NetworkModule.BaseRetrofit retrofit: Retrofit) : ReportService{
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideFavoritesApi(@NetworkModule.BaseRetrofit retrofit: Retrofit) : FavoritesService{
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideBlockApi(@NetworkModule.BaseRetrofit retrofit: Retrofit) : BlockService{
        return retrofit.buildService()
    }

}