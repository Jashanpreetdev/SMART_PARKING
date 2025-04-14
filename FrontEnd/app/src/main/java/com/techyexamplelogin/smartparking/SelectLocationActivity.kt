package com.techyexamplelogin.smartparking

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.gestures.gestures
import java.util.*
import android.location.Address
import android.widget.SearchView


class SelectLocationActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var pointAnnotationManager: PointAnnotationManager

    private var currentMarker: PointAnnotation? = null
    private val LOCATION_PERMISSION_CODE = 1001
    private fun searchLocation(query: String) {
        try {
            val addresses = geocoder.getFromLocationName(query, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val lat = address.latitude
                val lng = address.longitude
                val point = Point.fromLngLat(lng, lat)

                mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(point)
                        .zoom(15.0)
                        .build()
                )
                updateMarker(point)
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Search error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchLocation(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })



        mapView = findViewById(R.id.mapView)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        mapboxMap = mapView.mapboxMap

        mapboxMap.loadStyle(Style.MAPBOX_STREETS) { style ->
            // Style is loaded
            Log.d("Mapbox", "Style loaded")

            // Example: Add a marker image if needed


            // Now safe to initialize annotations and listeners
            pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

            initMapClickListener()
            checkLocationPermission()
        }

    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        } else {
            getCurrentLocation()
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val point = Point.fromLngLat(location.longitude, location.latitude)
                mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(point)
                        .zoom(15.0)
                        .build()
                )
                updateMarker(point)
            } else {
                Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMarker(point: Point) {
        Log.d("Mapbox", "Updating marker at: ${point.latitude()}, ${point.longitude()}")
        currentMarker?.let { pointAnnotationManager.delete(it) }

        val markerOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage("marker-15") // use the key you registered in style
            .withIconSize(1.5)

        currentMarker = pointAnnotationManager.create(markerOptions)
    }
    private fun sendLocationResult(address: Address,point: Point) {
        val fullAddress = address.getAddressLine(0) ?: ""
        val city = address.locality ?: ""
        val pin = address.postalCode ?: ""

        val resultIntent = Intent().apply {
            putExtra("lat",point.latitude().toString())
            putExtra("long",point.longitude().toString())
            putExtra("address", fullAddress)
            putExtra("city", city)
            putExtra("pin", pin)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }


    private fun reverseGeocode(lat: Double, lng: Double,point: Point) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (addresses.isNotEmpty()) {
                        sendLocationResult(addresses[0],point)
                    } else {
                        Toast.makeText(this@SelectLocationActivity, "No address found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(errorMessage: String?) {
                    Toast.makeText(this@SelectLocationActivity, "Geocoding error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            try {
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    sendLocationResult(addresses[0],point)
                } else {
                    Toast.makeText(this, "No address found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Geocoding failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun initMapClickListener() {
        mapView.gestures.addOnMapClickListener { point ->
            updateMarker(point)
            reverseGeocode(point.latitude(), point.longitude(),point)
            true
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
