package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.entity.PreferenceList
import umc.cozymate.data.model.response.member.stat.GetMyPreferenceResponse
import umc.cozymate.data.model.response.member.stat.PostMyPreferenceResponse
import umc.cozymate.data.model.response.member.stat.UpdatePreferenceResponse

interface MemberStatPreferenceRepository {

    suspend fun getMyPreference(accessToken: String): Response<GetMyPreferenceResponse>

    suspend fun postMyPreference(accessToken: String, preferenceList: PreferenceList): Response<PostMyPreferenceResponse>

    suspend fun updateMyPreference(accessToken: String, preferenceList: PreferenceList): Response<UpdatePreferenceResponse>

}