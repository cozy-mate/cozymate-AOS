package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.api.MemberStatPreferenceService
import umc.cozymate.data.model.entity.PreferenceList
import umc.cozymate.data.model.response.member.stat.GetMyPreferenceResponse
import umc.cozymate.data.model.response.member.stat.PostMyPreferenceResponse
import umc.cozymate.data.repository.repository.MemberStatPreferenceRepository
import javax.inject.Inject

class MemberStatPreferenceRepositoryImpl @Inject constructor(
    private val api: MemberStatPreferenceService
) : MemberStatPreferenceRepository {

    override suspend fun getMyPreference(accessToken: String): Response<GetMyPreferenceResponse> {
        return api.getMyPreference(accessToken)
    }

    override suspend fun postMyPreference(
        accessToken: String,
        preferenceList: PreferenceList
    ): Response<PostMyPreferenceResponse> {
        return api.postMyPreference(accessToken, preferenceList)
    }

    override suspend fun updateMyPreference(
        accessToken: String,
        preferenceList: PreferenceList
    ): Response<PostMyPreferenceResponse> {
        return api.updateMyPreference(accessToken, preferenceList)
    }
}