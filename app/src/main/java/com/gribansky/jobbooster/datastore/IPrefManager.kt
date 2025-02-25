package com.gribansky.jobbooster.datastore

interface IPrefManager {

    var accessToken:String

    var refreshToken:String

    val clientId:String

    val clientSecret:String

    val email:String

    val appName:String

    val resumeId:String

    var boostResult:String

    var errorDesc:String

}