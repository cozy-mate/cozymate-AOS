package umc.cozymate.ui.feed

import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import umc.cozymate.data.model.request.EditPostRequest
import umc.cozymate.databinding.ActivityWriteFeedBinding
import umc.cozymate.ui.viewmodel.FeedViewModel
import umc.cozymate.ui.viewmodel.ImageViewModel
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.fromDpToPx

@AndroidEntryPoint
class WriteFeedActivity : AppCompatActivity() {
    private val TAG  = this.javaClass.simpleName
    lateinit var binding : ActivityWriteFeedBinding
    lateinit var spf : SharedPreferences
    private var content : String? = ""
    private val viewModel : FeedViewModel by viewModels()
    private val imageViewModel : ImageViewModel by viewModels()
    private var roomId : Int = 0
    private var postId : Int = 0
    private var imageList : List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentData()
        initOnClickListener()
        setupObserver()
        setTextListener()
        checkInput()
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                imageViewModel.selectedImageUris.collect { uris->
                    uris?.let {
                        if(!uris.isNullOrEmpty()){
                            binding.tvImageCount.text = uris.size.toString()
                            updateImageLayout(uris)
                        }
                        else
                            binding.tvImageCount.text = "null"
                    }
                }
            }
        }

    }



    private fun getIntentData(){
        roomId = intent.getIntExtra("roomId",0)
        postId = intent.getIntExtra("postId",0)
        content = intent.getStringExtra("content")
    }

    private fun  initOnClickListener(){
        binding.btnInputButton.setOnClickListener{
            content = binding.etInputContent.text.toString()
            val request = EditPostRequest(roomId = roomId, postId = postId, content = content!!, imageList = imageList )
            if (postId == 0)viewModel.createPost(request)
            else viewModel.editPost(request)
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.layoutAddImage.setOnClickListener {
            pickMultipleImagesLauncher.launch("image/*")
        }
    }

    // 이미지 가져오기
    private val pickMultipleImagesLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            imageViewModel.setSelectedImages(uris) // 선택한 이미지 리스트를 ViewModel에 저장
        }
        Log.d(TAG,"uris : ${uris}")
    }

    private fun updateImageLayout( uris: List<Uri>) {
        for (uri in uris) {
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    80f.fromDpToPx(), 80f.fromDpToPx()
                ).apply {
                    setMargins(8f.fromDpToPx(), 0, 8f.fromDpToPx(), 0)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            // ✅ Glide를 사용하여 이미지 로드
            Glide.with(this).load(uri).into(imageView)

            binding.layoutImages.addView(imageView) // ✅ LinearLayout에 추가
        }
    }


    private fun setupObserver() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.isSuccess.observe(this) { isSuccess ->
            if(isSuccess) finish()
        }
        imageViewModel.imageList.observe(this){list->
            imageList = list
        }


    }

    private fun setTextListener() {
        if(!content.isNullOrBlank()) binding.etInputContent.setText(content)
        binding.etInputContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {checkInput()}
            override fun afterTextChanged(p0: Editable?) {
                checkInput()
            }
        })

    }

    private fun checkInput() {
        binding.btnInputButton.isEnabled = !binding.etInputContent.text.isNullOrEmpty()
    }

}