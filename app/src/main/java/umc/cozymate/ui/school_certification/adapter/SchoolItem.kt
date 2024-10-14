package umc.cozymate.ui.school_certification.adapter

data class SchoolItem(
    val id: Int, // 학교 id (이름 대신 구분하는 값이 있다면)
    val name: String,
    val logoUrl: String // 학교 로고 url
)