package com.riteshmaagadh.whatsappgrouplinks.ui.error

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.riteshmaagadh.whatsappgrouplinks.R
import com.riteshmaagadh.whatsappgrouplinks.databinding.ActivityErrorBinding
import com.riteshmaagadh.whatsappgrouplinks.ui.MainActivity

class ErrorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val hasNetworkGone = intent.extras?.getBoolean("has_network_gone")!!

        if (hasNetworkGone){
            Glide.with(this)
                .load(R.drawable.ic_no_data_bro)
                .into(binding.imageView)
            binding.textView.text = getString(R.string.no_internet)
        } else {
            Glide.with(this)
                .load(R.drawable.ic_feeling_sorry_rafiki)
                .into(binding.imageView)
            binding.textView.text = getString(R.string.something_went_wrong)
        }

        binding.retryBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}