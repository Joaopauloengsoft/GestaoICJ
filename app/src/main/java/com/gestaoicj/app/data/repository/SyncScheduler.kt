package com.gestaoicj.app.data.repository

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gestaoicj.app.data.sync.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private companion object {
        const val TRABALHO_IMEDIATO = "sync-estoque"
        const val TRABALHO_PERIODICO = "sync-estoque-periodico"
    }

    private val regras = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun agendar() {
        val pedido = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(regras)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            TRABALHO_IMEDIATO,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            pedido
        )
    }

    fun iniciarPeriodico() {
        val pedido = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(regras)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            TRABALHO_PERIODICO,
            ExistingPeriodicWorkPolicy.KEEP,
            pedido
        )
    }
}
