package com.example.surgeryapptest.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.surgeryapptest.databinding.ProgressEntryRowLayoutBinding
import com.example.surgeryapptest.model.network.AllProgressBookEntry
import com.example.surgeryapptest.model.network.AllProgressBookEntryItem
import com.example.surgeryapptest.utils.diffUtils.CustomDiffUtils

class Adapter : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    // Need to create a base adapter to cater all]
    // network response into Rv
    // Follow Jimmy's Base adapter method
    private var progressEntryItems = emptyList<AllProgressBookEntryItem>()

    class MyViewHolder(private val binding: ProgressEntryRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(allProgressBookEntryItem: AllProgressBookEntryItem){
                binding.result = allProgressBookEntryItem
                binding.executePendingBindings()
            }

        companion object {
            fun from(parent: ViewGroup) : MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ProgressEntryRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: Adapter.MyViewHolder, position: Int) {
        val currentProgressEntryItem = progressEntryItems[position]
        holder.bind(currentProgressEntryItem)
    }

    override fun getItemCount(): Int = progressEntryItems.size

    fun setData(newData : AllProgressBookEntry){
        val diffUtils = CustomDiffUtils(progressEntryItems,newData.result)
        val diffUtilsResult = DiffUtil.calculateDiff(diffUtils)
        progressEntryItems = newData.result
        diffUtilsResult.dispatchUpdatesTo(this)
    }

}