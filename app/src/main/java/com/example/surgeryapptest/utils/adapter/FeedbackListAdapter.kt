package com.example.surgeryapptest.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.surgeryapptest.databinding.FeedbackListRowLayoutBinding
import com.example.surgeryapptest.databinding.PatientListRowLayoutBinding
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.AssignedPatientsList
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.PatientName
import com.example.surgeryapptest.model.network.doctorResponse.getFeedbackResponse.FeedbackList
import com.example.surgeryapptest.model.network.doctorResponse.getFeedbackResponse.WoundImageFeedback
import com.example.surgeryapptest.utils.diffUtils.CustomDiffUtils

class FeedbackListAdapter : RecyclerView.Adapter<FeedbackListAdapter.MyViewHolder>() {

    private var feedbackList = emptyList<FeedbackList>()

    class MyViewHolder(private val binding: FeedbackListRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(feedbackList: FeedbackList){
                binding.result = feedbackList
                binding.executePendingBindings()
            }

        companion object {
            fun from(parent: ViewGroup) : MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FeedbackListRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentFeedback = feedbackList[position]
        holder.bind(currentFeedback)
    }

    override fun getItemCount(): Int = feedbackList.size

    fun setData(newData : WoundImageFeedback){
        val diffUtils = CustomDiffUtils(feedbackList, newData.result)
        val diffUtilsResult = DiffUtil.calculateDiff(diffUtils)
        feedbackList = newData.result
        diffUtilsResult.dispatchUpdatesTo(this)
    }

}