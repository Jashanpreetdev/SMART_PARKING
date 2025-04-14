package com.techyexamplelogin.smartparking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.techyexamplelogin.smartparking.NavigationActivity.VehileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AddSpaceActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var etDateFrom: TextInputEditText
    private lateinit var etTimeFrom: TextInputEditText
    private lateinit var etDateTo: TextInputEditText
    private lateinit var etTimeTo: TextInputEditText

    private lateinit var etAddress: TextInputEditText
    private lateinit var etCity: TextInputEditText
    private lateinit var etPin: TextInputEditText

    private lateinit var btnSubmit: Button
    private lateinit var pickLocationBtn: LinearLayout
    private var lat: String? = null
    private var long: String? = null


    // Activity result launcher
    private val locationPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                val street = data.getStringExtra("address")
                val city = data.getStringExtra("city")
                val pin = data.getStringExtra("pin")
                lat =data.getStringExtra("lat")
                long =data.getStringExtra("long")
                etAddress.setText(street)
                etCity.setText(city)
                etPin.setText(pin)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_space)

        // Initialize views
        etDateFrom = findViewById(R.id.et_date_from)
        etTimeFrom = findViewById(R.id.et_time_from)
        etDateTo = findViewById(R.id.et_date_to)
        etTimeTo = findViewById(R.id.et_time_to)

        etAddress = findViewById(R.id.et_address)
        etCity = findViewById(R.id.et_city)
        etPin = findViewById(R.id.et_pin)

        btnSubmit = findViewById(R.id.btn_submit)
        pickLocationBtn = findViewById(R.id.pick_location_btn)

        bottomNavigation = findViewById(R.id.bottomNavigationView)

        setupBottomNavigation()
        setupDatePicker(etDateFrom)
        setupDatePicker(etDateTo)
        setupTimePicker(etTimeFrom)
        setupTimePicker(etTimeTo)
        fun navigateToProfile() {
            Handler(mainLooper).postDelayed({
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }, 500) // Delay for 2 seconds
        }
        fun AddParkingfun(addParking: AddParking) {
            val apiservice = RetrofitInstance.instance.create(ApiService::class.java)
            val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
            val ID = sharedPref.getString("TOKEN", null)
            println(ID)
            apiservice.Addparking("Bearer $ID",addParking).enqueue(object : Callback<Any?> {
                override fun onResponse(
                    call: Call<Any?>,
                    response: Response<Any?>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddSpaceActivity,"Parking Space Added !",Toast.LENGTH_SHORT).show()
                        navigateToProfile()
                    }
                    else{
                        Toast.makeText(this@AddSpaceActivity,"Error in Adding Space!",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Any?>, t: Throwable) {
                    // Handle failure
                }
            })
        }


        pickLocationBtn.setOnClickListener {
            val intent = Intent(this, SelectLocationActivity::class.java)
            locationPickerLauncher.launch(intent)
        }

        btnSubmit.setOnClickListener {
            val address = findViewById<TextInputEditText>(R.id.et_address).text?.toString()?.trim()
            val city = findViewById<TextInputEditText>(R.id.et_city).text?.toString()?.trim()
            val pin = findViewById<TextInputEditText>(R.id.et_pin).text?.toString()?.trim()

            val fromDate = etDateFrom.text?.toString()?.trim()
            val fromTime = etTimeFrom.text?.toString()?.trim()
            val toDate = etDateTo.text?.toString()?.trim()
            val toTime = etTimeTo.text?.toString()?.trim()

            val lengthStr = findViewById<TextInputEditText>(R.id.et_length).text?.toString()?.trim()
            val widthStr = findViewById<TextInputEditText>(R.id.et_width).text?.toString()?.trim()

            // Check if any required field is empty
            if (address.isNullOrEmpty() || city.isNullOrEmpty() || pin.isNullOrEmpty() ||
                fromDate.isNullOrEmpty() || fromTime.isNullOrEmpty() ||
                toDate.isNullOrEmpty() || toTime.isNullOrEmpty() ||
                lengthStr.isNullOrEmpty() || widthStr.isNullOrEmpty()
            ) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val length = lengthStr.toDoubleOrNull()
            val width = widthStr.toDoubleOrNull()

            if (length == null || width == null) {
                Toast.makeText(this, "Invalid dimensions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (lat == null || long == null) {
                Toast.makeText(this, "Location not selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val coordinates = Coordinates(long, lat)
            val dimensions = Dimensions(length, width)

            val addParking = AddParking(
                street = address,
                city = city,
                pincode = pin,
                startDate = fromDate,
                startTime = fromTime,
                endDate = toDate,
                endTime = toTime,
                dimensions = dimensions,
                coordinates = coordinates
            )

            AddParkingfun(addParking)
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

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                editText.setText(formattedTime)
            }, hour, minute, true)

            timePickerDialog.show()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_add_space

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, NavigationActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_add_space -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
