package com.example.a1.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.a1.model.User
import org.json.JSONObject

object UserRepository {

    // ----- 캐시(메모리) -----
    private var currentUser: User? = null

    // ----- 퍼시스턴스 -----
    private const val PREF_NAME   = "user_pref"
    private const val KEY_JSON    = "user_json"

    /** 앱 시작 시 (Application.onCreate 등) 호출해두면 좋음 */
    fun init(context: Context) {
        if (currentUser != null) return      // 이미 초기화
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sp.getString(KEY_JSON, null)?.let { json ->
            kotlin.runCatching {
                val o = JSONObject(json)
                currentUser = User(
                    userId      = o.getInt("userId"),
                    email       = o.getString("email"),
                    accessToken = o.optString("accessToken", null)
                )
            }
        }
    }

    fun getCurrentUser(): User? = currentUser

    /** 로그인 성공 시 호출 */
    fun setUser(context: Context, user: User) {
        currentUser = user
        saveToPrefs(context)
    }

    /** 로그아웃 */
    fun clear(context: Context) {
        currentUser = null
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().remove(KEY_JSON).apply()
    }

    // -----------------------
    private fun saveToPrefs(context: Context) {
        currentUser?.let { u ->
            val json = JSONObject().apply {
                put("userId",      u.userId)
                put("email",       u.email)
                put("accessToken", u.accessToken)
            }.toString()
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_JSON, json).apply()
        }
    }
}