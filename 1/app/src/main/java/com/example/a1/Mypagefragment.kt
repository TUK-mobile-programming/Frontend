package com.example.a1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class Mypagefragment : Fragment() {

    private lateinit var imageProfile: ImageView
    private lateinit var textUsername: TextView

    private val PREFS_NAME = "mypage_prefs"
    private val KEY_USERNAME = "key_username"
    private val KEY_PROFILE_URI = "key_profile_uri"

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🟡 registerForActivityResult 등록
        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                selectedImageUri?.let {
                    imageProfile.setImageURI(it)

                    // SharedPreferences에 저장
                    val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    sharedPref.edit().putString(KEY_PROFILE_URI, it.toString()).apply()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)

        imageProfile = view.findViewById(R.id.image_profile)
        textUsername = view.findViewById(R.id.text_username)

        val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // 저장된 이름 불러오기
        val savedName = sharedPref.getString(KEY_USERNAME, "크리스티아누 호날두")
        textUsername.text = savedName

        // 저장된 프로필 이미지 불러오기
        val savedUriString = sharedPref.getString(KEY_PROFILE_URI, null)
        savedUriString?.let {
            val savedUri = Uri.parse(it)
            imageProfile.setImageURI(savedUri)
        }

        // 프로필 이미지 클릭 → 갤러리 열기
        imageProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }

        // 이름 클릭 → 다이얼로그 띄우기
        textUsername.setOnClickListener {
            showNameChangeDialog(textUsername)
        }

        // 캡슐/친구 수 표시
        view.findViewById<TextView>(R.id.text_capsule_count).text = "15"
        view.findViewById<TextView>(R.id.text_friend_count).text = "28"

        // 메뉴 클릭 이벤트
        view.findViewById<LinearLayout>(R.id.menu_alarm).setOnClickListener {
            Toast.makeText(requireContext(), "알림 설정 클릭됨", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<LinearLayout>(R.id.menu_friend).setOnClickListener {
            Toast.makeText(requireContext(), "친구 관리 클릭됨", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), FriendListActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.menu_account).setOnClickListener {
            Toast.makeText(requireContext(), "계정 설정 클릭됨", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), AccountActivity::class.java)
            startActivity(intent)
        }


        // About 메뉴 비활성화 및 흐리게
        val aboutMenus = listOf(
            view.findViewById<LinearLayout>(R.id.menu_other1),
            view.findViewById<LinearLayout>(R.id.menu_other2),
            view.findViewById<LinearLayout>(R.id.menu_other3)
        )
        for (menu in aboutMenus) {
            menu.isEnabled = false
            menu.alpha = 0.4f
        }

        return view
    }

    // 이름 변경 다이얼로그 함수
    private fun showNameChangeDialog(textView: TextView) {
        val editText = EditText(requireContext())
        editText.setText(textView.text)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("이름 변경")
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    textView.text = newName
                    val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    sharedPref.edit().putString(KEY_USERNAME, newName).apply()
                } else {
                    Toast.makeText(requireContext(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .create()

        dialog.show()
    }
}