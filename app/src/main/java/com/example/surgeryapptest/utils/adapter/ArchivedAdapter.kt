package com.example.surgeryapptest.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.surgeryapptest.databinding.ProgressEntryArchivedRowLayoutBinding
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntry
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.utils.adapter.ArchivedAdapter.MyViewHolder.Companion.from
import com.example.surgeryapptest.utils.diffUtils.CustomDiffUtils

class ArchivedAdapter(private val clickListener: (ArrayList<String>) -> Unit) :
    RecyclerView.Adapter<ArchivedAdapter.MyViewHolder>() {

    // Need to create a base adapter to cater all]
    // network response into Rv
    // Follow Jimmy's Base adapter method
    private var progressEntryArchivedItems = emptyList<AllProgressBookEntryItem>()

    // removed private val
    class MyViewHolder(val binding: ProgressEntryArchivedRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(allProgressBookEntryArchivedItem: AllProgressBookEntryItem) {
            binding.result = allProgressBookEntryArchivedItem
            binding.executePendingBindings()
        }

//        init {
//
//            binding.progressEntryArchivedRowLayout.setOnClickListener {
//                clickPosition(bindingAdapterPosition)
//                //clickListener(progressEntryArchivedItems[bindingAdapterPosition].progressTitle)
//            }
//
//        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ProgressEntryArchivedRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

//        vh.binding.progressEntryArchivedRowLayout.setOnClickListener {
//            clickListener(progressEntryArchivedItems[adapter].progressTitle)
//        }

        return from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentProgressEntryItem = progressEntryArchivedItems[position]
        holder.bind(currentProgressEntryItem)

        // Not a good practice but
        // this will do for now
        holder.binding.progressEntryArchivedRowLayout.setOnClickListener {
            val arrayList = arrayListOf<String>()
            val pos = progressEntryArchivedItems.indexOf(currentProgressEntryItem)

            arrayList.add(progressEntryArchivedItems[position].entryID.toString())
            arrayList.add(progressEntryArchivedItems[position].progressTitle)
            arrayList.add(pos.toString())

            clickListener(arrayList)
        }

    }

    override fun getItemCount(): Int = progressEntryArchivedItems.size

    fun setData(newData: AllProgressBookEntry) {
        val diffUtils = CustomDiffUtils(progressEntryArchivedItems, newData.result!!)
        val diffUtilsResult = DiffUtil.calculateDiff(diffUtils)
        progressEntryArchivedItems = newData.result
        diffUtilsResult.dispatchUpdatesTo(this)
    }

}
