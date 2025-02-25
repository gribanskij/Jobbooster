package com.gribansky.jobbooster.datastore

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.gribansky.jobbooster.BuildConfig


class PrefManager(context: Context) : IPrefManager {


    private val accessTokenKey = "accessTokenKey"
    private val refreshTokenKey = "refreshTokenKey"
    private val boostResultKey = "boostResultKey"
    private val errorDescKey = "errorDescKey"

    private val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)


    override var accessToken: String
        get() = pref.getString(accessTokenKey, BuildConfig.accessToken)!!
        set(value) {
            val editor = pref.edit()
            editor.putString(accessTokenKey, value)
            editor.apply()
        }
    override var refreshToken: String
        get() = pref.getString(refreshTokenKey, BuildConfig.refreshToken)!!
        set(value) {
            val editor = pref.edit()
            editor.putString(refreshTokenKey, value)
            editor.apply()
        }
    override val clientId: String
        get() = BuildConfig.clientId

    override val clientSecret: String
        get() = BuildConfig.clientSecret

    override val email: String
        get() = BuildConfig.email

    override val appName: String
        get() = BuildConfig.appName

    override val resumeId: String
        get() = BuildConfig.resumeId

    override var boostResult: String
        get() = pref.getString(boostResultKey, "")!!
        set(value) {
            val editor = pref.edit()
            editor.putString(boostResultKey, value)
            editor.apply()
        }
    override var errorDesc: String
        get() = pref.getString(errorDescKey, "")!!
        set(value) {
            val editor = pref.edit()
            editor.putString(errorDescKey, value)
            editor.apply()
        }
}