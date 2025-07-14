// Listactivity.kt
package com.example.a1

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView // ImageView import 추가
import android.widget.TextView // TextView import 추가
import androidx.activity.enableEdgeToEdge // 현재 사용하지 않으므로 제거 가능 (또는 주석 처리)
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat // 현재 사용하지 않으므로 제거 가능 (또는 주석 처리)
import androidx.core.view.WindowInsetsCompat // 현재 사용하지 않으므로 제거 가능 (또는 주석 처리)
import com.bumptech.glide.Glide // Glide 라이브러리 사용을 위해 import
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Listactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listactivity)

        // 1. XML 레이아웃에 정의된 뷰들을 찾아옵니다.
        val listedTitle: TextView = findViewById(R.id.listed_title)
        val listedImage: ImageView = findViewById(R.id.list_image)
        val listedText: TextView = findViewById(R.id.listed_text)
        val startDate: TextView = findViewById(R.id.start_date)
        val endDate: TextView = findViewById(R.id.end_date)
        val backButton: AppCompatImageView = findViewById(R.id.listed_back2)

        // 2. Intent로부터 Listfragment에서 putExtra로 보낸 데이터를 추출합니다.
        // 키 이름이 Listfragment에서 사용한 것과 정확히 일치해야 합니다.
        val title = intent.getStringExtra("CAPSULE_TITLE")
        val body = intent.getStringExtra("CAPSULE_BODY")
        val imageUriString = intent.getStringExtra("CAPSULE_IMAGE_URI")
        val startDateMillis = intent.getLongExtra("CAPSULE_START_DATE_MILLIS", -1L) // 기본값 -1L
        val ddayMillis = intent.getLongExtra("CAPSULE_DDAY_MILLIS", -1L)       // 기본값 -1L

        // 3. 추출한 데이터를 각 뷰에 설정하여 표시합니다.

        // 제목 설정
        listedTitle.text = title ?: "제목 없음" // 제목이 null일 경우 "제목 없음"으로 표시

        // 내용 설정
        listedText.text = body ?: "내용 없음"   // 내용이 null일 경우 "내용 없음"으로 표시

        // 이미지 설정 (Glide 라이브러리 사용)
        if (!imageUriString.isNullOrEmpty()) {
            // 이미지 URI가 있다면 Glide를 사용하여 로드합니다.
            Glide.with(this)
                .load(Uri.parse(imageUriString))
                .placeholder(R.drawable.sampleimage) // 이미지가 로딩 중일 때 표시할 이미지
                .error(R.drawable.sampleimage)       // 이미지 로딩 실패 시 표시할 이미지
                .into(listedImage)
        } else {
            // 이미지 URI가 없으면 기본 이미지(sampleimage)를 표시합니다.
            listedImage.setImageResource(R.drawable.sampleimage)
        }

        // 날짜 설정 (yyyy년 M월 d일 형식으로 포맷)
        val dateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())

        if (startDateMillis != -1L) { // 유효한 시작 날짜가 있다면
            startDate.text = dateFormat.format(Date(startDateMillis))
        } else {
            startDate.text = "날짜 미정" // 시작 날짜가 없으면 "날짜 미정"
        }

        if (ddayMillis != -1L) { // 유효한 종료 날짜(D-Day)가 있다면
            endDate.text = dateFormat.format(Date(ddayMillis))
        } else {
            endDate.text = "날짜 미정" // 종료 날짜가 없으면 "날짜 미정"
        }


        // 4. 뒤로가기 버튼 클릭 리스너 설정
        backButton.setOnClickListener {
            finish() // 현재 액티비티를 종료하고 이전 화면으로 돌아갑니다.
        }
    }
}