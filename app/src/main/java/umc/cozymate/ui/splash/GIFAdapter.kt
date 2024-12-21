package umc.cozymate.ui.splash

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import umc.cozymate.R

class GIFAdapter(fragment: SplashActivity) : FragmentStateAdapter(fragment) {
    private val lottiesResIds = listOf(
        R.raw.onboarding_1,
        R.raw.onboarding_2,
        R.raw.onboarding_3
    )
    private val descriptions = listOf(
        "나와 꼭 맞는 룸메이트 찾기",
        "학교인증으로 신뢰성 UP",
        "롤앤룰로 공동체 생활을 더 윤택하게"
    )
    private val subDescriptions = listOf(
        "정형화된 라이프스타일로\n나와 꼭 맞는 룸메이트를 쉽고 빠르게 찾아봐요",
        "학교 이메일을 통한 학교 인증으로,\n신뢰성을 높였어요",
        "우리방의 역할과 규칙을 정하고, \n서로 역할을 잘 수행하고 있는지 확인할 수 있어요"
    )

    override fun getItemCount(): Int {
        return lottiesResIds.size
    }

    override fun createFragment(position: Int): Fragment {
        return GIFFragment.newInstance(
            lottiesResIds[position],
            descriptions[position],
            subDescriptions[position]
        )
    }

}