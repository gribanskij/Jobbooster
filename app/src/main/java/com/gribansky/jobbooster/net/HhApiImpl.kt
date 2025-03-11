package com.gribansky.jobbooster.net

import com.gribansky.jobbooster.datastore.IPrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class HhApiImpl(private val prefManager: IPrefManager) : IhhApi {

    private val baseHhUrl = "https://api.hh.ru"

    private val httpClient by lazy { OkHttpClient.Builder().retryOnConnectionFailure(true).build() }

    private val timeFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault())


    override suspend fun boostResume() = withContext(Dispatchers.IO) {
        try {

            val res = when (val result = publishResume()) {
                is Answer.Success -> {
                    result
                }

                is Answer.Error -> {
                    if (result.statusCode == 403) {
                        updateToken()
                        publishResume()
                    } else result
                }
            }

            savePublishResult(res)

        } catch (ex: Exception) {
            savePublishResult(handleException(ex))
        }
    }


    private suspend fun updateToken(): Answer {

        val request = getUpdateTokenRequest()
        val ans = httpClient.newCall(request).await()

        return if (ans.isSuccessful) {
            saveTokens(ans.body?.string()?:"")
            Answer.Success("")
        } else {
            Answer.Error(statusCode = ans.code, ans.body?.string()?:"????")
        }

    }

    private fun getUpdateTokenRequest(): Request {

        val url =
            "$baseHhUrl/token?grant_type=refresh_token&refresh_token=${prefManager.refreshToken}"
        return Request.Builder()
            .url(url)
            .addHeader("User-Agent", "${prefManager.appName}/1.0 (${prefManager.email})")
            .post("".toRequestBody())
            .build()

    }


    private suspend fun publishResume(): Answer {

        val request = getResumeRequest()
        val ans = httpClient.newCall(request).await()

        return if (ans.code == 204) Answer.Success("")
        else Answer.Error(statusCode = ans.code, ans.body!!.string())

    }


    private fun getResumeRequest(): Request {

        val url = "$baseHhUrl/resumes/${prefManager.resumeId}/publish"

        return Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${prefManager.accessToken}")
            .addHeader("User-Agent", "${prefManager.appName}/1.0 (${prefManager.email})")
            .post("".toRequestBody())
            .build()

    }


    private fun handleException(ex: Exception): Answer {
        return Answer.Error(-1, ex.message ?: ex.toString())
    }


    private fun saveTokens(json: String) {
        if (json.isNullOrEmpty()) throw Exception("JSON is null or empty")
        val obj = JSONObject(json)
        prefManager.accessToken = obj.getString("access_token")
        prefManager.refreshToken = obj.getString("refresh_token")
    }

    private fun savePublishResult(result: Answer) {

        when (result) {

            is Answer.Error -> {
                prefManager.errorDesc =
                    "${timeFormat.format(Date())}: ${result.statusCode}:${result.message}"
            }

            is Answer.Success -> {
                prefManager.boostResult = "Успешно обновлено: ${timeFormat.format(Date())}"
            }
        }
    }

    private suspend fun Call.await() = suspendCancellableCoroutine { continuation ->

        continuation.invokeOnCancellation {
            cancel()
        }
        enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }
        })
    }

}