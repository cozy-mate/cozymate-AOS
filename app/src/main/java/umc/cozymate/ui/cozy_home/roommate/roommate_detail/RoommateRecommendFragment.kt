//package umc.cozymate.ui.cozy_home.roommate.roommate_detail
//
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.util.Log
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.CheckBox
//import android.widget.LinearLayout
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.Observer
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import dagger.hilt.android.AndroidEntryPoint
//import umc.cozymate.R
//import umc.cozymate.data.domain.Preference
//import umc.cozymate.data.model.entity.RecommendedMemberInfo
//import umc.cozymate.databinding.FragmentRoommateRecommendBinding
//import umc.cozymate.ui.cozy_home.roommate.recommended_roommate.RecommendedRoommateVPAdapter
//import umc.cozymate.ui.cozy_home.roommate.search_roommate.SearchRoommateActivity
//import umc.cozymate.ui.message.MessageDetailActivity.Companion.ITEM_SIZE
//import umc.cozymate.ui.roommate.RoommateOnboardingActivity
//import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
//import umc.cozymate.ui.viewmodel.RoommateRecommendViewModel
//
//@AndroidEntryPoint
//class RoommateRecommendFragment: Fragment() {
//    private val TAG = this.javaClass.simpleName
//    lateinit var binding: FragmentRoommateRecommendBinding
//    lateinit var spf : SharedPreferences
//    private val viewModel: RoommateRecommendViewModel by viewModels()
//    private val detailViewModel : RoommateDetailViewModel by viewModels()
//    private var memberList : List<RecommendedMemberInfo> = emptyList()
//    private var chips  = mutableListOf<CheckBox>()
//    private var selectedChips = mutableListOf<String>()
//    private var isLifestyleExist : Boolean = false
//    private var nickname : String = ""
//    private var page = 0
//    private val recommendAdapter = EndlessRoommateRVAdapter(){ memberId ->
//        detailViewModel.getOtherUserDetailInfo(memberId)
//    }
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentRoommateRecommendBinding.inflate(inflater, container, false)
//        spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        setupInfiniteScroll()
//        getPreference()
//        if(isLifestyleExist)  viewModel.fetchRoommateListByEquality()
//        else viewModel.fetchRecommendedRoommateList()
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setupObserver()
//        initChip()
//        initGuide()
//        binding.lyGuide.visibility = if(isLifestyleExist) View.GONE else View.VISIBLE
//        // 사용자 검색으로 이동
//        binding.lyRoomMateSearch.setOnClickListener {
//            val intent = Intent(requireActivity(), SearchRoommateActivity::class.java)
//            startActivity(intent)
//        }
//
//    }
//
//
//    private fun setupInfiniteScroll(){
//        binding.rvRoommateDetailInfo.adapter = recommendAdapter
//        binding.rvRoommateDetailInfo.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//
//        binding.rvRoommateDetailInfo.addOnScrollListener(object  : RecyclerView.OnScrollListener(){
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
//                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
//                val itemTotalCount = recyclerView.adapter!!.itemCount
//
//                // 스크롤이 끝에 도달했는지 확인
////                if ( lastVisibleItemPosition == itemTotalCount-1){
//
////                }
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val visibleItemCount = layoutManager.childCount
//                val totalItemCount = layoutManager.itemCount
//                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//
//                val isLoading = viewModel.isLoading.value // 로딩 중이 아닐 때만 요청
//                if (!isLoading!!) {
//                    val isAtBottom =
//                        (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                    val isValidPosition = firstVisibleItemPosition >= 0
//                    val hasEnoughItems = totalItemCount >= ITEM_SIZE
//
//                    if (isAtBottom && isValidPosition && hasEnoughItems) {
//                        viewModel.fetchRoommateListByEquality(selectedChips, ++page)
//                    }
//                }
//            }
//        })
//    }
//
//    private fun getPreference() {
//        nickname = spf.getString("user_nickname", "").toString()
//        isLifestyleExist = spf.getBoolean("is_lifestyle_exist", false)
//        Log.d(TAG, "nickname: $nickname")
//    }
//
//    private fun setupObserver(){
//        viewModel.roommateList.observe(viewLifecycleOwner, Observer { list ->
//            if (list.isNullOrEmpty()&& page == 0){
//                if (isLifestyleExist){
//                    binding.rvRoommateDetailInfo.visibility = View.GONE
//                    binding.tvEmpty.visibility = View.VISIBLE
//                }
//            }
//            else{
//                memberList = list
//                binding.rvRoommateDetailInfo.visibility = View.VISIBLE
//                binding.tvEmpty.visibility = View.GONE
//                recommendAdapter.addMember(list)
//            }
//        })
//        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            (activity as? CozyHomeRoommateDetailActivity)?.showProgressBar(isLoading)
//        }
//        detailViewModel.otherUserDetailInfo.observe(viewLifecycleOwner) {otherUserDetail ->
//            if(otherUserDetail == null) return@observe
//            else{
//                val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
//                intent.putExtra("other_user_detail", otherUserDetail)
//                startActivity(intent)
//            }
//        }
//        detailViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            (activity as? CozyHomeRoommateDetailActivity)?.showProgressBar(isLoading)
//        }
//    }
//
//
//    private fun initGuide() {
//        binding.tvUserName.text = nickname
//        binding.btnGoLifestyle.setOnClickListener(){
//            val intent = Intent(requireActivity(), RoommateOnboardingActivity::class.java)
//            intent.putExtra("nickname",nickname)
//            startActivity(intent)
//        }
//    }
//
//
//    private fun initChip(){
//        val filterList  = listOf("출생년도","학번","학과","합격여부","기상시간","취침시간","소등시간","흡연여부","잠버릇","에어컨","히터", "생활패턴","친밀도",
//            "물건공유", "게임여부", "전화여부", "공부여부","섭취여부","청결예민도", "소음예민도","청소빈도", "음주빈도" ,"성격", "MBTI" )
//        for(t in filterList){
//            var box = CheckBox(requireContext())
//            box.apply {
//                val layoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ConvertDPtoPX(33)) // 여기 wrap으로 줄이기
//                layoutParams.setMargins(ConvertDPtoPX(4), 0, ConvertDPtoPX(4),ConvertDPtoPX(12))
//                setPadding(ConvertDPtoPX(14),ConvertDPtoPX(8),ConvertDPtoPX(14),ConvertDPtoPX(8))
//                setTextAppearance(R.style.TextAppearance_App_14sp_Medium)
//                setTextColor(ContextCompat.getColor(context, R.color.unuse_font))
//                setBackgroundResource(R.drawable.background_custom_chip)
//                gravity = Gravity.CENTER
//                buttonDrawable = null
//                text = t
//                this.layoutParams = layoutParams
//            }
//            box.setOnCheckedChangeListener {box, isChecked ->
//                val color = if(isChecked)  R.color.main_blue else R.color.unuse_font
//                val filter = Preference.getPrefByDisplayName(box.text.toString())
//                box.setTextColor(ContextCompat.getColor(requireContext(), color))
//
//                if (!isChecked) selectedChips.remove(filter)
//                else if(!selectedChips.contains(filter)) selectedChips.add(filter)
//
//                // 라이프 스타일이 있을 경우 재 검색
//                if(isLifestyleExist){
//                    page = 0
//                    viewModel.fetchRoommateListByEquality(selectedChips)
//                    recommendAdapter.clearMember()
//                }
//                // 라이프스타일이 없을 경우 칩이 선택되지 않았을때ㅑ만 추천사용자 보여주기
//                else{
//                    binding.rvRoommateDetailInfo.visibility = if (selectedChips.isEmpty()) View.VISIBLE else View.GONE
//                }
//
//            }
//            chips.add(box)
//            binding.chips.addView(box)
//        }
//    }
//    private fun ConvertDPtoPX( dp: Int): Int {
//        val density = resources.displayMetrics.density
//        return Math.round(dp.toFloat() * density)
//    }
//
//}