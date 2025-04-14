package com.techyexamplelogin.smartparking

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var sharedPref: SharedPreferences
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var logoutBtn : Button

    private val PICK_IMAGE_REQUEST = 1  // Request code for image selection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize UI components
        profileImage = findViewById(R.id.profileImage)
        usernameTextView = findViewById(R.id.profileName)
        emailTextView = findViewById(R.id.profileEmail)
        phoneTextView = findViewById(R.id.profilePhone)
        bottomNavigation = findViewById(R.id.bottomNavigationView)
        logoutBtn= findViewById(R.id.logoutbttn)

        // Initialize SharedPreferences
        sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Load user data
        loadUserData()

        // Set up bottom navigation
        setupBottomNavigation()

        // Set up profile image click listener to open gallery
        profileImage.setOnClickListener {
            openGallery()
        }
        logoutBtn.setOnClickListener{
            val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
            sharedPref.edit().remove("TOKEN").apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loadUserData() {
        // Retrieve stored username, email, and phone
        val username = sharedPref.getString("USERNAME", "User") ?: "User"
        val email = sharedPref.getString("EMAIL", "Not Available") ?: "Not Available"
        val phone = sharedPref.getString("PHONE", "Not Available") ?: "Not Available"

        // Set text fields
        usernameTextView.text = username
        emailTextView.text = email
        phoneTextView.text = phone

        // Retrieve and set profile image (Unique for each user)
        val base64Image = sharedPref.getString("PROFILE_IMAGE_$username", null)
        val bitmap = base64ToBitmap(base64Image)
        if (bitmap != null) {
            profileImage.setImageBitmap(bitmap)
        } else {
            Log.e("ProfileActivity", "No valid image found for user: $username")
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_profile  // Highlight Profile icon

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, NavigationActivity::class.java))
                    overridePendingTransition(0, 0) // Prevents animation flicker
                    finish()
                    true
                }
                R.id.nav_add_space -> {
                    startActivity(Intent(this, AddSpaceActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_profile -> true  // Already in Profile, do nothing
                else -> false
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                profileImage.setImageBitmap(bitmap) // Update UI instantly
                saveImageToSharedPreferences(bitmap) // Save Image for this user
            }
        }
    }

    private fun saveImageToSharedPreferences(bitmap: Bitmap) {
        val username = sharedPref.getString("USERNAME", "default_user") ?: "default_user"

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream) // Use JPEG for better compression
        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

        sharedPref.edit().putString("PROFILE_IMAGE_$username", encodedImage).apply()
    }

    // Convert Base64 string to Bitmap
    private fun base64ToBitmap(base64String: String?): Bitmap? {
        if (base64String.isNullOrEmpty()) {
            Log.e("ProfileActivity", "Base64 string is null or empty")
            return null
        }

        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ProfileActivity", "Failed to decode Base64 string")
            null
        }
    }
}