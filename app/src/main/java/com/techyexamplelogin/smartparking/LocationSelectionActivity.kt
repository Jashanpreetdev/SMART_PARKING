package com.techyexamplelogin.smartparking

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LocationSelectionActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var locationList: List<String>
    private lateinit var etSearchLocation: EditText
    private lateinit var listView: ListView
    private lateinit var btnSetCurrentLocation: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Initialize views
        etSearchLocation = findViewById(R.id.etSearchLocation)
        listView = findViewById(R.id.locationListView)
        btnSetCurrentLocation = findViewById(R.id.btnSetCurrentLocation)

        locationList = listOf(
            "Una, Himachal Pradesh, India",
            "Hamirpur, Himachal Pradesh, India",
            "Dharamshala, Himachal Pradesh, India",
            "Nagrota, Himachal Pradesh, India",
            "Palampur, Himachal Pradesh, India",
            "Shimla, Himachal Pradesh, India",
            "Anu, Himachal Pradesh, India",
            "Bhota, Himachal Pradesh, India",
            "Nangal, Punjab, India"
        )

        adapter = ArrayAdapter(this, R.layout.list_item_location, R.id.tvLocationName, locationList)
        listView.adapter = adapter

        // Handle list item click
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedLocation = adapter.getItem(position) ?: return@OnItemClickListener
            openMapActivity(selectedLocation)
        }

        // Handle "Set Current Location" button click
        btnSetCurrentLocation.setOnClickListener {
            openMapActivity("Current Location (GPS)")
        }

        // Filter locations as user types
        etSearchLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterLocations(s.toString())
            }
        })
    }

    private fun openMapActivity(location: String) {
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("SELECTED_LOCATION", location)
        startActivity(intent)
    }

    private fun filterLocations(query: String) {
        val filteredList = locationList.filter { it.contains(query, ignoreCase = true) }
        adapter.clear()
        adapter.addAll(filteredList)
        adapter.notifyDataSetChanged()
    }
}
