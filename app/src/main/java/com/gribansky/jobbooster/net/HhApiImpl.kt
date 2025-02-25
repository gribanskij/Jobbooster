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

    private val code = "O82N1HFF021CSGEOS92UM2IOM7KC19CD0A2USSSRLKHO79UQBASHOMEVPL2RPPTI"
    private val accessToken = "USERN0V37KARPRCPPBP55HIISSLP7HE4HRI5DB24DUP6JU2KBQJJ2570AAF7NK05"
    private val refreshToken = "USERHEONKM1DNK461AQ03MILIC0RBEPPD87D8CICM6K6VFAR526G5N3E9CVF2I0A"
    private val resumeId = "6c88eac4ff05db84f90039ed1f3678704d4a6e"


    private val httpClient by lazy { OkHttpClient.Builder().retryOnConnectionFailure(true).build() }

    private val timeFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault())


    override suspend fun boostResume() = withContext(Dispatchers.IO) {
            try {

                val res = when (val result = publishResume()){
                    is Answer.Success -> { result }
                    is Answer.Error -> {
                        if (result.statusCode == 403){
                            updateToken()
                            publishResume()
                        } else result
                    }
                }

                savePublishResult(res)

            } catch (ex: Exception) {
                savePublishResult (handleException(ex))
            }
        }



    private suspend fun updateToken(): Answer {

        return try {
            val request = getUpdateTokenRequest()
            val ans = httpClient.newCall(request).await()

            if (ans.isSuccessful) {
                saveTokens(ans.body.toString())
                Answer.Success("")
            } else {
                Answer.Error(statusCode = ans.code, ans.body.toString())
            }

        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    private fun getUpdateTokenRequest(): Request {

        val url =
            "$baseHhUrl/token?grant_type=refresh_token&refresh_token=${prefManager.refreshToken}"
        return Request.Builder()
            .url(url)
            .addHeader("HH-User-Agent", "${prefManager.appName}/1.0 (${prefManager.email})")
            .post("".toRequestBody())
            .build()

    }


    private suspend fun publishResume():Answer{

        return try {

            val request = getResumeRequest()
            val ans = httpClient.newCall(request).await()

            if (ans.code == 204) Answer.Success("")
            else Answer.Error(statusCode = ans.code, ans.body!!.string())

        } catch (ex:Exception){
            handleException(ex)
        }

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

    private fun getAccessToken() {
        val url =
            "$baseHhUrl/token?grant_type=authorization_code&client_id=${prefManager.clientId}&client_secret=${prefManager.clientSecret}&code=${prefManager.clientSecret}"

        val f =
            "https://api.hh.ru/oauth/authorize?response_type=code&client_id=I4ORK7CEI8JVKQB7HPA6DVGCOS8HALCVOO1EBBC41CIL0AHLQIT5HSC2O3E2SBJI"

        val fff =
            "https://api.hh.ru/token?grant_type=authorization_code&client_id=I4ORK7CEI8JVKQB7HPA6DVGCOS8HALCVOO1EBBC41CIL0AHLQIT5HSC2O3E2SBJI&client_secret=T20GPRAP948TJ0DCFP9TO3DPBGSNGTB76O6ESU5235550B936BBRVPICRTC23K2B&code=O82N1HFF021CSGEOS92UM2IOM7KC19CD0A2USSSRLKHO79UQBASHOMEVPL2RPPTI"


        val test =
            "https://api.hh.ru/token?grant_type=client_credentials&client_id=I4ORK7CEI8JVKQB7HPA6DVGCOS8HALCVOO1EBBC41CIL0AHLQIT5HSC2O3E2SBJI&client_secret=T20GPRAP948TJ0DCFP9TO3DPBGSNGTB76O6ESU5235550B936BBRVPICRTC23K2B&code=29CtxMcaA8pRFDYyC8e8Gkm4"

    }

    private fun handleException(ex: Exception): Answer {
        return Answer.Error(-1, ex.message?:ex.toString())
    }


    private fun saveTokens(json: String) {
        if (json.isNullOrEmpty()) throw Exception("JSON is null or empty")
        val obj = JSONObject(json)
        prefManager.accessToken = obj.getString("access_token")
        prefManager.refreshToken = obj.getString("refresh_token")
    }

    private fun savePublishResult(result: Answer){

        when (result){

            is Answer.Error -> {
                prefManager.errorDesc = "${timeFormat.format(Date())}: ${result.statusCode}:${result.message}"
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