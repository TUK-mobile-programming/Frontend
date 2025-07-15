package com.example.a1  // 원하는 패키지에 두세요

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.a1.MainActivity
import com.example.a1.network.ApiClient
import org.json.JSONObject

class LoginFragment : Fragment() {

    private var _bind: FragmentLoginBinding? = null
    private val bind get() = _bind!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _bind = FragmentLoginBinding.inflate(inflater, container, false)

        /* 로그인 버튼 */
        bind.loginButton.setOnClickListener { doLogin() }

        /* 회원가입 텍스트 */
        bind.makeAccount.setOnClickListener {
            // 예: findNavController().navigate(R.id.action_login_to_register)
        }

        return bind.root
    }

    private fun doLogin() = with(bind) {
        val id  = idInput.text.toString().trim()
        val pw  = passwordInput.text.toString().trim()

        if (id.isBlank() || pw.isBlank()) {
            toast("아이디/비밀번호를 입력하세요"); return
        }

        /* API 호출 예시 */
        val payload = JSONObject()
            .put("email", id)
            .put("password", pw)

        ApiClient.postJson("/user/login", payload) { ok, msg ->
            requireActivity().runOnUiThread {
                if (ok) {
                    toast("로그인 성공!")
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                } else  toast("실패: $msg")
            }
        }
    }

    private fun toast(msg:String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() { super.onDestroyView(); _bind = null }
}
