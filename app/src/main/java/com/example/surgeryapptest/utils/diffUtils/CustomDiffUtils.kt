package com.example.surgeryapptest.utils.diffUtils

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class CustomDiffUtils constructor(private val mOldList: List<Any>, private val mNewList: List<Any>):
    DiffUtil.Callback() {

    fun updateList(adapter: RecyclerView.Adapter<*>) {
        val diffResult = DiffUtil.calculateDiff(this)
        diffResult.dispatchUpdatesTo(adapter)
    }

    override fun getOldListSize(): Int {
        return mOldList.size
    }

    override fun getNewListSize(): Int {
        return mNewList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldList[oldItemPosition] === mNewList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldList[oldItemPosition] == mNewList[newItemPosition]
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}