package com.example.a1.repository

import okhttp3.MediaType.Companion.toMediaType

/*
import com.example.a1.capsule.Capsule
import com.example.a1.network.ApiClient
import org.json.JSONArray
import org.json.JSONObject

object CapsuleRepository {

    private const val UPLOAD_ENDPOINT = "/uploadData"
    private const val DOWNLOAD_ENDPOINT = "/downloadDataList"
    private const val DATA_TYPE = "capsule"

    /**
     * 캡슐 업로드
     */
    fun uploadCapsule(capsule: Capsule, uploaderId: String, onComplete: (Boolean) -> Unit) {
        val json = JSONObject().apply {
            put("uploaderId", uploaderId)
            put("dataType", DATA_TYPE)
            put("dataId", System.currentTimeMillis().toString())
            put("data", JSONObject().apply {
                put("title", capsule.title)
                put("body", capsule.body)
                put("tags", capsule.tags)
                put("mediaUri", capsule.mediaUri)
                put("ddayMillis", capsule.ddayMillis)
                put("condition", capsule.condition)
                put("isJoint", capsule.isJoint)
            }.toString())
        }

        ApiClient.post(UPLOAD_ENDPOINT, json) { success, _ ->
            onComplete(success)
        }
    }

    /**
     * 캡슐 전체 다운로드
     */
    fun getAllCapsules(uploaderId: String, onResult: (List<Capsule>) -> Unit) {
        val requestJson = JSONObject().apply {
            put("uploaderId", uploaderId)
            put("dataType", DATA_TYPE)
        }

        ApiClient.post(DOWNLOAD_ENDPOINT, requestJson) { success, response ->
            if (!success || response == null) {
                onResult(emptyList())
                return@post
            }

            val result = mutableListOf<Capsule>()
            try {
                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val data = JSONObject(item.getString("data"))

                    val capsule = Capsule(
                        title = data.getString("title"),
                        body = data.getString("body"),
                        tags = data.getString("tags"),
                        mediaUri = data.optString("mediaUri", null),
                        ddayMillis = if (data.has("ddayMillis") && !data.isNull("ddayMillis")) data.getLong("ddayMillis") else null,
                        condition = data.optString("condition", null),
                        isJoint = data.getBoolean("isJoint")
                    )
                    result.add(capsule)
                }
                onResult(result)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(emptyList())
            }
        }
    }
}*/
/**
 * 임시 저장소
 */
/*
import com.example.a1.capsule.Capsule

object CapsuleRepository {

    // 메모리 내 저장소 (임시)
    private val capsuleList = mutableListOf<Capsule>()

    /**
     * 캡슐 추가
     */
    fun addCapsule(capsule: Capsule) {
        capsuleList.add(capsule)
    }

    /**
     * 모든 캡슐 조회
     */
    fun getAllCapsules(): List<Capsule> {
        return capsuleList.toList()
    }

    /**
     * 저장소 초기화 (테스트용)
     */
    fun clearAll() {
        capsuleList.clear()
    }
}*/
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.a1.capsule.Capsule
import com.example.a1.capsule.CapsuleContent
import com.example.a1.network.ApiClient
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

    fun getAllCapsules(): List<Capsule> = opened + closed
    fun clearCache() { opened.clear(); closed.clear() }

    // ────────────────── 날짜 포맷 ───────────────
    private val isoFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.KOREA)

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
                    capsuleId  = o.getInt("capsule_id"),
                    title      = o.getString("capsule_name"),
                    body       = "",                 // 목록에는 본문 없음
                    tags       = "",
                    mediaUri   = null,
                    ddayMillis = isoFmt.parse(o.getString("open_at"))?.time,
                    condition  = null,
                    isJoint    = (o.getInt("type") == 1),
                    latitude   = o.optDouble("location_lat"),
                    longitude  = o.optDouble("location_lng"),
                    isOpened   = (o.getInt("type") == 0)
                )
            }
        }

        opened.clear(); closed.clear()

        ApiClient.get("capsule/opened/$userId",
            { body ->
                parse(body, opened)
                ApiClient.get("capsule/closed/$userId",
                    { body2 -> parse(body2, closed); onComplete(true, null) },
                    { err2  -> onComplete(false, err2) }
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
                    val contents  = mutableListOf<CapsuleContent>()
                    for (i in 0 until contentsJ.length()) {
                        val c = contentsJ.getJSONObject(i)
                        contents += CapsuleContent(
                            type = c.getString("content_type"),
                            data = c.getString("content_data")
                        )
                    }
                    val cap = Capsule(
                        capsuleId  = o.getInt("capsule_id"),
                        title      = o.getString("capsule_name"),
                        body       = contents.firstOrNull { it.type == "text" }?.data ?: "",
                        tags       = "",
                        mediaUri   = contents.firstOrNull { it.type == "image" }?.data,
                        ddayMillis = isoFmt.parse(o.getString("open_at"))?.time,
                        condition  = null,
                        isJoint    = false,
                        latitude   = null,
                        longitude  = null,
                        isOpened   = true,
                        contents   = contents
                    )
                    opened.removeIf { it.capsuleId == cap.capsuleId }
                    opened += cap
                    onComplete(true, cap, null)
                } catch (e: Exception) { onComplete(false, null, e.message) }
            },
            { err -> onComplete(false, null, err) }
        )
    }

    // ────────────────── ③ 생성(업로드) ─────────
    data class CapsuleCreateForm(
        val userId      : Int,
        val capsuleName : String,
        val targetTime  : String,          // "2025.01.01"
        val locationLat : Double?,
        val locationLng : Double?,
        val isLocation  : Boolean,
        val isGroup     : Boolean,
        val condition   : String?,
        val members     : List<Int>,
        val contentText : String?,
        val files       : List<Uri>
    )

    fun uploadCapsule(
        ctx: Context,
        form: CapsuleCreateForm,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val mp = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            addFormDataPart("user_id",        form.userId.toString())
            addFormDataPart("capsule_name",   form.capsuleName)
            addFormDataPart("target_time",    form.targetTime)
            addFormDataPart("is_location",    form.isLocation.toString())
            addFormDataPart("is_group",       form.isGroup.toString())
            form.locationLat?.let { addFormDataPart("location_lat", it.toString()) }
            form.locationLng?.let { addFormDataPart("location_lng", it.toString()) }
            form.condition  ?.let { addFormDataPart("condition", it) }
            form.contentText?.let{ addFormDataPart("content_text", it) }
            form.members.forEach { addFormDataPart("members", it.toString()) }

            /* 파일 파트들 */
            form.files.forEachIndexed { idx, uri ->
                ctx.contentResolver.openInputStream(uri)?.use { s ->
                    val bytes = s.readBytes()
                    val req   = bytes.toRequestBody("application/octet-stream".toMediaType())
                    addFormDataPart("files", "file$idx", req)
                }
            }
        }.build()

        ApiClient.postMultipart(
            endpoint  = "capsule/",          // FastAPI 라우트
            body      = mp,
            onSuccess = { onComplete(true,  null) },
            onFailure = { err -> onComplete(false, err) }
        )
    }
}