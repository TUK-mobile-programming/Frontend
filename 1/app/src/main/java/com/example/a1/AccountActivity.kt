package com.example.a1

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class AccountActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var userIdText: TextView    // 아이디 표시용 TextView
    private lateinit var userName: TextView       // 닉네임 표시용 TextView
    private lateinit var passwordEditText: EditText
    private lateinit var togglePasswordVisibility: ImageView

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_account)

        val backButton: ImageView = findViewById(R.id.btn_backed)

        backButton.setOnClickListener {
            finish()  // 이전 화면으로 이동
        }

        profileImage = findViewById(R.id.profileImage)
        userIdText = findViewById(R.id.userIdText)
        userName = findViewById(R.id.userName)
        passwordEditText = findViewById(R.id.passwordEditText)
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility)

        // 아이디는 수정 불가, TextView만 있음
        // userIdText.setText("사용자 아이디 예: 123456789") // 필요시 세팅

        // 비밀번호 수정 가능하게
        passwordEditText.isEnabled = true

        // 토글 버튼 보이게
        togglePasswordVisibility.visibility = View.VISIBLE

        // 비밀번호 토글 클릭 리스너
        togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // 커서 위치 끝으로 유지
            passwordEditText.setSelection(passwordEditText.text.length)
        }


        // 갤러리에서 프로필 사진 선택
        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val uri: Uri? = data?.data
                uri?.let {
                    val inputStream = contentResolver.openInputStream(it)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    profileImage.setImageBitmap(bitmap)
                }
            }
        }

        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }
        // 수정 07/17 - 계정 ID, 비번 보이게끔 함
        // 예시: Intent로 전달된 사용자 정보 받기
        val userId = intent.getStringExtra("user_id") ?: "default_id"
        val userNickname = intent.getStringExtra("user_nickname") ?: "홍길동"
        val userPassword = intent.getStringExtra("user_password") ?: ""

        // 화면에 표시
        userIdText.text = userId
        userName.text = userNickname
        passwordEditText.setText(userPassword)

    }
}