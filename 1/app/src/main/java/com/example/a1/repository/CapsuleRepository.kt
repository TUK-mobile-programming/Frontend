package com.example.a1.repository

import android.content.Context
import android.net.Uri
import com.example.a1.capsule.Capsule
import com.example.a1.capsule.CapsuleContent
import com.example.a1.network.ApiClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

object CapsuleRepository {

    // ────────────────── 캐시 ──────────────────
    private val opened = mutableListOf<Capsule>()
    private val closed = mutableListOf<Capsule>()

    fun getOpenedCapsules(): List<Capsule> = opened
    fun getClosedCapsule(): List<Capsule> = closed

    fun clearCache() {
        opened.clear()
        closed.clear()
    }

    // ────────────────── 날짜 포맷 ───────────────
    private val isoFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)

    // ────────────────── ① 목록 새로고침 ─────────
    fun refreshCapsuleList(
        userId: Int,
        onComplete: (Boolean, String?) -> Unit
    ) {
        fun parse(json: String, target: MutableList<Capsule>) {
            val arr = JSONArray(JSONObject(json).getJSONArray("capsules").toString())
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                target += Capsule(
                    capsuleId = o.getInt("capsule_id"),
                    title = o.getString("capsule_name"),
                    body = "",
                    tags = "",
                    mediaUri = null,
                    ddayMillis = isoFmt.parse(o.getString("open_at"))?.time,
                    condition = null,
                    isJoint = (o.getInt("type") == 1),
                    latitude = o.optDouble("location_lat"),
                    longitude = o.optDouble("location_lng"),
                    isOpened = (o.getInt("type") == 0)
                )
            }
        }

        opened.clear()
        closed.clear()

        ApiClient.get("capsule/opened/$userId",
            { body ->
                parse(body, opened)
                ApiClient.get("capsule/closed/$userId",
                    { body2 -> parse(body2, closed); onComplete(true, null) },
                    { err2 -> onComplete(false, err2) }
                )
            },
            { err -> onComplete(false, err) }
        )
    }

    // ────────────────── ② 상세 열람 ────────────
    fun fetchCapsuleDetail(
        capsuleId: Int,
        userLat: Double,
        userLng: Double,
        onComplete: (Boolean, Capsule?, String?) -> Unit
    ) {
        ApiClient.get("capsule/$capsuleId?user_lat=$userLat&user_lng=$userLng",
            { body ->
                try {
                    val o = JSONObject(body)
                    val contentsJ = o.getJSONArray("contents")
                    val contents = mutableListOf<CapsuleContent>()
                    for (i in 0 until contentsJ.length()) {
                        val c = contentsJ.getJSONObject(i)
                        contents += CapsuleContent(
                            type = c.getString("content_type"),
                            data = c.getString("content_data")
                        )
                    }
                    val cap = Capsule(
                        capsuleId = o.getInt("capsule_id"),
                        title = o.getString("capsule_name"),
                        body = contents.firstOrNull { it.type == "text" }?.data ?: "",
                        tags = "",
                        mediaUri = contents.firstOrNull { it.type == "image" }?.data,
                        ddayMillis = isoFmt.parse(o.getString("open_at"))?.time,
                        condition = null,
                        isJoint = false,
                        latitude = null,
                        longitude = null,
                        isOpened = true,
                        contents = contents
                    )
                    opened.removeIf { it.capsuleId == cap.capsuleId }
                    opened += cap
                    onComplete(true, cap, null)
                } catch (e: Exception) {
                    onComplete(false, null, e.message)
                }
            },
            { err -> onComplete(false, null, err) }
        )
    }

    // ────────────────── ③ 생성(업로드) ─────────
    data class CapsuleCreateForm(
        val userId: Int,
        val capsuleName: String,
        val targetTime: String,
        val locationLat: Double?,
        val locationLng: Double?,
        val isLocation: Boolean,
        val isGroup: Boolean,
        val condition: String?,
        val contentText: String?,
        val files: List<Uri>
    )

    fun uploadCapsule(
        ctx: Context,
        form: CapsuleCreateForm,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val mp = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            addFormDataPart("user_id", form.userId.toString())
            addFormDataPart("capsule_name", form.capsuleName)
            addFormDataPart("target_time", form.targetTime)
            addFormDataPart("is_location", form.isLocation.toString())
            addFormDataPart("is_group", form.isGroup.toString())
            form.locationLat?.let { addFormDataPart("location_lat", it.toString()) }
            form.locationLng?.let { addFormDataPart("location_lng", it.toString()) }
            form.condition?.let { addFormDataPart("condition", it) }
            form.contentText?.let { addFormDataPart("content_text", it) }

            // 파일 파트
            form.files.forEachIndexed { idx, uri ->
                ctx.contentResolver.openInputStream(uri)?.use { s ->
                    val bytes = s.readBytes()
                    val req = bytes.toRequestBody("application/octet-stream".toMediaType())
                    addFormDataPart("files", "file$idx", req)
                }
            }
        }.build()

        ApiClient.postMultipart(
            endpoint = "capsule/",
            body = mp,
            onSuccess = { onComplete(true, null) },
            onFailure = { err -> onComplete(false, err) }
        )
    }

}