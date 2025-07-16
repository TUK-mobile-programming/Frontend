package com.example.a1

import android.content.Context
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
import com.example.a1.network.ApiClient

import org.json.JSONObject

class LoginFragment : Fragment() {

    private var _bind: FragmentLoginBinding? = null
    private val bind get() = _bind!!

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

    private fun attemptLogin() = with(bind) {
        val email = etEmail.text.toString().trim()
        val pw    = etPassword.text.toString().trim()
        if (email.isBlank() || pw.isBlank()) { toast("이메일과 비밀번호를 입력하세요"); return }

        val payload = JSONObject().apply {
            put("email", email)
            put("password", pw)
        }

        ApiClient.postJson("user/login", payload) { ok, res ->
            requireActivity().runOnUiThread {
                if (ok) {
                    /* 1) userId = 서버가 보낸 순수 문자열   ─ 공백·개행 제거 */
                    val userId = res.trim()           // 예: "4"

                    /* 2) SharedPreferences 저장 */
                    requireContext()
                        .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("userId", userId)  // 문자열 그대로 저장
                        .apply()

                    toast("로그인 성공!")

                    /* 3) 로그인 → 홈 화면으로 이동 (기존 로직 유지) */
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

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }
}