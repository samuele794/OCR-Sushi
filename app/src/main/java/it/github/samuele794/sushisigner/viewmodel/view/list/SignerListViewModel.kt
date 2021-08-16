package it.github.samuele794.sushisigner.viewmodel.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.github.samuele794.sushisigner.data.model.Signer
import it.github.samuele794.sushisigner.data.repository.SignerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignerListViewModel(private val signerRepository: SignerRepository) : ViewModel() {

    private val mSignerFlow = MutableStateFlow(listOf<Signer>())
    val signerFlow: StateFlow<List<Signer>> = mSignerFlow

    init {
        updateSignerData()
    }

    fun updateSignerData() {
        viewModelScope.launch(Dispatchers.IO) {
            mSignerFlow.value = signerRepository.getAllSigner()
        }
    }
}