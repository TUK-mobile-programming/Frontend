package com.example.a1

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a1.capsule.Capsule // Capsule 데이터 클래스 임포트
import com.example.a1.databinding.ActivityCapsuleDetailBinding // activity_capsule_detail.xml에 대한 뷰 바인딩 임포트
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CapsuleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCapsuleDetailBinding // 뷰 바인딩 변수 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 뷰 바인딩 초기화: activity_capsule_detail.xml 레이아웃을 액티비티에 연결
        binding = ActivityCapsuleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로부터 'selected_capsule'이라는 이름으로 전달된 Capsule 객체를 받음
        val capsule = intent.getSerializableExtra("selected_capsule") as? Capsule

        if (capsule != null) {
            // Capsule 객체가 성공적으로 전달되었다면, 해당 데이터를 UI에 표시하는 함수 호출
            displayCapsuleDetails(capsule)
        } else {
            // Capsule 데이터가 없을 경우 (예: 오류 또는 잘못된 호출) 액티비티를 종료
            // 필요하다면 사용자에게 오류 메시지를 보여줄 수도 있습니다.
            finish()
        }
    }

    // Capsule 객체의 데이터를 UI 요소에 바인딩하는 함수
    private fun displayCapsuleDetails(capsule: Capsule) {
        // 캡슐 제목 (ID: r4jl26uviswr) 설정
        binding.r4jl26uviswr.text = capsule.title

        // 캡슐 내용 (ID: r06qjlpfmidtq) 설정
        binding.r06qjlpfmidtq.text = capsule.body

        // 캡슐 이미지 (ID: r9ljk3zxn6r8) 설정
        capsule.mediaUri?.let { uriString -> // mediaUri가 null이 아닐 경우
            try {
                val imageUri = Uri.parse(uriString) // URI 문자열을 Uri 객체로 파싱
                binding.r9ljk3zxn6r8.setImageURI(imageUri) // 이미지 뷰에 이미지 설정
            } catch (e: Exception) {
                // URI 파싱이나 이미지 로드 중 오류 발생 시 기본 이미지 설정
                binding.r9ljk3zxn6r8.setImageResource(R.mipmap.ic_launcher)
                e.printStackTrace() // 오류 로그 출력
            }
        } ?: run {
            // mediaUri가 null일 경우 기본 이미지 설정
            binding.r9ljk3zxn6r8.setImageResource(R.mipmap.ic_launcher)
        }

        // 만료일 (ddayMillis) (ID: rcnbse5vzia) 설정
        capsule.ddayMillis?.let { millis -> // ddayMillis가 null이 아닐 경우
            val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()) // 날짜 형식 지정
            val calendar = Calendar.getInstance().apply { timeInMillis = millis } // 밀리초를 Calendar 객체로 변환
            binding.rcnbse5vzia.text = dateFormat.format(calendar.time) // 형식에 맞게 날짜 표시
        } ?: run {
            // ddayMillis가 null일 경우 "만료일 없음" 표시
            binding.rcnbse5vzia.text = "만료일 없음"
        }

        // 참고: 'Start Date' (ID: r3xfha9wdhsv)는 현재 Capsule 객체에 필드가 없으므로,
        // 이 코드에서는 해당 텍스트뷰를 업데이트하지 않습니다. 필요하다면 Capsule 데이터 클래스에
        // 'startDateMillis' 같은 필드를 추가하고 이 함수에서 설정 로직을 구현해야 합니다.
    }
}