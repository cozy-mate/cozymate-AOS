package umc.cozymate.ui.feed

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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
                imageViewModel.imageList.collect { urls->
                    urls.let {
                        binding.tvImageCount.text = urls.size.toString()
                        updateImageLayout(urls)
                        imageList = urls
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"destory")
        imageViewModel.deleteImages(imageList)
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
            pickImagesLauncher.launch(arrayOf("image/*"))
        }
    }


    private val pickImagesLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val contentResolver = applicationContext.contentResolver
            uris.forEach { uri ->
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            imageViewModel.uploadImages(uris) // ✅ 기존 선택한 이미지 유지하면서 추가
        }
    }


    private fun updateImageLayout( urls: List<String>) {
        binding.layoutImages.removeAllViews()
        for (url in urls) {
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    80f.fromDpToPx(), 80f.fromDpToPx()
                ).apply {
                    setMargins(8f.fromDpToPx(), 0, 8f.fromDpToPx(), 0)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            imageView.setOnClickListener {
                imageViewModel.deleteImages(listOf(url))
            }

            // ✅ Glide를 사용하여 이미지 로드
            Glide.with(this).load(url).into(imageView)

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