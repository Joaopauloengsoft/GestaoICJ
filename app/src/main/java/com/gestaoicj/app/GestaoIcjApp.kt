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

    // Ensina o WorkManager a criar workers com o Hilt
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()        // o Hilt injeta os campos acima aqui
        if (auth.currentUser != null) {
            scheduler.agendar()           // envia o que ficou pendente
            scheduler.iniciarPeriodico()  // rede de segurança a cada 6 h
        }
    }
}