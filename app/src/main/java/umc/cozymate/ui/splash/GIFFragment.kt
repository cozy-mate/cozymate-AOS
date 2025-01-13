package umc.cozymate.ui.splash

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import umc.cozymate.R
import umc.cozymate.databinding.VpItemGifBinding

class GIFFragment : Fragment(R.layout.vp_item_gif) {
    private var lottieResId: Int? = null
    private var description: String? = null
    private var subDescription: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lottieResId = it.getInt(ARG_LOTTIE_RES_ID)
            description = it.getString(ARG_DESCRIPTION)
            subDescription = it.getString(ARG_SUB_DESCRIPTION)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = VpItemGifBinding.bind(view)
        binding.lottieAnimation.setAnimation(lottieResId ?: 0)
        binding.tvCozyDescription.text = description
        binding.tvCozyDescription2.text = subDescription

        // Lottie 애니메이션 끝날 때 페이지 전환
        binding.lottieAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                val currentItem = (activity as SplashActivity).binding.vpGif.currentItem
                val nextItem = if (currentItem + 1 < (activity as SplashActivity).binding.vpGif.adapter!!.itemCount) currentItem + 1 else 0
                (activity as SplashActivity).binding.vpGif.setCurrentItem(nextItem, true)
            }
            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    companion object {
        private const val ARG_LOTTIE_RES_ID = "lottie_res_id"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_SUB_DESCRIPTION = "sub_description"

        fun newInstance(lottieResId: Int, description: String, subDescription: String): GIFFragment {
            return GIFFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_LOTTIE_RES_ID, lottieResId)
                    putString(ARG_DESCRIPTION, description)
                    putString(ARG_SUB_DESCRIPTION, subDescription)
                }
            }
        }
    }
}