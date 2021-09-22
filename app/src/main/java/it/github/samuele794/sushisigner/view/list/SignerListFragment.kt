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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import it.github.samuele794.sushisigner.databinding.FragmentSignerListBinding
import it.github.samuele794.sushisigner.utils.viewBinding
import it.github.samuele794.sushisigner.view.list.adapter.SignerListAdapter
import it.github.samuele794.sushisigner.viewmodel.view.list.SignerListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

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

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onMoved(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                fromPos: Int,
                target: RecyclerView.ViewHolder,
                toPos: Int,
                x: Int,
                y: Int
            ) {

                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
//                val fromPosition = viewHolder.adapterPosition
//                val toPosition = target.adapterPosition
                Collections.swap(signerAdapter.dataSet, toPos, fromPos)
                signerAdapter.notifyItemChanged(fromPos)
                signerAdapter.notifyItemChanged(toPos)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                viewModel.removeSigner(signerAdapter.dataSet[viewHolder.adapterPosition])
                signerAdapter.removeFromIndex(viewHolder.adapterPosition)
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
            }
        }).attachToRecyclerView(viewBinding.signerRV)

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