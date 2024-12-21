package umc.cozymate.ui.university_certification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

import umc.cozymate.ui.university_certification.adapter.UniversitylItem

class SchoolSearchViewModel : ViewModel() {
    val searchQuery = MutableLiveData<String>("")

    private val _schoolList = MutableLiveData<List<UniversitylItem>>() // 전체 학교 목록
    val schoolList: LiveData<List<UniversitylItem>> = _schoolList

    // 검색어에 따라 학교 목록이 필터링 결과
    val filteredSchoolList: LiveData<List<UniversitylItem>> = searchQuery.map { query ->
        if (query.isEmpty()) {
            _schoolList.value ?: emptyList()
        } else {
            _schoolList.value?.filter { it.name.contains(query, true) } ?: emptyList()
        }

    }
    
    // 검색 결과가 비었는지 확인하는 변수
    val isEmpty: LiveData<Boolean> = filteredSchoolList.map {
        it.isEmpty()
    }

    // 초기 데이터 설정 (예시 데이터)
    init {
        _schoolList.value = listOf(
            UniversitylItem(1, "가톨릭대학교", "url_to_logo_3"),
            UniversitylItem(2, "인하대학교", "url_to_logo_1"),
            UniversitylItem(3, "숭실대학교", "url_to_logo_3"),
            UniversitylItem(4, "한국공학대학교", "url_to_logo_3")
        )
    }
}