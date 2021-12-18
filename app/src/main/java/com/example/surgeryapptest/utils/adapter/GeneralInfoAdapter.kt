package com.example.surgeryapptest.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.surgeryapptest.databinding.GeneralInfoRowLayoutBinding
import com.example.surgeryapptest.databinding.ProgressEntryRowLayoutBinding
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntry
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.model.network.utilsResponse.GeneralInfoList
import com.example.surgeryapptest.model.network.utilsResponse.GeneralInfoResponse
import com.example.surgeryapptest.utils.diffUtils.CustomDiffUtils

class GeneralInfoAdapter : RecyclerView.Adapter<GeneralInfoAdapter.MyViewHolder>() {

    private var generalInfoItem = emptyList<GeneralInfoList>()

    class MyViewHolder(private val binding: GeneralInfoRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(allGeneralInfoItem: GeneralInfoList){
                binding.generalResult = allGeneralInfoItem
                binding.executePendingBindings()
            }

        companion object {
            fun from(parent: ViewGroup) : MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GeneralInfoRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralInfoAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GeneralInfoAdapter.MyViewHolder, position: Int) {
        val currentInfoItem = generalInfoItem[position]
        holder.bind(currentInfoItem)
    }

    override fun getItemCount(): Int = generalInfoItem.size

    fun setData(newData : GeneralInfoResponse){
        val diffUtils = CustomDiffUtils(generalInfoItem,newData.result!!)
        val diffUtilsResult = DiffUtil.calculateDiff(diffUtils)
        generalInfoItem = newData.result
        diffUtilsResult.dispatchUpdatesTo(this)
    }

}