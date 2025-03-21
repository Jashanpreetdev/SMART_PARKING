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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Default locations with location emoji
        locationList = listOf(
            " Una, Himachal Pradesh, India",
            " Hamirpur, Himachal Pradesh, India",
            " Dharamshala, Himachal Pradesh, India",
            " Nagrota, Himachal Pradesh, India",
            " Palampur, Himachal Pradesh, India",
            " Shimla, Himachal Pradesh, India",
            " Anu, Himachal Pradesh, India",
            " Bhota, Himachal Pradesh, India",
            " Nangal, Punjab, India"
        )

        val listView = findViewById<ListView>(R.id.locationListView)
        val btnSetCurrentLocation = findViewById<Button>(R.id.btnSetCurrentLocation)
        etSearchLocation = findViewById(R.id.etSearchLocation)

        // Set up adapter with custom layout
        adapter = ArrayAdapter(this, R.layout.list_item_location, R.id.tvLocationName, locationList)
        listView.adapter = adapter

        // Handle list item click
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedLocation = adapter.getItem(position) ?: return@OnItemClickListener
            sendLocationResult(selectedLocation)
        }

        // Handle "Set Current Location" button click
        btnSetCurrentLocation.setOnClickListener {
            sendLocationResult("📍 Current Location (GPS)")
        }

        // Implement search functionality
        etSearchLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterLocations(s.toString())
            }
        })
    }

    private fun sendLocationResult(location: String) {
        val intent = Intent()
        intent.putExtra("SELECTED_LOCATION", location)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun filterLocations(query: String) {
        val filteredList = locationList.filter { it.contains(query, ignoreCase = true) }
        adapter.clear()
        adapter.addAll(filteredList)
        adapter.notifyDataSetChanged()
    }
}