package com.example.surgeryapptest.ui.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.ActivityChartingBinding
import com.example.surgeryapptest.databinding.ActivityLoginBinding
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.PatientName
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.formatter.PercentFormatter


class ChartingActivity : AppCompatActivity() {

    private val args by navArgs<ChartingActivityArgs>()

    private lateinit var binding: ActivityChartingBinding

    private val names = mutableListOf<String>()
    private val patient_ID_barChart = mutableListOf<Int>()
    private val noOfImages = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val chartData = args.patientListData.result
        println(chartData)

        extractChartData(chartData)

        binding.loadPieChart.setOnClickListener {
            binding.chartTv.visibility = View.GONE
            binding.pieChart.visibility = View.VISIBLE
            binding.barChart.visibility = View.GONE
            setupPieChart()
            loadPieChart(names, noOfImages)
        }

        binding.loadBarChart.setOnClickListener {
            binding.chartTv.visibility = View.GONE
            binding.pieChart.visibility = View.GONE
            loadBarChart(patient_ID_barChart, noOfImages)
        }

    }

    private fun extractChartData(chartData: ArrayList<PatientName>) {
        for (i in chartData) {
            names.add(i.patientId.split(":")[1])
            patient_ID_barChart.add((i.patientId.split(":")[0]).toInt())
            noOfImages.add(i.woundImages.size)
        }
    }

    private fun loadBarChart(names: MutableList<Int>, images: MutableList<Int>) {

        binding.barChart.visibility = View.VISIBLE

        val entries = arrayListOf<BarEntry>()
        for (i in 0 until names.size) {
            entries.add(BarEntry(names[i].toFloat(), images[i].toFloat()))
        }

        val colors = arrayListOf<Int>()
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }
        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }

        val dataSet = BarDataSet(entries, "Patients Summary")
        dataSet.colors = colors

        val data = BarData(dataSet)

        data.apply {
            //setValueFormatter(PercentFormatter(binding.pieChart))
            setValueTextSize(15F)
            setValueTextColor(Color.BLACK)
        }

        binding.barChart.apply {
            setFitBars(true)
            setData(data)
            description.text = "EXAMPLE"
            invalidate()
        }

        binding.barChart.animateY(1400, Easing.EaseInOutCirc)

    }

    private fun setupPieChart() {

        binding.pieChart.apply {

            isDrawHoleEnabled = true
            setUsePercentValues(true)

            setEntryLabelTextSize(15F)
            setEntryLabelColor(Color.BLACK)
            centerText = "Patients' Uploaded Entries Summary"
            setCenterTextSize(20F)
            description.isEnabled = false

            val l: Legend = this.legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            l.orientation = Legend.LegendOrientation.VERTICAL
            l.setDrawInside(false)
            l.textSize = 15F
            l.isEnabled = true

        }
    }

    private fun loadPieChart(names: MutableList<String>, images: MutableList<Int>) {

        val entries = arrayListOf<PieEntry>()

        for (i in 0 until names.size) {
            entries.add(PieEntry(images[i].toFloat(), names[i]))
        }

        println(entries)

        val colors = arrayListOf<Int>()
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }
        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }

        val dataSet = PieDataSet(entries, "Patients Summary")
        dataSet.colors = colors

        val data = PieData(dataSet)

        data.apply {
            setDrawValues(true)
            setValueFormatter(PercentFormatter(binding.pieChart))
            setValueTextSize(15F)
            setValueTextColor(Color.BLACK)
        }

        binding.pieChart.apply {
            setData(data)
            invalidate()
        }

        binding.pieChart.animateY(1400, Easing.EaseInOutQuad)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true
        //return super.onSupportNavigateUp()
    }
}

