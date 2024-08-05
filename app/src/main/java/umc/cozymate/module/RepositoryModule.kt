package umc.cozymate.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import umc.cozymate.data.api.ChatService
import umc.cozymate.data.repository.repository.ChatRepository
import umc.cozymate.data.repository.repositoryImpl.ChatRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @ViewModelScoped
    @Provides
    fun providesChatRepository(
        chatService: ChatService
    ): ChatRepository = ChatRepositoryImpl(chatService)

}