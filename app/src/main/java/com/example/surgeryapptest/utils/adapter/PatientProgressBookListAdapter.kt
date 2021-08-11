package com.example.surgeryapptest.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.surgeryapptest.databinding.DoctorPatientProgressEntryRowLayoutBinding
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.PatientName
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.WoundImage
import com.example.surgeryapptest.utils.diffUtils.CustomDiffUtils

class PatientProgressBookListAdapter : RecyclerView.Adapter<PatientProgressBookListAdapter.MyViewHolder>() {

    private var patientProgressBookItems = emptyList<WoundImage>()

    class MyViewHolder(private val binding: DoctorPatientProgressEntryRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(woundImage: WoundImage){
                binding.result = woundImage
                binding.executePendingBindings()
            }

        companion object {
            fun from(parent: ViewGroup) : MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DoctorPatientProgressEntryRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientProgressBookListAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PatientProgressBookListAdapter.MyViewHolder, position: Int) {
        val currentPatientProgressBookItem = patientProgressBookItems[position]
        holder.bind(currentPatientProgressBookItem)
    }

    override fun getItemCount(): Int = patientProgressBookItems.size

    fun setData(newData : PatientName){
        val diffUtils = CustomDiffUtils(patientProgressBookItems,newData.woundImages)
        val diffUtilsResult = DiffUtil.calculateDiff(diffUtils)
        patientProgressBookItems = newData.woundImages
        diffUtilsResult.dispatchUpdatesTo(this)
    }

}