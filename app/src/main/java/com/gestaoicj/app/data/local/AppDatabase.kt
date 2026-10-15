package com.gestaoicj.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gestaoicj.app.data.local.dao.MovimentoDao
import com.gestaoicj.app.data.local.dao.ProdutoDao
import com.gestaoicj.app.data.local.dao.UsuarioDao
import com.gestaoicj.app.data.local.entity.MovimentoEntity
import com.gestaoicj.app.data.local.entity.ProdutoEntity
import com.gestaoicj.app.data.local.entity.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        ProdutoEntity::class,
        MovimentoEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun produtoDao(): ProdutoDao
    abstract fun movimentoDao(): MovimentoDao
}