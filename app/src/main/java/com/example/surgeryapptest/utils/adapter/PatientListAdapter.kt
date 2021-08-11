package com.example.surgeryapptest.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.surgeryapptest.databinding.PatientListRowLayoutBinding
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.AssignedPatientsList
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.PatientName
import com.example.surgeryapptest.utils.diffUtils.CustomDiffUtils

class PatientListAdapter : RecyclerView.Adapter<PatientListAdapter.MyViewHolder>() {

    private var patientNameList = emptyList<PatientName>()

    class MyViewHolder(private val binding: PatientListRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(patientNameList: PatientName){
                binding.result = patientNameList
                binding.executePendingBindings()
            }

        companion object {
            fun from(parent: ViewGroup) : MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PatientListRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientListAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PatientListAdapter.MyViewHolder, position: Int) {
        val currentPatientName = patientNameList[position]
        holder.bind(currentPatientName)
    }

    override fun getItemCount(): Int = patientNameList.size

    fun setData(newData : AssignedPatientsList){
        val diffUtils = CustomDiffUtils(patientNameList,newData.result!!)
        val diffUtilsResult = DiffUtil.calculateDiff(diffUtils)
        patientNameList = newData.result
        diffUtilsResult.dispatchUpdatesTo(this)
    }

}