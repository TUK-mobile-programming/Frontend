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
import androidx.fragment.app.Fragment

class Mypagefragment : Fragment() {

    private val imagePickerRequestCode = 1001
    private lateinit var imageProfile: ImageView
    private lateinit var textUsername: TextView

    private val PREFS_NAME = "mypage_prefs"
    private val KEY_USERNAME = "key_username"
    private val KEY_PROFILE_URI = "key_profile_uri"

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

        // 저장된 프로필 이미지 Uri 불러오기
        val savedUriString = sharedPref.getString(KEY_PROFILE_URI, null)
        savedUriString?.let {
            val savedUri = Uri.parse(it)
            imageProfile.setImageURI(savedUri)
        }
        // 프로필 이미지 클릭 - 갤러리에서 선택
        imageProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, imagePickerRequestCode)
        }


        // 이름 클릭하면 수정 다이얼로그 띄우기
        textUsername.setOnClickListener {
            showNameChangeDialog(textUsername)
        }

        // 프로필 이미지 변경
        imageProfile = view.findViewById(R.id.image_profile)
        imageProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, imagePickerRequestCode)
        }

        // 캡슐/친구 수 표시
        view.findViewById<TextView>(R.id.text_capsule_count).text = "15"
        view.findViewById<TextView>(R.id.text_friend_count).text = "28"

        // 이름 TextView 클릭 시 수정 다이얼로그 띄우기
        val textUsername = view.findViewById<TextView>(R.id.text_username)
        textUsername.setOnClickListener {
            showNameChangeDialog(textUsername)
        }

        // 설정 메뉴 클릭 활성화
        view.findViewById<LinearLayout>(R.id.menu_alarm).setOnClickListener {
            Toast.makeText(requireContext(), "알림 설정 클릭됨", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<LinearLayout>(R.id.menu_friend).setOnClickListener {
            Toast.makeText(requireContext(), "친구 관리 클릭됨", Toast.LENGTH_SHORT).show()
            // FriendListActivity 실행
            val intent = Intent(requireContext(), FriendListActivity::class.java)
            startActivity(intent)
        }


        view.findViewById<LinearLayout>(R.id.menu_account).setOnClickListener {
            Toast.makeText(requireContext(), "계정 설정 클릭됨", Toast.LENGTH_SHORT).show()
        }

        // About 메뉴 비활성화 + 흐리게 처리
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagePickerRequestCode && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                imageProfile.setImageURI(it)

                // SharedPreferences에 프로필 사진 Uri 저장
                val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                sharedPref.edit().putString(KEY_PROFILE_URI, it.toString()).apply()
            }
        }
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
                } else {
                    Toast.makeText(requireContext(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .create()

        dialog.show()
    }


        }