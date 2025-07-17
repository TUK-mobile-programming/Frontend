package com.example.a1

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.a1.capsule.Capsule
import com.example.a1.databinding.ActivityCapsuleDetailBinding
import com.example.a1.repository.CapsuleRepository
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
        val locationStr = intent.getStringExtra("location")

        if (capsule == null || locationStr == null) {
            Toast.makeText(this, "캡슐 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val lat = locationStr.split(",").getOrNull(0)?.toDoubleOrNull()
        val lng = locationStr.split(",").getOrNull(1)?.toDoubleOrNull()

        if (lat == null || lng == null) {
            Toast.makeText(this, "위치 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ✅ 상세 정보 fetch
        CapsuleRepository.fetchCapsuleDetail(
            capsuleId = capsule.capsuleId,
            userLat = lat,
            userLng = lng
        ) { ok, detailedCapsule, err ->
            runOnUiThread {
                if (ok && detailedCapsule != null) {
                    Log.e("캡슐 디테일", "받아온 캡슐 전체: $detailedCapsule")
                    Log.e("캡슐 디테일", "미디어 URI: ${detailedCapsule.mediaUri}")
                    displayCapsuleDetails(detailedCapsule)
                } else {
                    Toast.makeText(this, "캡슐 상세 정보를 불러오지 못했습니다: $err", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
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