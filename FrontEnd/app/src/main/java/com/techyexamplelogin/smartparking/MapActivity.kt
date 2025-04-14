package com.techyexamplelogin.smartparking

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var pointAnnotationManager: PointAnnotationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)

        mapView.mapboxMap.loadStyle(Style.STANDARD) { style ->

            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_blue_marker)
            style.addImage("marker-default", bitmap)

            val annotationPlugin = mapView.annotations
            pointAnnotationManager = annotationPlugin.createPointAnnotationManager()
            val latString = intent.getStringExtra("lat")
            val longString = intent.getStringExtra("long")
            val lat = latString?.toDoubleOrNull()
            val lon = longString?.toDoubleOrNull()
            if (lat != null && lon != null) {
                mapView.mapboxMap.setCamera(
                    com.mapbox.maps.CameraOptions.Builder()
                        .center(Point.fromLngLat(lon, lat))
                        .zoom(11.0)
                        .build()
                )
            }
            fetchParkingLocations(longString,latString)
        }
    }

    private fun fetchParkingLocations(long: String?,lat:String?,) {
        val service = RetrofitInstance.instance.create(ApiService::class.java)
        val startDate = intent.getStringExtra("startDate")
        val startTime = intent.getStringExtra("startTime")
        val data = getParking(long, lat,startDate,startTime) // Sample or use real-time user location
        service.getParking(data).enqueue(object : Callback<parkingResponse?> {
            override fun onResponse(
                call: Call<parkingResponse?>,
                response: Response<parkingResponse?>
            ) {
                if (response.isSuccessful) {
                    val parkingList = response.body()?.data ?: emptyList()
                    for (parking in parkingList) {
                        addMarker(parking)
                    }
                } else {
                    Toast.makeText(this@MapActivity, "Failed to load markers", Toast.LENGTH_SHORT).show()
                    Log.e("MAP_DEBUG", "API Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<parkingResponse?>, t: Throwable) {
                Toast.makeText(this@MapActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("MAP_DEBUG", "Network Error", t)
            }
        })
    }

    private fun addMarker(parking: AddParking) {
        val lat = parking.coordinates.latitude?.toDoubleOrNull()
        val lon = parking.coordinates.longitude?.toDoubleOrNull()

        if (lat != null && lon != null) {
            val point = Point.fromLngLat(lon, lat)
            val marker = pointAnnotationManager.create(
                PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage("marker-default")
            )

            pointAnnotationManager.addClickListener {
                if (it == marker) {
                    showBottomSheet(parking.street, parking.city, parking.pincode)
                    true
                } else false
            }

            Log.d("MAP_DEBUG", "Marker added at: $lat, $lon")
        } else {
            Log.w("MAP_DEBUG", "Invalid coordinates for: ${parking.street}")
        }
    }

    private fun showBottomSheet(street: String, city: String, pincode: String) {
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null)
        // Update if phone is added in API
        dialog.setContentView(view)
        dialog.show()
    }
}
