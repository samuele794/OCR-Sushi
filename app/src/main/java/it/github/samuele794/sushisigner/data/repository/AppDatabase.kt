package it.github.samuele794.sushisigner.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import it.github.samuele794.sushisigner.data.dao.SignerDao
import it.github.samuele794.sushisigner.data.model.Signer

@Database(entities = [Signer::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun signerDao(): SignerDao
}