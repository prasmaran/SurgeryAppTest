package com.example.surgeryapptest.utils.adapter.appointmentAdaptes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.surgeryapptest.databinding.AppointmentRowLayoutBinding
import com.example.surgeryapptest.model.network.appointmentResponse.AppointmentNetworkResponse
import com.example.surgeryapptest.model.network.appointmentResponse.Result
import com.example.surgeryapptest.utils.diffUtils.CustomDiffUtils

class PatientAppointmentAdapter : RecyclerView.Adapter<PatientAppointmentAdapter.MyViewHolder>() {

    private var patientAppointmentList = emptyList<Result>()

    class MyViewHolder(private val binding: AppointmentRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(patientAppointmentList: Result){
                binding.appointment = patientAppointmentList
                binding.executePendingBindings()
            }

        companion object {
            fun from(parent: ViewGroup) : MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AppointmentRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientAppointmentAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PatientAppointmentAdapter.MyViewHolder, position: Int) {
        val currentAppointment = patientAppointmentList[position]
        holder.bind(currentAppointment)
    }

    override fun getItemCount(): Int = patientAppointmentList.size

    fun setData(newData : AppointmentNetworkResponse){
        val diffUtils = CustomDiffUtils(patientAppointmentList, newData.result)
        val diffUtilsResult = DiffUtil.calculateDiff(diffUtils)
        patientAppointmentList = newData.result
        diffUtilsResult.dispatchUpdatesTo(this)
    }


//    fun setData(newData : List<Result>){
//        val diffUtils = CustomDiffUtils(patientAppointmentList, newData)
//        val diffUtilsResult = DiffUtil.calculateDiff(diffUtils)
//        patientAppointmentList = newData
//        diffUtilsResult.dispatchUpdatesTo(this)
//    }

}