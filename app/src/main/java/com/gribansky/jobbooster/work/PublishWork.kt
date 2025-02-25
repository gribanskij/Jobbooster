package com.gribansky.jobbooster.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gribansky.jobbooster.datastore.PrefManager
import com.gribansky.jobbooster.net.HhApiImpl

class PublishWork(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    private val api = HhApiImpl(PrefManager(appContext))

    override suspend fun doWork(): Result {
        api.boostResume()
        return Result.success()
    }
}