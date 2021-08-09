package it.github.samuele794.sushisigner.data.repository

import it.github.samuele794.sushisigner.data.model.Signer

interface SignerRepository {
    suspend fun saveSigner(signer: Signer)

}

class SignerRepositoryImpl(appDB: AppDatabase) : SignerRepository {
    private val signerDao = appDB.signerDao()

    override suspend fun saveSigner(signer: Signer) {
        signerDao.insertSigner(signer)
    }
}