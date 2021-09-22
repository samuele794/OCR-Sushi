package it.github.samuele794.sushisigner.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import it.github.samuele794.sushisigner.data.model.Signer

@Dao
interface SignerDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertSigner(signer: Signer)

    @Query("SELECT * from Signer")
    suspend fun getAll(): List<Signer>

    @Delete
    fun deleteSigner(item: Signer)

}