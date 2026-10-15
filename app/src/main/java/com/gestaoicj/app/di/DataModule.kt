package com.gestaoicj.app.di

import android.content.Context
import androidx.room.Room
import com.gestaoicj.app.data.local.AppDatabase
import com.gestaoicj.app.data.local.dao.MovimentoDao
import com.gestaoicj.app.data.local.dao.ProdutoDao
import com.gestaoicj.app.data.local.dao.UsuarioDao
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides @Singleton
    fun provideAuth(): FirebaseAuth = Firebase.auth

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "estoque.db")
            .fallbackToDestructiveMigration(true)   // SÓ EM DESENVOLVIMENTO: remover antes de publicar
            .build()

    @Provides fun provideUsuarioDao(db: AppDatabase): UsuarioDao = db.usuarioDao()
    @Provides fun provideProdutoDao(db: AppDatabase): ProdutoDao = db.produtoDao()
    @Provides fun provideMovimentoDao(db: AppDatabase): MovimentoDao = db.movimentoDao()
}