package com.techyexamplelogin.smartparking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class Searchdetails : AppCompatActivity() {
    private lateinit var vehicleNumberView: TextView
    private lateinit var companyNameView: TextView
    private lateinit var modelView: TextView
    private lateinit var locationField: TextView
    private lateinit var dateField: EditText
    private lateinit var timeField: EditText
    private lateinit var findParkingButton: Button
    private lateinit var pickLocationBtn: LinearLayout
    private var lat: String? = null
    private var long: String? = null
    private var street: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchdetails)

        vehicleNumberView = findViewById(R.id.vehicleNumberView)
        companyNameView = findViewById(R.id.companyNameView)
        modelView = findViewById(R.id.modelView)
        locationField = findViewById(R.id.locationField)
        dateField = findViewById(R.id.dateField)
        timeField = findViewById(R.id.timeField)
        findParkingButton = findViewById(R.id.findParkingButton)
        pickLocationBtn = findViewById(R.id.pick_location_btn)

        val vehicleNumber = intent.getStringExtra("vehicleNumber")
        val companyName = intent.getStringExtra("companyName")
        val model = intent.getStringExtra("model")
        street = intent.getStringExtra("location") ?: "Pick Location"
        val type = intent.getStringExtra("type")
        lat = intent.getStringExtra("lat")
        long = intent.getStringExtra("long")
        val typeImage = findViewById<ImageView>(R.id.vehicleTypeImage)
        locationField.setText(street)
        val drawableRes = when (type) {
            "car" -> R.drawable.ic_car
            "bike" -> R.drawable.ic_bike
            "jeep" -> R.drawable.ic_bus
            else -> R.drawable.ic_car
        }
        pickLocationBtn.setOnClickListener {
            val intent = Intent(this, SelectLocationActivity::class.java)
            locationPickerLauncher.launch(intent)
        }

        typeImage.setImageResource(drawableRes)
// may be null
        findParkingButton.setOnClickListener {
            val location = locationField.text.toString().trim()
            val date = dateField.text.toString().trim()
            val time = timeField.text.toString().trim()
            if(location=="Pick Location"){
                Toast.makeText(this, "Please Select the Location!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (location.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            if (lat.isNullOrEmpty() || long.isNullOrEmpty()) {
                Toast.makeText(this, "Location coordinates not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this@Searchdetails, MapActivity::class.java)
            intent.putExtra("lat", lat)
            intent.putExtra("long", long)
            intent.putExtra("startDate", date)
            intent.putExtra("startTime", time)

            startActivity(intent)
        }


        vehicleNumberView.text = vehicleNumber
        companyNameView.text = companyName
        modelView.text = model

        if (street != null) {
            locationField.setText(street)
            locationField.isEnabled = false
        }

        setupDatePicker(dateField)
        setupTimePicker(timeField)


    }
    private val locationPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                street = data.getStringExtra("address")
                lat =data.getStringExtra("lat")
                long =data.getStringExtra("long")
                locationField.setText(street)


            }
        }
    }

    private fun setupDatePicker(editText: EditText) {
        editText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                editText.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }
    }

    private fun setupTimePicker(editText: EditText) {
        editText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                editText.setText(formattedTime)
            }, hour, minute, true).show()
        }
    }
}