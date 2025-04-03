package com.techyexamplelogin.smartparking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class NavigationActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var usernameTextView: TextView
    private lateinit var carButton: ImageButton
    private lateinit var bikeButton: ImageButton
    private lateinit var busButton: ImageButton
    private lateinit var findSpaceButton: Button
    private lateinit var carDropdownLayout: LinearLayout
    private lateinit var carTimingLayout: LinearLayout
    private lateinit var bikeTimingLayout: LinearLayout
    private lateinit var busDropdownLayout: LinearLayout
    private lateinit var busTimingLayout: LinearLayout
    private lateinit var vehicleBrandDropdown: Spinner
    private lateinit var vehicleModelDropdown: Spinner
    private lateinit var busLengthDropdown: Spinner
    private lateinit var busWidthDropdown: Spinner
    private lateinit var fromTimeCar: EditText
    private lateinit var untilTimeCar: EditText
    private lateinit var fromTimeBike: EditText
    private lateinit var untilTimeBike: EditText
    private lateinit var fromTimeBus: EditText
    private lateinit var untilTimeBus: EditText
    private lateinit var fromDateCar: EditText
    private lateinit var untilDateCar: EditText
    private lateinit var fromDateBike: EditText
    private lateinit var untilDateBike: EditText
    private lateinit var fromDateBus: EditText
    private lateinit var untilDateBus: EditText
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_navigation)

        // Initialize Bottom Navigation
        setupBottomNavigation(R.id.nav_home)

        // Initialize Views
        usernameTextView = findViewById(R.id.welcomeText)
        carButton = findViewById(R.id.carButton)
        bikeButton = findViewById(R.id.bikeButton)
        busButton = findViewById(R.id.busButton)
        findSpaceButton = findViewById(R.id.findSpaceButton)
        carDropdownLayout = findViewById(R.id.carDropdownLayout)
        carTimingLayout = findViewById(R.id.carTimingLayout)
        bikeTimingLayout = findViewById(R.id.bikeTimingLayout)
        busDropdownLayout = findViewById(R.id.busDropdownLayout)
        busTimingLayout = findViewById(R.id.busTimingLayout)
        vehicleBrandDropdown = findViewById(R.id.vehicleBrandDropdown)
        vehicleModelDropdown = findViewById(R.id.vehicleModelDropdown)
        busLengthDropdown = findViewById(R.id.busLengthDropdown)
        busWidthDropdown = findViewById(R.id.busWidthDropdown)
        fromTimeCar = findViewById(R.id.fromTimeCar)
        untilTimeCar = findViewById(R.id.untilTimeCar)
        fromTimeBike = findViewById(R.id.fromTimeBike)
        untilTimeBike = findViewById(R.id.untilTimeBike)
        fromTimeBus = findViewById(R.id.fromTimeBus)
        untilTimeBus = findViewById(R.id.untilTimeBus)
        fromDateCar = findViewById(R.id.fromDateCar)
        untilDateCar = findViewById(R.id.untilDateCar)
        fromDateBike = findViewById(R.id.fromDateBike)
        untilDateBike = findViewById(R.id.untilDateBike)
        fromDateBus = findViewById(R.id.fromDateBus)
        untilDateBus = findViewById(R.id.untilDateBus)

        // Load Username from SharedPreferences
        sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val username = sharedPref.getString("USERNAME", "User") ?: "User"
        usernameTextView.text = "Hello, $username"
        usernameTextView.setTextColor(Color.BLACK)

        // Default Button Backgrounds
        resetButtonStyles()

        // Button Click Listeners
        carButton.setOnClickListener { showVehicleLayout("Car") }
        bikeButton.setOnClickListener { showVehicleLayout("Bike") }
        busButton.setOnClickListener { showVehicleLayout("Bus") }

        // Navigate to LocationSelectionActivity
        findSpaceButton.setOnClickListener {
            val isCarSelected = carDropdownLayout.visibility == View.VISIBLE
            val isBikeSelected = bikeTimingLayout.visibility == View.VISIBLE
            val isBusSelected = busDropdownLayout.visibility == View.VISIBLE

            var isValidSelection = false

            when {
                isCarSelected -> {
                    val brand = vehicleBrandDropdown.selectedItem.toString()
                    val model = vehicleModelDropdown.selectedItem.toString()
                    val fromTime = fromTimeCar.text.toString()
                    val untilTime = untilTimeCar.text.toString()

                    if (brand != "Select Brand" && model != "Select Model" && fromTime.isNotEmpty() && untilTime.isNotEmpty()) {
                        isValidSelection = true
                    } else {
                        Toast.makeText(this, "Please select vehicle brand, model, and timings", Toast.LENGTH_SHORT).show()
                    }
                }
                isBikeSelected -> {
                    val fromTime = fromTimeBike.text.toString()
                    val untilTime = untilTimeBike.text.toString()

                    if (fromTime.isNotEmpty() && untilTime.isNotEmpty()) {
                        isValidSelection = true
                    } else {
                        Toast.makeText(this, "Please select bike timings", Toast.LENGTH_SHORT).show()
                    }
                }
                isBusSelected -> {
                    val length = busLengthDropdown.selectedItem.toString()
                    val width = busWidthDropdown.selectedItem.toString()
                    val fromTime = fromTimeBus.text.toString()
                    val untilTime = untilTimeBus.text.toString()

                    if (length != "Select Length" && width != "Select Width" && fromTime.isNotEmpty() && untilTime.isNotEmpty()) {
                        isValidSelection = true
                    } else {
                        Toast.makeText(this, "Please select bus length, width, and timings", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if (isValidSelection) {
                val selectedLocation = sharedPref.getString("SELECTED_LOCATION", "Unknown Location") ?: "Unknown Location"

                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra("SELECTED_LOCATION", selectedLocation) // Pass selected location
                startActivity(intent)
            }
        }

        // Populate Dropdowns
        populateBrandDropdown()
        populateBusSizeDropdown()

        setupDatePicker(fromDateCar)
        setupDatePicker(untilDateCar)
        setupDatePicker(fromDateBike)
        setupDatePicker(untilDateBike)
        setupDatePicker(fromDateBus)
        setupDatePicker(untilDateBus)

        // Setup Time Pickers
        setupTimePicker(fromTimeCar)
        setupTimePicker(untilTimeCar)
        setupTimePicker(fromTimeBike)
        setupTimePicker(untilTimeBike)
        setupTimePicker(fromTimeBus)
        setupTimePicker(untilTimeBus)
    }

    private fun setupBottomNavigation(selectedItemId: Int) {
        bottomNavigation = findViewById(R.id.bottomNavigationView)
        bottomNavigation.selectedItemId = selectedItemId

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (selectedItemId != R.id.nav_home) {
                        startActivity(Intent(this, NavigationActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }
                R.id.nav_add_space -> {
                    if (selectedItemId != R.id.nav_add_space) {
                        startActivity(Intent(this, AddSpaceActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (selectedItemId != R.id.nav_profile) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun resetButtonStyles() {
        val defaultBackground = ContextCompat.getDrawable(this, R.drawable.vehicle_selector)
        carButton.background = defaultBackground
        bikeButton.background = defaultBackground
        busButton.background = defaultBackground
    }

    private fun showVehicleLayout(vehicleType: String) {
        resetButtonStyles()
        when (vehicleType) {
            "Car" -> {
                carButton.background = ContextCompat.getDrawable(this, R.drawable.vehicle_selected)
                carDropdownLayout.visibility = View.VISIBLE
                carTimingLayout.visibility = View.VISIBLE
                bikeTimingLayout.visibility = View.GONE
                busDropdownLayout.visibility = View.GONE
                busTimingLayout.visibility = View.GONE
            }
            "Bike" -> {
                bikeButton.background = ContextCompat.getDrawable(this, R.drawable.vehicle_selected)
                carDropdownLayout.visibility = View.GONE
                carTimingLayout.visibility = View.GONE
                bikeTimingLayout.visibility = View.VISIBLE
                busDropdownLayout.visibility = View.GONE
                busTimingLayout.visibility = View.GONE
            }
            "Bus" -> {
                busButton.background = ContextCompat.getDrawable(this, R.drawable.vehicle_selected)
                carDropdownLayout.visibility = View.GONE
                carTimingLayout.visibility = View.GONE
                bikeTimingLayout.visibility = View.GONE
                busDropdownLayout.visibility = View.VISIBLE
                busTimingLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun populateBrandDropdown() {
        val brands = listOf("Select Brand", "Toyota", "Honda", "Ford", "BMW", "Audi", "Mercedes")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, brands)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vehicleBrandDropdown.adapter = adapter

        vehicleBrandDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    populateModelDropdown(brands[position])
                } else {
                    populateModelDropdown(null)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun populateModelDropdown(brand: String?) {
        val modelsMap = mapOf(
            "Toyota" to listOf("Select Model", "Corolla", "Camry", "Supra"),
            "Honda" to listOf("Select Model", "Civic", "Accord", "CR-V"),
            "Ford" to listOf("Select Model", "Mustang", "Explorer"),
            "BMW" to listOf("Select Model", "X5", "X3"),
            "Audi" to listOf("Select Model", "A4", "Q7"),
            "Mercedes" to listOf("Select Model", "C-Class", "E-Class")
        )

        val models = modelsMap[brand] ?: listOf("Select Model")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, models)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vehicleModelDropdown.adapter = adapter
    }

    private fun populateBusSizeDropdown() {
        val lengths = listOf("Select Length", "8m", "10m", "12m")
        val widths = listOf("Select Width", "2.5m", "3m")
        busLengthDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lengths)
        busWidthDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, widths)
    }

    private fun setupDatePicker(editText: EditText) {
        editText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                editText.setText(formattedDate)
                editText.setTextColor(Color.BLACK)
            }, year, month, day).show()
        }
    }

    private fun setupTimePicker(editText: EditText) {
        editText.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                val formattedTime = String.format("%02d:%02d", hour, minute)
                editText.setText(formattedTime)
                editText.setTextColor(Color.BLACK)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }
}