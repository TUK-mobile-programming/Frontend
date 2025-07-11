package com.example.a1.repository
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
}