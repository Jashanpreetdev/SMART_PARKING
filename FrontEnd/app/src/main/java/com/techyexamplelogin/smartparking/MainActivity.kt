package com.techyexamplelogin.smartparking

import android.content.Intent
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body


class MainActivity : AppCompatActivity() {
    private fun SendOTP(email: String) {
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.isEnabled = false // Disable button
        btnRegister.alpha = 0.7f
        val apiService = RetrofitInstance.instance.create(ApiService::class.java)
        val emailreq =Email(email)
        apiService.sendOtp(emailreq).enqueue(object : Callback<OTPresponse> {
            override fun onResponse(call: Call<OTPresponse>, response: Response<OTPresponse>) {
                val isSuccess = response.body()?.success ?: false
                if (isSuccess) {
                    println("Signup successful: ${response.body()?.message}")
                    Toast.makeText(this@MainActivity, "OTP Sent Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, OTPVerificationActivity::class.java)
                    intent.putExtra("email", email) // Passing email for verification
                    startActivity(intent)
                    finish()
                } else {
                    println("Signup failed: ${response.errorBody()?.string()}")
                    Toast.makeText(this@MainActivity, "User Already Exist" , Toast.LENGTH_SHORT).show()
                    btnRegister.isEnabled = true // Disable button
                    btnRegister.alpha = 1f
                }
            }

            override fun onFailure(call: Call<OTPresponse>, t: Throwable) {
                println("Network error: ${t.message}")
                btnRegister.isEnabled = true // Disable button
                btnRegister.alpha = 1f
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize UI components
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvAlreadyRegistered = findViewById<TextView>(R.id.tvAlreadyRegistered)

        // Register button click
        btnRegister.setOnClickListener {
            // Optional: Change transparency to indicate it's disabled



            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                if (password.length < 8) {
                    Toast.makeText(this, "Password must be of atleast 8 characters!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (phone.length != 10) {
                    Toast.makeText(this, "Invalid Phone Number!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
//                RegisterUser()
                // Save user details in SharedPreferences
                SendOTP(email)
                val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("USERNAME", name)
                    putString("EMAIL", email)
                    putString("PHONE", phone)
                    putString("PASSWORD", password)
                    apply()
                }

                // Prevent going back to register page
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to Login Screen
        tvAlreadyRegistered.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}

