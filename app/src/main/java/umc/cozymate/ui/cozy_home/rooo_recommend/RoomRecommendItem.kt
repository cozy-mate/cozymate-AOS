package umc.cozymate.ui.cozy_home.rooo_recommend

data class RoomRecommendItem(
    val roomName: String,
    val matchRate: String,
    val firstCriteria: String,
    val secondCriteria: String,
    val thirdCriteria: String,
    val fourthCriteria: String,
    val hashtagList: ArrayList<String>,
    val currentPeopleNum: Int,
    val maxPeopleNum: Int,
)

// 변수명은 서버 변수명에 맞춰 수정할 것.