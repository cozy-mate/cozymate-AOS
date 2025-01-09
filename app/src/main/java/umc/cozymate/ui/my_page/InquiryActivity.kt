package umc.cozymate.ui.my_page

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.InquiryData
import umc.cozymate.databinding.ActivityInquiryBinding
import umc.cozymate.ui.viewmodel.InquiryViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class InquiryActivity: AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding : ActivityInquiryBinding
    private val viewModel : InquiryViewModel by viewModels()
    private var inquiryList : List<InquiryData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        setupObservers()
        viewModel.getInquiry()
        setClickListener()
    }


    private fun setClickListener() {
        binding.btnInputButton.setOnClickListener{
            val intent : Intent = Intent(this, WriteInquiryActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers(){
        viewModel.getInquiryResponse.observe(this, Observer {response ->
            if (response == null) return@Observer
            if (response.isSuccessful){
                inquiryList = response.body()!!.result
                updateUI()
            }
        })

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun updateUI() {
        val inquiryRVAdaper =  InquiryRVAdapter(inquiryList)
        binding.rvInquiry.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        binding.rvInquiry.adapter = inquiryRVAdaper
    }

}