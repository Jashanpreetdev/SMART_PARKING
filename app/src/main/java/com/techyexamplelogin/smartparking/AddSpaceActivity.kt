package com.techyexamplelogin.smartparking

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class AddSpaceActivity : AppCompatActivity() {

    private lateinit var fromTime: EditText
    private lateinit var untilTime: EditText
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_space)

        // Initialize views
        fromTime = findViewById(R.id.fromTime)
        untilTime = findViewById(R.id.untilTime)
        bottomNavigation = findViewById(R.id.bottomNavigationView)

        // Setup time pickers
        setupTimePicker(fromTime)
        setupTimePicker(untilTime)

        // Setup bottom navigation
        setupBottomNavigation()
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

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_add_space  // Highlight Add Space icon

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, NavigationActivity::class.java))
                    overridePendingTransition(0, 0) // Smooth transition
                    finish()
                    true
                }
                R.id.nav_add_space -> true  // Already in Add Space, do nothing
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