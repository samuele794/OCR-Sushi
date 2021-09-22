package it.github.samuele794.sushisigner.data.repository

import it.github.samuele794.sushisigner.data.model.Signer

interface SignerRepository {
    suspend fun saveSigner(signer: Signer)

    suspend fun getAllSigner(): List<Signer>
    fun deleteSigner(item: Signer)
}

class SignerRepositoryImpl(appDB: AppDatabase) : SignerRepository {
    private val signerDao = appDB.signerDao()

    override suspend fun saveSigner(signer: Signer) {
        signerDao.insertSigner(signer)
    }

    override suspend fun getAllSigner() = signerDao.getAll()

    override fun deleteSigner(item: Signer) {
        signerDao.deleteSigner(item)
    }
}