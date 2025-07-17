package com.example.a1

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.a1.capsule.Capsule
import com.example.a1.databinding.ActivityCapsuleDetailBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CapsuleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCapsuleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCapsuleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val capsule = intent.getSerializableExtra("selected_capsule") as? Capsule
        Log.e("CapsuleDetailActivity", "Received capsule: $capsule")
        if (capsule != null) {
            displayCapsuleDetails(capsule)
        } else {
            finish()
        }
    }

    private fun displayCapsuleDetails(capsule: Capsule) {
        binding.r4jl26uviswr.text = capsule.title
        binding.r06qjlpfmidtq.text = capsule.body

        // ✅ Glide로 이미지 URI 처리 (URL일 경우도 지원)
        capsule.mediaUri?.let { uriString ->
            Glide.with(this)
                .load(uriString)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(binding.r9ljk3zxn6r8)
        } ?: run {
            binding.r9ljk3zxn6r8.setImageResource(R.mipmap.ic_launcher)
        }

        capsule.ddayMillis?.let { millis ->
            val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
            val calendar = Calendar.getInstance().apply { timeInMillis = millis }
            binding.rcnbse5vzia.text = dateFormat.format(calendar.time)
        } ?: run {
            binding.rcnbse5vzia.text = "만료일 없음"
        }
    }
}