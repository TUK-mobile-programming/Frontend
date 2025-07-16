package com.example.a1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.a1.R
import com.example.a1.databinding.FragmentLoginBinding
import com.example.a1.model.User
import com.example.a1.network.ApiClient
import com.example.a1.repository.UserRepository

import org.json.JSONObject

class LoginFragment : Fragment() {

    private var _bind: FragmentLoginBinding? = null
    private val bind get() = _bind!!
    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _bind = FragmentLoginBinding.inflate(inflater, container, false)

        /* 로그인 클릭 */
        bind.btnLogin.setOnClickListener { attemptLogin() }

        /* 회원가입 화면으로 이동(필요 시) */
        bind.btnRegister.setOnClickListener {
            // Navigation-Component 사용 시: findNavController().navigate(R.id.action_login_to_register)
            Toast.makeText(requireContext(), "회원가입 화면으로 이동", Toast.LENGTH_SHORT).show()
        }

        return bind.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    private fun attemptLogin() = with(bind) {
        val email = etEmail.text.toString().trim()
        val pw = etPassword.text.toString().trim()
        if (email.isBlank() || pw.isBlank()) {
            toast("이메일과 비밀번호를 입력하세요"); return
        }

        val payload = JSONObject().apply {
            put("email", email)
            put("password", pw)
        }

        // ... 생략 …
        ApiClient.postJson("user/login", payload) { ok, res ->
            requireActivity().runOnUiThread {
                if (ok) {
                    /* ① 서버에서 받은 응답(JSON)을 파싱해서 사용자 정보 얻기
                       예: { "user_id": 3, "email": "user1", "access_token": "abc..." } */
                    val obj = JSONObject(res)
                    val user = User(
                        userId = obj.getInt("user_id"),
                        email = obj.getString("email"),
                        accessToken = obj.optString("access_token", null)
                    )
                    // ② Repository에 저장 + prefs에 기록
                    UserRepository.setUser(requireContext(), user)

                    // ③ 화면 전환
                    findNavController().navigate(
                        R.id.action_loginFragment_to_home,
                        null,
                        androidx.navigation.NavOptions.Builder()
                            .setPopUpTo(R.id.loginFragment, true)
                            .build()
                    )
                } else {
                    toast("로그인 실패: $res")
                }
            }
        }

    }
}