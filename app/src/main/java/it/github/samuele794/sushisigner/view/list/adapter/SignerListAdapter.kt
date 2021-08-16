package it.github.samuele794.sushisigner.view.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.github.samuele794.sushisigner.data.model.Signer
import it.github.samuele794.sushisigner.databinding.ItemSignerBinding

class SignerListAdapter :
    RecyclerView.Adapter<SignerListAdapter.SignerViewHolder>() {

    private val dataSet = mutableListOf<Signer>()

    class SignerViewHolder(val binding: ItemSignerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignerViewHolder {
        return SignerViewHolder(
            ItemSignerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SignerViewHolder, position: Int) {
        holder.binding.headerSignerTV.text = dataSet[position].title
        holder.binding.bodySignerTV.text = dataSet[position].body
    }

    override fun getItemCount(): Int = dataSet.size

    fun setData(list: List<Signer>) {
        dataSet.clear()
        dataSet.addAll(list)
        notifyDataSetChanged()
    }
}