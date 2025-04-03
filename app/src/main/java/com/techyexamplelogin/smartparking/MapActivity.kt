package com.techyexamplelogin.smartparking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)

        // Load the map style
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)

        // Get the selected location from intent
        val selectedLocation = intent.getStringExtra("SELECTED_LOCATION")
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()  // ✅ Only call these if you're using MapView lifecycle handling
    }

    override fun onResume() {
        super.onResume()
        mapView.getMapboxMap() // ✅ No direct onResume() method for MapView
    }

    override fun onPause() {
        super.onPause()
        mapView.getMapboxMap() // ✅ No direct onPause() method for MapView
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

}
