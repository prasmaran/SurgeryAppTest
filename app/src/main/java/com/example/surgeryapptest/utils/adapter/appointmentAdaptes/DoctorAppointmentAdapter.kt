package com.example.surgeryapptest.utils.adapter.appointmentAdaptes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.surgeryapptest.databinding.AppointmentDoctorRowLayoutBinding
import com.example.surgeryapptest.databinding.AppointmentRowLayoutBinding
import com.example.surgeryapptest.model.network.appointmentResponse.AppointmentNetworkResponse
import com.example.surgeryapptest.model.network.appointmentResponse.Result
import com.example.surgeryapptest.utils.diffUtils.CustomDiffUtils

class DoctorAppointmentAdapter : RecyclerView.Adapter<DoctorAppointmentAdapter.MyViewHolder>() {

    private var doctorAppointmentList = emptyList<Result>()

    class MyViewHolder(private val binding: AppointmentDoctorRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(patientAppointmentList: Result){
                binding.appointment = patientAppointmentList
                binding.executePendingBindings()
            }

        companion object {
            fun from(parent: ViewGroup) : MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AppointmentDoctorRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentAppointment = doctorAppointmentList[position]
        holder.bind(currentAppointment)
    }

    override fun getItemCount(): Int = doctorAppointmentList.size

    fun setData(newData : AppointmentNetworkResponse){
        val diffUtils = CustomDiffUtils(doctorAppointmentList, newData.result)
        val diffUtilsResult = DiffUtil.calculateDiff(diffUtils)
        doctorAppointmentList = newData.result
        diffUtilsResult.dispatchUpdatesTo(this)
    }
}