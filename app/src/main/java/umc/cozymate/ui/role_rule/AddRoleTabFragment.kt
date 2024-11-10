package umc.cozymate.ui.role_rule

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.request.RoleRequest
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.databinding.FragmentAddRoleTabBinding
import umc.cozymate.ui.viewmodel.RoleViewModel
import umc.cozymate.ui.viewmodel.SelectedTabViewModel

@AndroidEntryPoint
class AddRoleTabFragment: Fragment() {
    lateinit var binding: FragmentAddRoleTabBinding
    private val TAG = this.javaClass.simpleName
    private var mateList :  List<GetRoomInfoResponse.Result.Mate> = emptyList()
    private val week = arrayListOf("월","화","수","목","금","토","일")
    private val repeatDayList =mutableListOf<String>()
    private val selectedMateIds = mutableListOf<Int>()
    private val memberBox = mutableListOf<CheckBox>()
    private val weekdayBox = mutableListOf<CheckBox>()
    private var roomId : Int = 0
    private val tabViewModel: SelectedTabViewModel by viewModels()
    private val viewModel: RoleViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRoleTabBinding.inflate(inflater, container, false)
        getPreference()
        initMember()
        initWeekdays()
        initClickListener()
        setTextinput()
        checkInput()

        binding.btnInputButton.setOnClickListener {
            val request = RoleRequest(selectedMateIds, binding.etInputRole.text.toString(),repeatDayList)
            Log.d(TAG,"Role 입력 데이터 ${request}")
            viewModel.createRole(roomId, request)
            viewModel.createResponse.observe(viewLifecycleOwner){response->
                if (response.isSuccessful) {
                    Log.d(TAG,"연결 성공 ${request}")
                    tabViewModel.setSelectedTab(1)
                } else {
                    Log.d(TAG,"연결 실패")
                }
            }
            (requireActivity() as AddTodoActivity).finish()
        }
        return binding.root
    }

    private fun initWeekdays() {
        val layoutParams  = LinearLayout.LayoutParams(ConvertDPtoPX(requireContext(),32),ConvertDPtoPX(requireContext(),32))
        layoutParams.marginEnd = ConvertDPtoPX(requireContext(),12)
        for(index in week.indices){
            val checkBox  = CheckBox(context).apply {
                text = week[index]
                setTextAppearance(requireContext(), R.style.TextAppearance_App_12sp_Medium)
                setBackgroundResource(R.drawable.ic_circle)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
                gravity = Gravity.CENTER
                buttonDrawable = null
                setOnClickListener {
                    updateCheckBoxColor(this, this.isChecked)
                    checkAllCheckboxes() // 모든 체크박스 체크 상태 확인
                    checkInput()
                    if (this.isChecked) {
                        repeatDayList.add(week[index]) // 체크되면 요일 문자열 추가
                    } else {
                        repeatDayList.remove(week[index]) // 체크 해제되면 요일 문자열 제거
                    }
                }
                this.layoutParams = layoutParams
            }
            weekdayBox.add(checkBox)
            binding.layoutWeekdays.addView(checkBox)
        }
    }
    private fun initMember(){
        val layoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ConvertDPtoPX(requireContext(),37)) // 여기 wrap으로 줄이기
//        layoutParams.marginStart = ConvertDPtoPX(requireContext(),8)
//        layoutParams.marginEnd = ConvertDPtoPX(requireContext(),8)
        layoutParams.setMargins(
            ConvertDPtoPX(requireContext(), 0), // left
            ConvertDPtoPX(requireContext(), 0), // top
            ConvertDPtoPX(requireContext(), 8), // right
            ConvertDPtoPX(requireContext(), 8)  // bottom
        )
        for (mate in mateList) {
            val checkBox = CheckBox(context).apply {
                text = mate.nickname
                setPadding( ConvertDPtoPX(requireContext(),20), ConvertDPtoPX(requireContext(),10), ConvertDPtoPX(requireContext(),20), ConvertDPtoPX(requireContext(),10))
                setTextAppearance(requireContext(), R.style.TextAppearance_App_12sp_Medium)
                setBackgroundResource(R.drawable.ic_input_role_member)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
                gravity = Gravity.CENTER
                buttonDrawable = null
                setOnClickListener {
                    updateCheckBoxColor(this,this.isChecked)
                    if (this.isChecked) {
                        selectedMateIds.add(mate.mateId) // 체크되면 mateId를 추가
                    } else {
                        selectedMateIds.remove(mate.mateId) // 체크 해제되면 mateId를 제거
                    }
                    checkInput()
                }
                this.layoutParams = layoutParams
            }
            memberBox.add(checkBox)
            binding.layoutMember.addView(checkBox)
        }
    }

    // 입력 버튼 활성화
    private fun checkInput() {
        val memberFlag = memberBox.any{it.isChecked}
        val weekdayFlag = weekdayBox.any{it.isChecked}
        val titleFlag = !binding.etInputRole.text.isNullOrEmpty()
        binding.btnInputButton.isEnabled = (memberFlag && weekdayFlag && titleFlag)
    }

    private fun initClickListener(){

        // 메일 체크박스 체크 여부 확인
        binding.cbEmptyWeekday.setOnClickListener{
            val isChecked =  binding.cbEmptyWeekday.isChecked
            weekdayBox.forEach {  checkBox ->
                checkBox.isChecked = isChecked // 모든 체크박스 체크 상태 변경
                updateCheckBoxColor(checkBox, isChecked)
            }
            repeatDayList.clear()
            if(isChecked) repeatDayList.addAll(week)
            checkInput()
        }
    }

    // shzredpreference에서 데이터 받아오기
    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)
        val mateListJson = spf.getString("mate_list", null)
        if (mateListJson != null) {
            mateList = getListFromPrefs(mateListJson)!!
            Log.d(TAG, "Mates list: $ mateList")
        } else {
            Log.d(TAG, "No mate list found")
        }

    }
    fun getListFromPrefs(json: String): List<GetRoomInfoResponse.Result.Mate>? {
        return try {
            val gson = Gson()
            val type = object : TypeToken<List<GetRoomInfoResponse.Result.Mate>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse mates list JSON", e)
            null
        }
    }

    // 텍스트 입력 설정
    private fun setTextinput() {
        val maxLength = 20 // 최대 글자수 설정
        binding.etInputRole.filters = arrayOf(InputFilter.LengthFilter(maxLength)) // 글자수 제한 적용
        binding.etInputRole.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 변경 전 텍스트에 대한 처리
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(s: Editable?) {
                checkInput()
            }
        })
    }

    // dp를 픽셀로
    private fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    private fun updateCheckBoxColor(checkBox: CheckBox, isChecked: Boolean ){
        if (isChecked) {
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue))
        } else {
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
        }
    }

    // 매일 체크
    private fun checkAllCheckboxes() {
        val allChecked = weekdayBox.all { it.isChecked } // 모든 체크박스가 체크되었는지 확인
        binding.cbEmptyWeekday.isChecked = allChecked // cbEveryday 체크 상태 업데이트
    }





}