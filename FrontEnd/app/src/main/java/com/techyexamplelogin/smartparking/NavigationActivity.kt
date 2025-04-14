package com.techyexamplelogin.smartparking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.Callback
import retrofit2.Response

import java.util.*
import kotlin.collections.List

data class Vehicle(
    val id: String,
    val companyName: String,
    val model: String,
    val type:String,
    val vehicleNumber :String// URL of vehicle image
)


class NavigationActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var usernameTextView: TextView
    private lateinit var carButton: ImageButton
    private lateinit var bikeButton: ImageButton
    private lateinit var busButton: ImageButton
    private lateinit var addVehileLayout: LinearLayout
    private lateinit var sharedPref: SharedPreferences
    private  lateinit var addVehicle : Button
    private  lateinit var locationInput : EditText
    private var lat: String? = null
    private var long: String? = null
    private var street: String? = null
    private lateinit var locationtext: TextView




    data class VehileResponse(
        val success: Boolean,
        val message: String,
        val data: List<Vehicle>?,  // Change Objects to Any? (nullable)
        val error: Any?  // Change Objects to Any? (nullable)
    )

    val vehicleList = mutableListOf<Vehicle>()
    var type=""


    private val locationPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
              street = data.getStringExtra("address")
                val city = data.getStringExtra("city")
                val pin = data.getStringExtra("pin")
                lat =data.getStringExtra("lat")
                long =data.getStringExtra("long")
                locationtext.setText(street)
                val sharedPref = getSharedPreferences("LOCATION", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("street",street )
                    putString("long",long )
                    putString("lat",lat )

                    apply()
                }

            }
        }
    }

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
        locationtext=findViewById(R.id.locationtext)
        locationInput=findViewById(R.id.locationInput)
        addVehileLayout = findViewById(R.id.addVehileLayout)
        val companyField = findViewById<TextInputEditText>(R.id.vehicleCompany)
        val modelField = findViewById<TextInputEditText>(R.id.vehicleModel)
        val numberField = findViewById<TextInputEditText>(R.id.vehicleNumber)// dropdown
        val SubmitAdd= findViewById<Button>(R.id.submitAdd)


        locationInput.setOnClickListener {
            val intent = Intent(this, SelectLocationActivity::class.java)
            locationPickerLauncher.launch(intent)
        }
        val sharedPrefloc = getSharedPreferences("LOCATION", MODE_PRIVATE)
        street = sharedPrefloc.getString("street", null)
        long = sharedPrefloc.getString("long", null)
        lat = sharedPrefloc.getString("lat", null)
        locationtext.setText(street)


        addVehicle= findViewById( R.id.addVehicle)

            val vehicleRecyclerView: RecyclerView = findViewById(R.id.vehicleRecyclerView)
            fun fetchVehicles(mode:Number?) {
                val apiservice = RetrofitInstance.instance.create(ApiService::class.java)
                val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
                val ID = sharedPref.getString("ID", null)
                println(ID)
                apiservice.fetchVeh(ID).enqueue(object : Callback<VehileResponse> {
                    override fun onResponse(
                        call: Call<VehileResponse>,
                        response: Response<VehileResponse>
                    ) {
                        if (response.isSuccessful) {

                            vehicleList.clear()
                            vehicleList.addAll(response.body()?.data ?: emptyList())
                            vehicleRecyclerView.adapter?.notifyDataSetChanged()

                            if(mode==1){
                            vehicleRecyclerView.visibility=View.VISIBLE
                            vehicleRecyclerView.layoutManager =
                                LinearLayoutManager(this@NavigationActivity, LinearLayoutManager.HORIZONTAL, false)
                                vehicleRecyclerView.adapter = VehicleAdapter(vehicleList.filter { it.type == type }) { selectedVehicle ->
                                    val intent = Intent(this@NavigationActivity, Searchdetails::class.java)
                                    intent.putExtra("vehicleNumber", selectedVehicle.vehicleNumber)
                                    intent.putExtra("companyName", selectedVehicle.companyName)
                                    intent.putExtra("model", selectedVehicle.model)
                                    intent.putExtra("type", selectedVehicle.type)
                                    intent.putExtra("lat", lat)
                                    intent.putExtra("long", long)
                                    intent.putExtra("location",street )
                                    startActivity(intent)
                                }
                            addVehileLayout.visibility=View.GONE
                            }

                        }
                    }

                    override fun onFailure(call: Call<VehileResponse>, t: Throwable) {
                        // Handle failure
                    }
                })
            }
            fun handleSubmit(companyName: String,model: String,number: String,type: String) {
                SubmitAdd.isEnabled=false
                SubmitAdd.alpha=0.9f
                val apiService = RetrofitInstance.instance.create(ApiService::class.java)
                val vehile = Addvehicle(companyName, model, number, type)
                val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
                val ID = sharedPref.getString("TOKEN", "")
                apiService.Addvehicle("Bearer $ID", vehile).enqueue(object : Callback<Any?> {
                    override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                        if (response.isSuccessful) {
                            val responseData = response.body()
                            fetchVehicles(1)




                            SubmitAdd.isEnabled=true
                            SubmitAdd.alpha=1f


                        }
                        else {
                            println("Failed")

                        }
                    }
                    override fun onFailure(call: Call<Any?>, t: Throwable) {
                        // Handle failure
                    }
                })
            }




        // Sample Data (Replace this with backend API data)
    fetchVehicles(0)

        // Setup RecyclerView

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

        addVehicle.setOnClickListener{
            val vehicleRecyclerView : RecyclerView = findViewById(R.id.vehicleRecyclerView)
            vehicleRecyclerView.visibility=View.GONE
            addVehicle.visibility=View.VISIBLE
            addVehileLayout.visibility=View.VISIBLE
            val nodata =findViewById<TextView>(R.id.nodata)
            nodata.visibility=View.GONE

        }
        SubmitAdd.setOnClickListener{
            val company = companyField.text.toString().trim()
            val model = modelField.text.toString().trim()
            val number = numberField.text.toString().trim()
            if (company.isEmpty() || model.isEmpty() || number.isEmpty() || type.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                handleSubmit(company, model, number, type)
            }
            companyField.text?.clear()
            modelField.text?.clear()
            numberField.text?.clear()
        }
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
        val carimage =findViewById<ImageView>(R.id.carimage)
        carimage.visibility=View.GONE
        resetButtonStyles()
        val nodata =findViewById<TextView>(R.id.nodata)
        when (vehicleType) {
            "Car" -> {

                carButton.background = ContextCompat.getDrawable(this, R.drawable.vehicle_selected)
                type="car"
                val vehileListcont:LinearLayout = findViewById(R.id.vehileListcont)
                vehileListcont.visibility=View.VISIBLE
                val vehicleRecyclerView: RecyclerView = findViewById(R.id.vehicleRecyclerView)
                vehicleRecyclerView.visibility=View.VISIBLE
                vehicleRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                if (vehicleList.filter { it.type=="car" }.isEmpty()){
                    nodata.visibility=View.VISIBLE
                }
                else{
                    nodata.visibility=View.GONE
                }
                vehicleRecyclerView.adapter = VehicleAdapter(vehicleList.filter { it.type == type }) { selectedVehicle ->
                    val intent = Intent(this@NavigationActivity, Searchdetails::class.java)
                    intent.putExtra("vehicleNumber", selectedVehicle.vehicleNumber)
                    intent.putExtra("companyName", selectedVehicle.companyName)
                    intent.putExtra("model", selectedVehicle.model)
                    intent.putExtra("type", selectedVehicle.type)
                    intent.putExtra("lat", lat)
                    intent.putExtra("long", long)
                    intent.putExtra("location",street )
                    startActivity(intent)
                }
                addVehileLayout.visibility=View.GONE



//                carDropdownLayout.visibility = View.VISIBLE
//                carTimingLayout.visibility = View.VISIBLE
//                bikeTimingLayout.visibility = View.GONE
//                busDropdownLayout.visibility = View.GONE
//                busTimingLayout.visibility = View.GONE
            }
            "Bike" -> {
                bikeButton.background = ContextCompat.getDrawable(this, R.drawable.vehicle_selected)
                type="bike"
                val vehileListcont:LinearLayout = findViewById(R.id.vehileListcont)
                vehileListcont.visibility=View.VISIBLE
                val vehicleRecyclerView: RecyclerView = findViewById(R.id.vehicleRecyclerView)
                vehicleRecyclerView.visibility=View.VISIBLE
                vehicleRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                vehicleRecyclerView.adapter = VehicleAdapter(vehicleList.filter { it.type == type }) { selectedVehicle ->
                    val intent = Intent(this@NavigationActivity, Searchdetails::class.java)
                    intent.putExtra("vehicleNumber", selectedVehicle.vehicleNumber)
                    intent.putExtra("companyName", selectedVehicle.companyName)
                    intent.putExtra("model", selectedVehicle.model)
                    intent.putExtra("type", selectedVehicle.type)
                    intent.putExtra("lat", lat)
                    intent.putExtra("long", long)
                    intent.putExtra("location",street )
                    startActivity(intent)
                }
                if (vehicleList.filter { it.type=="bike" }.isEmpty()){
                    nodata.visibility=View.VISIBLE
                }
                else{
                    nodata.visibility=View.GONE
                }
                addVehileLayout.visibility=View.GONE
//                carDropdownLayout.visibility = View.GONE
//                carTimingLayout.visibility = View.GONE
//                bikeTimingLayout.visibility = View.VISIBLE
//                busDropdownLayout.visibility = View.GONE
//                busTimingLayout.visibility = View.GONE
            }
            "Bus" -> {
                busButton.background = ContextCompat.getDrawable(this, R.drawable.vehicle_selected)
                type="jeep"
                val vehileListcont:LinearLayout = findViewById(R.id.vehileListcont)
                vehileListcont.visibility=View.VISIBLE
                val vehicleRecyclerView: RecyclerView = findViewById(R.id.vehicleRecyclerView)
                vehicleRecyclerView.visibility=View.VISIBLE
                vehicleRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                vehicleRecyclerView.adapter = VehicleAdapter(vehicleList.filter { it.type == type }) { selectedVehicle ->
                    val intent = Intent(this@NavigationActivity , Searchdetails::class.java)
                    intent.putExtra("vehicleNumber", selectedVehicle.vehicleNumber)
                    intent.putExtra("companyName", selectedVehicle.companyName)
                    intent.putExtra("model", selectedVehicle.model)
                    intent.putExtra("type", selectedVehicle.type)
                    intent.putExtra("lat", lat)
                    intent.putExtra("long", long)
                    intent.putExtra("location",street )
                    startActivity(intent)
                }
                if (vehicleList.filter { it.type=="jeep" }.isEmpty()){
                    nodata.visibility=View.VISIBLE
                }
                else{
                    nodata.visibility=View.GONE
                }
                addVehileLayout.visibility=View.GONE
//                carDropdownLayout.visibility = View.GONE
//                carTimingLayout.visibility = View.GONE
//                bikeTimingLayout.visibility = View.GONE
//                busDropdownLayout.visibility = View.VISIBLE
//                busTimingLayout.visibility = View.VISIBLE
            }
        }
    }

//    private fun populateBrandDropdown() {
//        val brands = listOf("Select Brand", "Toyota", "Honda", "Ford", "BMW", "Audi", "Mercedes")
//
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, brands)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        vehicleBrandDropdown.adapter = adapter
//
//        vehicleBrandDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                if (position > 0) {
//                    populateModelDropdown(brands[position])
//                } else {
//                    populateModelDropdown(null)
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//    }

//    private fun populateModelDropdown(brand: String?) {
//        val modelsMap = mapOf(
//            "Toyota" to listOf("Select Model", "Corolla", "Camry", "Supra"),
//            "Honda" to listOf("Select Model", "Civic", "Accord", "CR-V"),
//            "Ford" to listOf("Select Model", "Mustang", "Explorer"),
//            "BMW" to listOf("Select Model", "X5", "X3"),
//            "Audi" to listOf("Select Model", "A4", "Q7"),
//            "Mercedes" to listOf("Select Model", "C-Class", "E-Class")
//        )
//
//        val models = modelsMap[brand] ?: listOf("Select Model")
//
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, models)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        vehicleModelDropdown.adapter = adapter
//    }

//    private fun populateBusSizeDropdown() {
//        val lengths = listOf("Select Length", "8m", "10m", "12m")
//        val widths = listOf("Select Width", "2.5m", "3m")
//        busLengthDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lengths)
//        busWidthDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, widths)
//    }

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