package com.gestaoicj.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.gestaoicj.app.data.repository.SyncScheduler
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GestaoIcjApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var scheduler: SyncScheduler
    @Inject lateinit var auth: FirebaseAuth

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        if (auth.currentUser != null) {
            scheduler.agendar()
            scheduler.iniciarPeriodico()
        }
    }
}
