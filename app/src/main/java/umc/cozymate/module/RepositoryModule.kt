package umc.cozymate.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import umc.cozymate.data.api.ChatService
import umc.cozymate.data.api.FavoritesService
import umc.cozymate.data.api.FeedService
import umc.cozymate.data.api.InquiryService
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
import umc.cozymate.data.repository.repository.ChatRepository
import umc.cozymate.data.repository.repository.FavoritesRepository
import umc.cozymate.data.repository.repository.FeedRepository
import umc.cozymate.data.repository.repository.InquiryRepository
import umc.cozymate.data.repository.repository.MemberRepository
import umc.cozymate.data.repository.repository.MemberStatPreferenceRepository
import umc.cozymate.data.repository.repository.MemberStatRepository
import umc.cozymate.data.repository.repository.ReportRepository
import umc.cozymate.data.repository.repository.RoleRepository
import umc.cozymate.data.repository.repository.RoomLogRepository
import umc.cozymate.data.repository.repository.RoomRepository
import umc.cozymate.data.repository.repository.RoommateRepository
import umc.cozymate.data.repository.repository.RuleRepository
import umc.cozymate.data.repository.repository.TodoRepository
import umc.cozymate.data.repository.repositoryImpl.ChatRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.FavoritesRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.FeedRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.InquiryRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.MemberRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.MemberStatPreferenceRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.MemberStatRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.ReportRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.RoleRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.RoomLogRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.RoomRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.RoommateRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.RuleRepositoryImpl
import umc.cozymate.data.repository.repositoryImpl.TodoRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @ViewModelScoped
    @Provides
    fun providesFavoritesRepository(
        favoritesService: FavoritesService
    ): FavoritesRepository = FavoritesRepositoryImpl(favoritesService)

    @ViewModelScoped
    @Provides
    fun providesRoommateRepository(
        roommateService: RoommateService
    ): RoommateRepository = RoommateRepositoryImpl(roommateService)

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
    fun providesRoomLogRepository(
        service: RoomLogService
    ): RoomLogRepository = RoomLogRepositoryImpl(service)

    @ViewModelScoped
    @Provides
    fun providesMemberRepository(
        service: MemberService
    ): MemberRepository = MemberRepositoryImpl(service)

    @ViewModelScoped
    @Provides
    fun providesMemberStatRepository(
        service: MemberStatService
    ): MemberStatRepository = MemberStatRepositoryImpl(service)

    @ViewModelScoped
    @Provides
    fun providesMemberStatPreferenceRepository(
        preferenceService: MemberStatPreferenceService
    ): MemberStatPreferenceRepository = MemberStatPreferenceRepositoryImpl(preferenceService)

    @ViewModelScoped
    @Provides
    fun providesRuleRepository(
        service: RuleService
    ): RuleRepository = RuleRepositoryImpl(service)

    @ViewModelScoped
    @Provides
    fun providesRoleRepository(
        service: RoleService
    ): RoleRepository = RoleRepositoryImpl(service)

    @ViewModelScoped
    @Provides
    fun providesTodoRepository(
        todoService: TodoService
    ): TodoRepository = TodoRepositoryImpl(todoService)

    @ViewModelScoped
    @Provides
    fun providesReportRepository(
        reportService: ReportService
    ): ReportRepository = ReportRepositoryImpl(reportService)

    @ViewModelScoped
    @Provides
    fun providesInquiryRepository(
        inquiryService: InquiryService
    ): InquiryRepository = InquiryRepositoryImpl(inquiryService)

    @ViewModelScoped
    @Provides
    fun providesFeedRepository(
        feedService: FeedService
    ): FeedRepository = FeedRepositoryImpl(feedService)
}