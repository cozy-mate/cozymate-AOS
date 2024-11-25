package umc.cozymate.ui.role_rule

import android.content.Context
import android.content.SharedPreferences
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
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.entity.RoleData.mateInfo
import umc.cozymate.data.model.request.RoleRequest
import umc.cozymate.data.model.response.room.GetRoomInfoResponse.Result.MateDetail
import umc.cozymate.databinding.FragmentAddRoleTabBinding
import umc.cozymate.ui.viewmodel.RoleViewModel

@AndroidEntryPoint
class AddRoleTabFragment(private val isEditable : Boolean): Fragment(), ItemClick {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: FragmentAddRoleTabBinding
    lateinit var spf : SharedPreferences
    private var repeatDayList =mutableListOf<String>()
    private var selectedMates = mutableListOf<mateInfo>()
    private val weekdayBox = mutableListOf<Daybox>()
    private var mateBox = mutableListOf<MemberBox>()
    private var roomId : Int = 0
    private var roleId : Int = 0
    private var content : String? = ""
    private val viewModel: RoleViewModel by viewModels()
    private var dummy : List<Mate> = emptyList()

    override fun deleteClickFunction() {
        viewModel.deleteRole(roomId, roleId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRoleTabBinding.inflate(inflater, container, false)
        spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        getPreference()
        //initMember(dummy)
        initWeekdays()
        initdata()
        setTextinput()
        initClickListener()
        checkInput()
        return binding.root
    }

    private fun getPreference() {
        roomId = spf.getInt("room_id", 0)
        val mateListJson = spf.getString("mate_list", null)
        if (mateListJson != null) {
            val list =  getListFromPrefs(mateListJson)!!
            initMember(list)
            Log.d(TAG, "Mates list: $ mateBox")
        }
        else Log.d(TAG, "No mate list found")
    }
    fun getListFromPrefs(json: String): List<MateDetail>? {
        return try {
            val gson = Gson()
            val type = object : TypeToken<List<MateDetail>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse mates list JSON", e)
            null
        }
    }

    private fun initdata(){
        if(isEditable){
            roleId = spf.getInt("role_id",0)
            spf.edit().remove("role_id")
            content = spf.getString("role_content","")
            spf.edit().remove("role_content")
            try{
                var json = spf.getString("role_mate_list","")
                var type = object : TypeToken<MutableList<mateInfo>>(){}.type
                if(!json.isNullOrEmpty()) {
                    selectedMates = Gson().fromJson(json,type)
                    for(mate in mateBox )
                        if(selectedMates.contains(mate.getMateInfo()))mate.box.isChecked = true
                }
                json = spf.getString("role_day_list","")
                type = object : TypeToken<MutableList<String>>(){}.type
                if(!json.isNullOrEmpty()) {
                    repeatDayList = Gson().fromJson(json,type)
                    for(day in weekdayBox)
                        if(repeatDayList.contains(day.weekDay)) day.box.isChecked = true
                }
            }catch (e: JsonParseException){ e.printStackTrace() }
            if(repeatDayList.isNullOrEmpty())binding.cbEmptyWeekday.isChecked = true
            spf.edit().remove("role_mate_list")
            spf.edit().remove("role_day_list")
            spf.edit().apply()
        }
        else{
            roleId=0
            content = ""
            selectedMates.clear()
            repeatDayList.clear()
        }
    }

    private fun initMember(list : List<MateDetail>){
        for (l in list) {
            val mate = Mate(l)
            val m = MemberBox(mate,CheckBox(context))
            m.box.setOnCheckedChangeListener { box, isChecked ->
                val color = if(isChecked)  R.color.main_blue else R.color.unuse_font
                box.setTextColor(ContextCompat.getColor(requireContext(),color))
                if (box.isChecked && !selectedMates.contains(m.getMateInfo())) selectedMates.add(m.getMateInfo()) // 체크되면 mate를 추가
                if(!isChecked) selectedMates.remove(m.getMateInfo()) // 체크 해제되면 mate를 제거
                checkAll()
                checkInput()
            }
            binding.layoutMember.addView(m.box)
            mateBox.add(m)
        }
    }

    private fun initWeekdays() {
        val week =  listOf("월","화","수","목","금","토","일")
        for(w in week){
            val box = Daybox(w,CheckBox(context))
            weekdayBox.add(box)
        }
    }
    // 입력 버튼 활성화
    private fun checkInput() {
        val memberFlag = mateBox.any{it.box.isChecked}
        val weekdayFlag = weekdayBox.any{it.box.isChecked} || binding.cbEmptyWeekday.isChecked
        val titleFlag = !binding.etInputRole.text.isNullOrEmpty()
        binding.btnInputButton.isEnabled = (memberFlag && weekdayFlag && titleFlag)
    }

    // 텍스트 입력 설정
    private fun setTextinput() {
        val maxLength = 20 // 최대 글자수 설정
        binding.etInputRole.filters = arrayOf(InputFilter.LengthFilter(maxLength)) // 글자수 제한 적용
        binding.etInputRole.setText(content)
        binding.etInputRole.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun initClickListener(){
        // 모두 체크박스 체크 확인
        binding.cbEveryone.setOnClickListener{
            val isChecked= binding.cbEveryone.isChecked
            for(mate in mateBox)mate.box.isChecked = isChecked
            checkInput()
        }

        // 요일 없어요 체크 확인
        binding.cbEmptyWeekday.setOnClickListener{
            val isChecked =  binding.cbEmptyWeekday.isChecked
            if(isChecked){
                weekdayBox.forEach { 
                    it.box.isChecked = !isChecked }
                repeatDayList.clear()
            }
            checkInput()
        }

        binding.btnInputButton.setOnClickListener {
            val request = RoleRequest(selectedMates, binding.etInputRole.text.toString(),repeatDayList)
            Log.d(TAG,"Role 입력 데이터 ${request}")
            if(isEditable) viewModel.editRole(roomId, roleId, request)
            else viewModel.createRole(roomId, request)

            // 돌아갈 룰앤롤탭 순서 지정
            spf.edit().putInt("tab_idx", 1)
            spf.edit().apply()

            (requireActivity() as AddTodoActivity).finish()
        }
    }

    private fun checkAll() {
        val isAll = mateBox.all { it.box.isChecked } // 모든 체크박스가 체크되었는지 확인
        binding.cbEveryone.isChecked = isAll // cbEveryone 체크 상태 업데이트
    }

    // dp를 픽셀로
    private fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }


    inner class Daybox(
        val weekDay : String,
        val box : CheckBox
    ){
        init { setBox() }
        fun setBox(){
            val layoutParams  = LinearLayout.LayoutParams(ConvertDPtoPX(requireContext(),32),ConvertDPtoPX(requireContext(),32))
            layoutParams.marginEnd = ConvertDPtoPX(requireContext(),12)
            box.apply {
                text = weekDay
                setTextAppearance(requireContext(), R.style.TextAppearance_App_12sp_Medium)
                setBackgroundResource(R.drawable.ic_circle)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
                gravity = Gravity.CENTER
                buttonDrawable = null
                this.layoutParams = layoutParams
            }
            box.setOnCheckedChangeListener { box, isChecked ->
                val color = if(isChecked)  R.color.main_blue else R.color.unuse_font
                box.setTextColor(ContextCompat.getColor(requireContext(),color))
                if (isChecked && !repeatDayList.contains(this.weekDay)) {
                    repeatDayList.add(this.weekDay) // 체크되면 요일 문자열 추가
                    binding.cbEmptyWeekday.isChecked = false
                }
                if(!isChecked && repeatDayList.contains(this.weekDay)) repeatDayList.remove(this.weekDay)
                if(repeatDayList.isNullOrEmpty()) binding.cbEmptyWeekday.isChecked = true
                checkInput()
            }
            binding.layoutWeekdays.addView(box)
        }
    }
}