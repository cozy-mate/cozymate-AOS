package umc.cozymate.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import umc.cozymate.data.api.ChatService
import umc.cozymate.data.api.MemberService
import umc.cozymate.data.api.RoomService
import umc.cozymate.data.api.RuleService
import umc.cozymate.data.repository.repository.ChatRepository
import umc.cozymate.data.repository.repository.MemberRepository
import umc.cozymate.data.repository.repository.RoomRepository
import umc.cozymate.data.repository.repository.RuleRepository
import umc.cozymate.data.repository.repositoryImpl.ChatRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.MemberRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.RoomRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.RuleRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @ViewModelScoped
    @Provides
    fun providesChatRepository(
        chatService: ChatService
    ): ChatRepository = ChatRepositoryImpl(chatService)

    @ViewModelScoped
    @Provides
    fun providesRoomRepository(
        service: RoomService
    ): RoomRepository = RoomRepositoryImpl(service)

    @ViewModelScoped
    @Provides
    fun providesMemberRepository(
        service: MemberService
    ): MemberRepository = MemberRepositoryImpl(service)

    @ViewModelScoped
    @Provides
    fun providesRuleRepository(
        service: RuleService
    ): RuleRepository = RuleRepositoryImpl(service)

}