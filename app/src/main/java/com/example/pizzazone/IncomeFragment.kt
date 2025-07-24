package com.example.pizzazone

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.example.pizzazone.databinding.FragmentIncomeBinding
import java.util.ArrayList

// Firebase Imports
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class IncomeFragment : Fragment() {

    // View Binding for the fragment layout
    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!

    // Firebase Database reference
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using View Binding
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        // Setup both charts after the view is inflated
        setupPieChart()
        setupBarChart()

        // --- Fetch and display total customers ---
        fetchTotalCustomers()

        // Handle back arrow click
        binding.backArrowIncome.setOnClickListener {
            // Handle back button click, e.g., navigate back
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return view
    }

    // --- Fetch Total Customers from Firebase ---
    private fun fetchTotalCustomers() {
        // Reference to the "Customers" node in your Firebase Realtime Database
        val customersRef = database.getReference("Customers")

        customersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the count of children under the "Customers" node
                val totalCustomers = snapshot.childrenCount

                // Update the TextView with the total customer count
                binding.totalCustomersValue.text = totalCustomers.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that occur during data retrieval
                // You might want to log the error or show a message to the user
                binding.totalCustomersValue.text = "Error" // Or handle more gracefully
                // Log.e("IncomeFragment", "Failed to load customers: ${error.message}")
            }
        })
    }

    // --- Pie Chart Setup ---
    private fun setupPieChart() {
        val pieChart = binding.pieChart // Access the PieChart via binding

        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.setDragDecelerationFrictionCoef(0.95f)
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f
        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        // Dummy data for Pie Chart. Replace with real data from Firebase as needed.
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(70f, "Solar panel"))
        entries.add(PieEntry(20f, "Lithium Battery"))
        entries.add(PieEntry(10f, "Inverters"))

        val dataSet = PieDataSet(entries, "Category Sales")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors: ArrayList<Int> = ArrayList()
        // Make sure these colors match your XML color definitions for the legend
        colors.add(Color.parseColor("#FFC107")) // Solar panel color
        colors.add(Color.parseColor("#2196F3")) // Lithium Battery color
        colors.add(Color.parseColor("#4CAF50")) // Inverters color

        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data

        pieChart.highlightValues(null)
        pieChart.invalidate()
    }

    // --- Bar Chart Setup ---
    private fun setupBarChart() {
        val barChart = binding.monthlyBarChart // Access the BarChart via binding

        // Dummy data for Bar Chart. Replace with real data from Firebase as needed.
        val barEntriesList: ArrayList<BarEntry> = ArrayList()
        barEntriesList.add(BarEntry(1f, 100f)) // Jan
        barEntriesList.add(BarEntry(2f, 250f)) // Feb
        barEntriesList.add(BarEntry(3f, 150f)) // Mar
        barEntriesList.add(BarEntry(4f, 300f)) // Apr
        barEntriesList.add(BarEntry(5f, 200f)) // May


        val barDataSet = BarDataSet(barEntriesList, "Monthly Sales Data")

        val barData = BarData(barDataSet)
        barChart.data = barData

        barDataSet.valueTextColor = Color.BLACK
        barDataSet.setColor(requireContext().getColor(R.color.purple_200)) // Ensure R.color.purple_200 is defined
        barDataSet.valueTextSize = 16f
        barChart.description.isEnabled = false

        // Invalidate chart to refresh
        barChart.invalidate()
    }

    // Clean up the binding in onDestroyView() to prevent memory leaks.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}