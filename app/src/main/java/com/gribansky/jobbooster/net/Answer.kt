package com.gribansky.jobbooster.net

sealed interface Answer {
    data class Success(val data: String) : Answer
    data class Error(val statusCode: Int, val message: String, val data: String? = null) : Answer
}
