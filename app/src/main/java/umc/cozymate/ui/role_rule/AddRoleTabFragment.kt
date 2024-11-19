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
import umc.cozymate.data.model.entity.RoleData
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
    private val selectedMates = mutableListOf<RoleData.mateInfo>()
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

        return binding.root
    }

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

    // 입력 버튼 활성화
    private fun checkInput() {
        val memberFlag = memberBox.any{it.isChecked}
        val weekdayFlag = weekdayBox.any{it.isChecked} || binding.cbEmptyWeekday.isChecked
        val titleFlag = !binding.etInputRole.text.isNullOrEmpty()
        binding.btnInputButton.isEnabled = (memberFlag && weekdayFlag && titleFlag)
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

    private fun initMember(){
        val layoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ConvertDPtoPX(requireContext(),37)) // 여기 wrap으로 줄이기
        layoutParams.setMargins(
            ConvertDPtoPX(requireContext(), 0), // left
            ConvertDPtoPX(requireContext(), 0), // top
            ConvertDPtoPX(requireContext(), 8), // right
            ConvertDPtoPX(requireContext(), 8)  // bottom
        )

        // 맴버 선택 추가
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
                    val info = RoleData.mateInfo(mate.mateId,mate.nickname)
                    if (this.isChecked) {
                        selectedMates.add(info) // 체크되면 mate를 추가
                    } else {
                        selectedMates.remove(info) // 체크 해제되면 mate를 제거
                    }
                    checkAll()
                    checkInput()
                }
                this.layoutParams = layoutParams
            }
            memberBox.add(checkBox)
            binding.layoutMember.addView(checkBox)
        }
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
                    checkInput()
                    if (this.isChecked) {
                        repeatDayList.add(week[index]) // 체크되면 요일 문자열 추가
                        binding.cbEmptyWeekday.isChecked = false
                    } else {
                        repeatDayList.remove(week[index])
                        if(repeatDayList.isNullOrEmpty()) binding.cbEmptyWeekday.isChecked = true
                    }
                }
                this.layoutParams = layoutParams
            }
            weekdayBox.add(checkBox)
            binding.layoutWeekdays.addView(checkBox)
        }
    }

    private fun initClickListener(){
        // 모두 체크박스 체크 확인
        binding.cbEveryone.setOnClickListener{
            val isChecked= binding.cbEveryone.isChecked
            selectedMates.clear()
            for (i :Int in 0..mateList.size-1) {
                memberBox[i].isChecked = isChecked
                updateCheckBoxColor(memberBox[i],isChecked)
                if(isChecked) selectedMates.add( RoleData.mateInfo(mateList[i].mateId,mateList[i].nickname))
            }
            checkInput()
        }

        // 요일 없어요 체크 확인
        binding.cbEmptyWeekday.setOnClickListener{
            val isChecked =  binding.cbEmptyWeekday.isChecked
            if(isChecked){
                weekdayBox.forEach {  checkBox ->
                    checkBox.isChecked = !isChecked
                    updateCheckBoxColor(checkBox, !isChecked)
                }
                repeatDayList.clear()
            }
            checkInput()
        }

        binding.btnInputButton.setOnClickListener {
            val request = RoleRequest(selectedMates, binding.etInputRole.text.toString(),repeatDayList)
            Log.d(TAG,"Role 입력 데이터 ${request}")
            viewModel.createRole(roomId, request)
            viewModel.createResponse.observe(viewLifecycleOwner){response->
                if (!response.isSuccessful) Log.d(TAG,"연결 실패")
            }
            (requireActivity() as AddTodoActivity).finish()
        }
    }


    private fun updateCheckBoxColor(checkBox: CheckBox, isChecked: Boolean ){
        if (isChecked) {
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue))
        } else {
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
        }
    }

    private fun checkAll() {
        val isAll = memberBox.all { it.isChecked } // 모든 체크박스가 체크되었는지 확인
        binding.cbEveryone.isChecked = isAll // cbEveryday 체크 상태 업데이트
    }


    // dp를 픽셀로
    private fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }






}