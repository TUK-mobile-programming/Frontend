package com.example.a1.network

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object ApiClient {

    private val client = OkHttpClient()

    /** 에뮬레이터 → 로컬 서버라면 10.0.2.2 사용, 실제 서버라면 URL 교체 */
    private const val BASE_URL = "https://bold-seal-only.ngrok-free.app/api/v1/"

    /**
     * JSON POST 를 간단히 실행하고 결과 문자열을 그대로 돌려줍니다.
     *
     * @param endpoint  예) "/user/login"
     * @param jsonBody  전송할 JSONObject
     * @param onResult  (success, responseText)
     */
    fun postJson(
        endpoint: String,
        jsonBody: JSONObject,
        onResult: (Boolean, String) -> Unit
    ) {
        val body = jsonBody
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(BASE_URL + endpoint)
            .post(body)
            .build()

        Log.d("ApiClient", "POST → ${request.url} $jsonBody")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ApiClient", "fail: ${e.message}")
                onResult(false, e.message ?: "network error")
            }

            override fun onResponse(call: Call, response: Response) {
                val resBody = response.body?.string() ?: ""
                Log.d("ApiClient", "code=${response.code} body=$resBody")
                onResult(response.isSuccessful, resBody)
            }
        })
    }

    fun get(endpoint: String,
            onSuccess: (String) -> Unit,
            onFailure: (String) -> Unit) {

        val req = Request.Builder()
            .url(BASE_URL + endpoint)
            .get()
            .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) =
                onFailure(e.message ?: "network error")

            override fun onResponse(call: Call, res: Response) {
                val text = res.body?.string() ?: ""
                if (res.isSuccessful) onSuccess(text) else onFailure(text)
            }
        })
    }

    /**
     * 멀티파트 폼-데이터 POST
     *
     * @param endpoint   예) "capsule/"
     * @param body       이미 완성된 MultipartBody
     * @param onSuccess  HTTP 2xx 일 때 호출 – response body 문자열 전달
     * @param onFailure  실패 또는 2xx 외 코드 – 에러 메시지 전달
     */
    fun postMultipart(
        endpoint: String,
        body: MultipartBody,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val request = Request.Builder()
            .url(BASE_URL + endpoint)
            .post(body)
            .build()

        Log.d("ApiClient", "POST(Multipart) → ${request.url} (${body.parts.size} parts)")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ApiClient", "fail: ${e.message}")
                onFailure(e.message ?: "network error")
            }

            override fun onResponse(call: Call, response: Response) {
                val resBody = response.body?.string() ?: ""
                Log.d("ApiClient", "code=${response.code} body=$resBody")
                if (response.isSuccessful) onSuccess(resBody) else onFailure(resBody)
            }
        })
    }
}