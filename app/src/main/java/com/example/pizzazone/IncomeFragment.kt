package com.example.pizzazone

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.CalendarView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.example.pizzazone.databinding.FragmentIncomeBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

// MPAndroidChart imports for XAxis formatting
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.YAxis

// Firebase Imports
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class IncomeFragment : Fragment() {


    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!


    private lateinit var database: FirebaseDatabase

    // Date formatter for Firebase dates (e.g., "2025-07-16")
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    // Month formatter for displaying month names on the Bar Chart X-axis
    private val monthFormatter = SimpleDateFormat("MMM", Locale.getDefault())


    // To keep track of the currently selected date for pie chart filtering
    private var selectedDateForPieChart: String = ""

    // Dynamic product titles for mapping categories
    private val productTitles = mutableListOf<String>()

    // Map to store product name to its price
    private val productPrices = mutableMapOf<String, Double>()

    // Define all possible categories that you want in your legend
    private val allDisplayCategories = listOf(
        "Solar panel",
        "Lithium Battery",
        "Inverters",
        "Others",
        "Solar Panel System" // Add this if you have a distinct category for systems
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        val view = binding.root


        database = FirebaseDatabase.getInstance()


        setupPieChart()
        setupBarChart()


        fetchTotalCustomers()
        // Fetch product titles and prices first, then proceed with other data fetching
        fetchProductDataAndInitialize()

        // Initialize selectedDateForPieChart to today's date
        selectedDateForPieChart = dateFormatter.format(Calendar.getInstance().time)

        // Set up CalendarView listener
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            selectedDateForPieChart = dateFormatter.format(selectedCalendar.time)
            fetchSelectedDateIncome(selectedDateForPieChart)
            // Update pie chart immediately on date change
            updateCategorySalesChart()
        }

        // Set up RadioGroup listener for Day/Month filter for Pie Chart
        binding.categorySalesFilterGroup.setOnCheckedChangeListener { _, checkedId ->
            // Update pie chart when radio button changes
            updateCategorySalesChart()
        }


        binding.backArrowIncome.setOnClickListener {

            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Handle Order History Button click
        binding.orderHistoryButton.setOnClickListener {
            Log.d("IncomeFragment", "Order History Button Clicked!")
            // Implement navigation to Order History Fragment/Activity here
        }

        return view
    }



    private fun fetchTotalCustomers() {

    // --- Fetch Product Data (Titles and Prices) from Firebase ---
    private fun fetchProductDataAndInitialize() {
        val productsRef = database.getReference("Products") // Your 'Products' node

        productsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productTitles.clear() // Clear previous data
                productPrices.clear() // Clear previous data

                for (productSnapshot in snapshot.children) {
                    val title = productSnapshot.child("title").getValue(String::class.java)
                    // Assuming you have a 'price' field in your Products node
                    val price = productSnapshot.child("price").getValue(Double::class.java) ?: 0.0

                    if (title != null) {
                        productTitles.add(title)
                        productPrices[title] = price // Store price with title
                    }
                }
                Log.d("IncomeFragment", "Fetched Product Titles: $productTitles")
                Log.d("IncomeFragment", "Fetched Product Prices: $productPrices")

                // After fetching products, proceed with setting up charts and other data
                setupPieChart()
                // Call fetchMonthlySalesDataForBarChart here to populate the bar chart with real data
                fetchMonthlySalesDataForBarChart()
                fetchTotalCustomers()
                fetchSalesData()
                fetchSelectedDateIncome(selectedDateForPieChart)
                updateCategorySalesChart() // Initial update for pie chart based on today's date
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("IncomeFragment", "Failed to load product data: ${error.message}")
                // Handle error: maybe show a message or use default empty data
                setupPieChart() // Setup with empty/dummy data if fetching fails
                setupBarChartWithData(ArrayList(), ArrayList()) // Setup bar chart with empty data
                fetchTotalCustomers()
                fetchSalesData()
                fetchSelectedDateIncome(selectedDateForPieChart)
                updateCategorySalesChart() // Try to update chart even on error, might show "No Sales Data"
            }
        })
    }


    // --- Fetch Sales Data (Today's and Monthly) from Firebase ---
    private fun fetchSalesData() {
        val ordersRef = database.getReference("Orders")

        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var todaySalesAmount = 0.0
                var monthlySalesAmount = 0.0

                val todayCalendar = Calendar.getInstance()
                val currentMonth = todayCalendar.get(Calendar.MONTH) // 0-indexed
                val currentYear = todayCalendar.get(Calendar.YEAR)

                val todayDateString = dateFormatter.format(todayCalendar.time)

                for (orderSnapshot in snapshot.children) {
                    val orderDateString = orderSnapshot.child("date").getValue(String::class.java)
                    val totalAmount = orderSnapshot.child("totalAmount").getValue(Double::class.java)

                    if (orderDateString != null && totalAmount != null) {
                        try {
                            val orderDate = dateFormatter.parse(orderDateString)
                            val orderCalendar = Calendar.getInstance().apply {
                                time = orderDate!!
                            }

                            // Check for Today's Sales
                            if (orderDateString == todayDateString) {
                                todaySalesAmount += totalAmount
                            }

                            // Check for Monthly Sales
                            if (orderCalendar.get(Calendar.MONTH) == currentMonth &&
                                orderCalendar.get(Calendar.YEAR) == currentYear) {
                                monthlySalesAmount += totalAmount
                            }

                        } catch (e: ParseException) {
                            Log.e("IncomeFragment", "Error parsing date: $orderDateString", e)
                        }
                    }
                }

                binding.todaySalesValue.text = String.format(Locale.getDefault(), "%.2f$", todaySalesAmount)
                binding.monthlySalesValue.text = String.format(Locale.getDefault(), "%.2f$", monthlySalesAmount)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("IncomeFragment", "Failed to load sales data: ${error.message}")
                binding.todaySalesValue.text = "Error"
                binding.monthlySalesValue.text = "Error"
            }
        })
    }

    // --- Fetch Selected Date Income from Firebase ---
    private fun fetchSelectedDateIncome(selectedDate: String) {
        val ordersRef = database.getReference("Orders")
        var selectedDateIncome = 0.0

        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderDateString = orderSnapshot.child("date").getValue(String::class.java)
                    val totalAmount = orderSnapshot.child("totalAmount").getValue(Double::class.java)

                    if (orderDateString == selectedDate && totalAmount != null) {
                        selectedDateIncome += totalAmount
                    }
                }
                binding.selectedDateIncomeValue.text = String.format(Locale.getDefault(), "%.2f$", selectedDateIncome)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("IncomeFragment", "Failed to load selected date income: ${error.message}")
                binding.selectedDateIncomeValue.text = "Error"
            }
        })
    }

    // --- Fetch Total Customers from Firebase ---
    private fun fetchTotalCustomers() {

        val customersRef = database.getReference("Customers")

        customersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val totalCustomers = snapshot.childrenCount


                val totalCustomers = snapshot.childrenCount

                binding.totalCustomersValue.text = totalCustomers.toString()
            }

            override fun onCancelled(error: DatabaseError) {

                binding.totalCustomersValue.text = "Error" // Or handle more gracefully

                binding.totalCustomersValue.text = "Error"
                Log.e("IncomeFragment", "Failed to load customers: ${error.message}")
            }
        })
    }

    // --- Function to fetch and update Pie Chart data ---
    private fun updateCategorySalesChart() {
        val ordersRef = database.getReference("Orders")
        val categorySales = mutableMapOf<String, Float>() // Map to store sales per category

        val isDayFilter = binding.radioDay.isChecked
        val calendarForFilter = Calendar.getInstance().apply {
            try {
                time = dateFormatter.parse(selectedDateForPieChart)!!
            } catch (e: ParseException) {
                Log.e("IncomeFragment", "Error parsing selectedDateForPieChart: $selectedDateForPieChart", e)
                time = Date() // Fallback to current date
            }
        }
        val filterMonth = calendarForFilter.get(Calendar.MONTH)
        val filterYear = calendarForFilter.get(Calendar.YEAR)

        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderDateString = orderSnapshot.child("date").getValue(String::class.java)
                    if (orderDateString == null) continue

                    try {
                        val orderDate = dateFormatter.parse(orderDateString)
                        val orderCalendar = Calendar.getInstance().apply { time = orderDate!! }

                        val matchesFilter = if (isDayFilter) {
                            // Check if the order date matches the selected day
                            orderDateString == selectedDateForPieChart
                        } else {
                            // Check if the order month and year match the selected month/year
                            orderCalendar.get(Calendar.MONTH) == filterMonth &&
                                    orderCalendar.get(Calendar.YEAR) == filterYear
                        }

                        if (matchesFilter) {
                            val itemsSnapshot = orderSnapshot.child("items")
                            for (itemSnapshot in itemsSnapshot.children) {
                                val itemName = itemSnapshot.child("itemName").getValue(String::class.java)
                                val quantity = itemSnapshot.child("quantity").getValue(Long::class.java)

                                if (itemName != null && quantity != null) {
                                    val category = getCategoryFromItemName(itemName)
                                    val salesAmountForItem = (quantity * getItemUnitPrice(itemName)).toFloat()
                                    categorySales[category] = categorySales.getOrDefault(category, 0f) + salesAmountForItem
                                }
                            }
                        }
                    } catch (e: ParseException) {
                        Log.e("IncomeFragment", "Error parsing order date: $orderDateString", e)
                    }
                }

                val entries: ArrayList<PieEntry> = ArrayList()
                var totalSalesForPieChart = 0f

                // Iterate through all defined display categories to ensure they are represented
                for (category in allDisplayCategories) {
                    val sales = categorySales.getOrDefault(category, 0f)
                    // Add to entries only if sales are greater than 0, as MikePhilChart doesn't draw 0-value slices
                    if (sales > 0) {
                        entries.add(PieEntry(sales, category))
                        totalSalesForPieChart += sales
                    }
                }

                if (entries.isEmpty()) {
                    // If no sales data for the selected period, display a "No Sales Data" slice
                    entries.add(PieEntry(1f, "No Sales Data")) // Value 1f to make it a full circle
                    setupPieChartData(entries, "No Sales Data")
                } else {
                    setupPieChartData(entries, "Category Sales")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("IncomeFragment", "Failed to load category sales data: ${error.message}")
                // If there's an error, display "Error Loading Data"
                setupPieChartData(ArrayList(), "Error Loading Data")
            }
        })
    }

    // Helper function to map item names to categories
    private fun getCategoryFromItemName(itemName: String): String {
        // This mapping should be robust and cover all your products
        return when {
            itemName.contains("Solar Panel", ignoreCase = true) && !itemName.contains("System", ignoreCase = true) -> "Solar panel"
            itemName.contains("Lithium Battery", ignoreCase = true) -> "Lithium Battery"
            itemName.contains("Inverter", ignoreCase = true) -> "Inverters"
            // If you have a specific product that constitutes a "Solar Panel System"
            // and you want it as a separate category
            itemName.contains("Solar Panel System", ignoreCase = true) -> "Solar Panel System"
            // All other items fall into "Others"
            else -> "Others"
        }
    }


    // Helper function to get unit price of an item
    private fun getItemUnitPrice(itemName: String): Double {
        // Try to get price from the productPrices map which is fetched from Firebase
        val price = productPrices[itemName]
        if (price != null && price > 0.0) {
            return price
        }

        // Fallback/Dummy prices if not found or if price is 0.0
        // It's crucial that your Firebase "Products" node has actual prices for all items.
        Log.w("IncomeFragment", "Price for item '$itemName' not found or is 0.0 in fetched product prices. Using dummy price.")
        return when {
            itemName.contains("Solar Panel", ignoreCase = true) -> 150.0
            itemName.contains("Lithium Battery", ignoreCase = true) -> 100.0
            itemName.contains("Inverter", ignoreCase = true) -> 200.0
            // Add specific dummy prices for other common categories if productPrices is not reliable
            else -> 50.0 // Default dummy price for 'Others'
        }
    }


    // --- Pie Chart Setup (basic configuration) ---
    private fun setupPieChart() {
        val pieChart = binding.pieChart

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
        pieChart.legend.isEnabled = false // Disable built-in legend as you have a custom one in XML
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)



        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(70f, "Solar panel"))
        entries.add(PieEntry(20f, "Lithium Battery"))
        entries.add(PieEntry(10f, "Inverters"))

        // No need to call updateCategorySalesChart() here, it's called after product data is fetched
    }

    // This function actually sets the data to the pie chart
    private fun setupPieChartData(entries: ArrayList<PieEntry>, dataSetLabel: String) {
        val pieChart = binding.pieChart

        val dataSet = PieDataSet(entries, dataSetLabel)
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors: ArrayList<Int> = ArrayList()

        colors.add(Color.parseColor("#FFC107"))
        colors.add(Color.parseColor("#2196F3"))
        colors.add(Color.parseColor("#4CAF50"))

        // Define colors for your categories in the order you expect them or map them explicitly
        // Make sure these colors align with your XML legend colors
        val solarPanelColor = Color.parseColor("#FFC107")
        val lithiumBatteryColor = Color.parseColor("#2196F3")
        val invertersColor = Color.parseColor("#4CAF50")
        val othersColor = Color.parseColor("#9C27B0")
        val solarPanelSystemColor = Color.parseColor("#673AB7")
        val noSalesColor = Color.GRAY // A distinct color for "No Sales Data"

        // Map colors to categories for consistency
        val categoryColorsMap = mapOf(
            "Solar panel" to solarPanelColor,
            "Lithium Battery" to lithiumBatteryColor,
            "Inverters" to invertersColor,
            "Others" to othersColor,
            "Solar Panel System" to solarPanelSystemColor,
            "No Sales Data" to noSalesColor
        )

        // Assign colors based on the category name in the PieEntry
        val assignedColors = ArrayList<Int>()
        for (entry in entries) {
            assignedColors.add(categoryColorsMap.getOrDefault(entry.label, Color.BLACK)) // Default to black if category not mapped
        }
        dataSet.colors = assignedColors

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
        val barChart = binding.monthlyBarChart


        val barEntriesList: ArrayList<BarEntry> = ArrayList()
        barEntriesList.add(BarEntry(1f, 100f)) // Jan
        barEntriesList.add(BarEntry(2f, 250f)) // Feb
        barEntriesList.add(BarEntry(3f, 150f)) // Mar
        barEntriesList.add(BarEntry(4f, 300f)) // Apr
        barEntriesList.add(BarEntry(5f, 200f)) // May

    // --- Fetch Monthly Sales Data for Bar Chart from Firebase ---
    private fun fetchMonthlySalesDataForBarChart() {
        val ordersRef = database.getReference("Orders")
        val monthlySalesMap = mutableMapOf<String, Float>() // "YYYY-MM" -> total sales for that month

        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderDateString = orderSnapshot.child("date").getValue(String::class.java)
                    val totalAmount = orderSnapshot.child("totalAmount").getValue(Double::class.java)

                    if (orderDateString != null && totalAmount != null) {
                        try {
                            val orderDate = dateFormatter.parse(orderDateString)
                            val orderCalendar = Calendar.getInstance().apply { time = orderDate!! }

                            // Format date to "YYYY-MM" to group by month
                            val monthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(orderCalendar.time)
                            monthlySalesMap[monthKey] = monthlySalesMap.getOrDefault(monthKey, 0f) + totalAmount.toFloat()

                        } catch (e: ParseException) {
                            Log.e("IncomeFragment", "Error parsing order date for bar chart: $orderDateString", e)
                        }
                    }
                }

                // Sort the months to ensure correct order on the chart
                val sortedMonthKeys = monthlySalesMap.keys.toList().sorted()

                val barEntriesList: ArrayList<BarEntry> = ArrayList()
                val monthLabels: ArrayList<String> = ArrayList()

                // Prepare entries and labels for the chart
                for ((index, monthKey) in sortedMonthKeys.withIndex()) {
                    val sales = monthlySalesMap[monthKey] ?: 0f
                    // Use index + 1 as x-value for BarEntry
                    barEntriesList.add(BarEntry(index.toFloat(), sales))

                    // Extract month name (e.g., "Jan", "Feb") from "YYYY-MM"
                    try {
                        val dateForMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(monthKey)
                        monthLabels.add(monthFormatter.format(dateForMonth!!))
                    } catch (e: ParseException) {
                        Log.e("IncomeFragment", "Error parsing month key for label: $monthKey", e)
                        monthLabels.add("N/A") // Fallback
                    }
                }

                // Pass the prepared data to the setup function
                setupBarChartWithData(barEntriesList, monthLabels)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("IncomeFragment", "Failed to load monthly sales data for bar chart: ${error.message}")
                setupBarChartWithData(ArrayList(), ArrayList()) // Show empty chart on error
            }
        })
    }


    // --- Bar Chart Setup (with dynamic data) ---
    private fun setupBarChartWithData(barEntriesList: ArrayList<BarEntry>, monthLabels: ArrayList<String>) {
        val barChart = binding.monthlyBarChart


        // If no data, display a message or clear the chart
        if (barEntriesList.isEmpty()) {
            barChart.setNoDataText("No monthly sales data available.")
            barChart.invalidate()
            return
        }

        val barDataSet = BarDataSet(barEntriesList, "Monthly Sales Data")

        val barData = BarData(barDataSet)
        barChart.data = barData

        barDataSet.valueTextColor = Color.BLACK

        barDataSet.setColor(requireContext().getColor(R.color.purple_200))
        barDataSet.valueTextSize = 16f
        barChart.description.isEnabled = false


        barChart.invalidate()

        barDataSet.setColor(requireContext().getColor(R.color.purple_200)) // Ensure R.color.purple_200 is defined
        barDataSet.valueTextSize = 12f // Reduced text size for better fit
        barData.barWidth = 0.5f // Adjust bar width

        barChart.description.isEnabled = false // Disable description label
        barChart.animateY(1000) // Add animation for bar chart

        // Customize X-axis labels to show month names
        val xAxis: XAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(monthLabels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f // Set granularity to 1 to ensure all labels are displayed if enough space
        xAxis.setCenterAxisLabels(true) // Center labels below bars
        xAxis.setLabelCount(monthLabels.size, false) // Ensure all labels are shown, if possible

        // Customize Y-axis (left) to show dollar values
        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.valueFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format(Locale.getDefault(), "%.2f$", value)
            }
        }
        leftAxis.setDrawGridLines(true)
        leftAxis.setAxisMinimum(0f) // Start y-axis from 0

        // Disable right Y-axis
        barChart.axisRight.isEnabled = false

        barChart.invalidate() // Refresh the chart

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
}