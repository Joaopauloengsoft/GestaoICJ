package com.gestaoicj.app.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gestaoicj.app.data.repository.SyncRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val auth: FirebaseAuth,
    private val sync: SyncRepository
) : CoroutineWorker(context, params) {

    private companion object {
        const val TAG = "SyncWorker"
        const val TIMEOUT_MS = 5 * 60 * 1000L
        const val MAX_TENTATIVAS = 6
    }

    override suspend fun doWork(): Result {
        val uid = auth.currentUser?.uid ?: return Result.success()

        return try {
            withTimeout(TIMEOUT_MS) { sync.enviarPendentes(uid) }
            Log.d(TAG, "Sincronização concluída")
            Result.success()
        } catch (e: TimeoutCancellationException) {
            tentarDeNovo()
        } catch (e: CancellationException) {
            throw e
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                Code.PERMISSION_DENIED,
                Code.INVALID_ARGUMENT,
                Code.UNAUTHENTICATED -> {
                    Log.e(TAG, "Erro permanente: ${e.code}", e)
                    Result.failure()
                }
                else -> tentarDeNovo()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Falha ao sincronizar", e)
            tentarDeNovo()
        }
    }

    private fun tentarDeNovo(): Result =
        if (runAttemptCount >= MAX_TENTATIVAS) Result.failure() else Result.retry()
}
