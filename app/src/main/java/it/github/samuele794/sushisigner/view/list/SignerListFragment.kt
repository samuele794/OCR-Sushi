package it.github.samuele794.sushisigner.view.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import it.github.samuele794.sushisigner.databinding.FragmentSignerListBinding
import it.github.samuele794.sushisigner.utils.viewBinding
import it.github.samuele794.sushisigner.view.list.adapter.SignerListAdapter
import it.github.samuele794.sushisigner.viewmodel.view.list.SignerListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignerListFragment : Fragment() {

    private val viewBinding by viewBinding(FragmentSignerListBinding::inflate)

    private val viewModel by viewModel<SignerListViewModel>()
    private lateinit var signerAdapter: SignerListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signerAdapter = SignerListAdapter()
        viewBinding.signerRV.adapter = signerAdapter

        viewBinding.addSignerFAB.setOnClickListener {
            findNavController()
                .navigate(
                    SignerListFragmentDirections
                        .actionSignerListFragmentToCameraAcquisitionFragment()
                )
        }

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateSignerData()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signerFlow
                    .collect {
                        signerAdapter.setData(it)
                    }
            }
        }
    }
}