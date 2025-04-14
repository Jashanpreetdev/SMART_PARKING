package com.techyexamplelogin.smartparking

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body


class OTPVerificationActivity : AppCompatActivity() {

    private fun registerUser(name:String,email:String,phone: String,password: String,otp:String) {
        val apiService = RetrofitInstance.instance.create(ApiService::class.java)
        val user =UserData(name,email,phone,password,otp,"")

        apiService.signUpUserData(user).enqueue(object : Callback<UserResponseAuth> {
            override fun onResponse(call: Call<UserResponseAuth>, response: Response<UserResponseAuth>) {
                if (response.isSuccessful) {
                    val responseData = response.body()?.data

                    responseData?.token?.let { token ->
                        val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("TOKEN", token)
                            putString("ID", responseData.user._id)
                            apply()
                        }
                        Toast.makeText(this@OTPVerificationActivity, "OTP Verified!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@OTPVerificationActivity, NavigationActivity::class.java))
                        finish()
                    } ?: run {
                        Toast.makeText(this@OTPVerificationActivity, "Token not received!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    println("Signup failed: ${response.errorBody()?.string()}")
                    Toast.makeText(this@OTPVerificationActivity, "Incorrect OTP! Try again.", Toast.LENGTH_SHORT).show()
                    clearOTPFields()
                }
            }

            override fun onFailure(call: Call<UserResponseAuth>, t: Throwable) {
                println("Network error: ${t.message}")
            }
        })
    }

    private lateinit var etCode1: EditText
    private lateinit var etCode2: EditText
    private lateinit var etCode3: EditText
    private lateinit var etCode4: EditText
    private lateinit var btnResendCode: Button
    private lateinit var tvEmail: TextView
    private lateinit var tvChangeEmail: TextView

     // Replace with actual OTP logic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otp)

        // Initialize UI elements
        etCode1 = findViewById(R.id.etCode1)
        etCode2 = findViewById(R.id.etCode2)
        etCode3 = findViewById(R.id.etCode3)
        etCode4 = findViewById(R.id.etCode4)
        btnResendCode = findViewById(R.id.btnResendCode)
        tvEmail = findViewById(R.id.tvEmail)
        tvChangeEmail = findViewById(R.id.tvChangeEmail)

        setupOTPInputs()

        // Resend OTP Button Click Listener
        btnResendCode.setOnClickListener {
            resendOTP()
        }

        // Change Email Click Listener
        tvChangeEmail.setOnClickListener {
            Toast.makeText(this, "Change Email Clicked!", Toast.LENGTH_SHORT).show()
            // Handle email change functionality
        }
    }

    private fun setupOTPInputs() {
        val editTexts = arrayOf(etCode1, etCode2, etCode3, etCode4)

        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus() // Move to next field
                        } else {
                            // If last field is filled, check OTP
                            checkOTP()
                        }
                    } else if (s?.isEmpty() == true) {
                        if (i > 0) {
                            editTexts[i - 1].requestFocus() // Move to previous field on delete
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun checkOTP() {
        val enteredOTP = getEnteredOTP()
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val name = sharedPref.getString("USERNAME", "User") ?: "User"
        val email = sharedPref.getString("EMAIL", "Email") ?: "User"
        val password = sharedPref.getString("PASSWORD", "Password") ?: "User"
        val phone = sharedPref.getString("PHONE", "Phone") ?: "User"

        registerUser(name,email,phone, password,enteredOTP)
//
    }

    private fun getEnteredOTP(): String {
        return etCode1.text.toString() +
                etCode2.text.toString() +
                etCode3.text.toString() +
                etCode4.text.toString()
    }

    private fun clearOTPFields() {
        etCode1.text.clear()
        etCode2.text.clear()
        etCode3.text.clear()
        etCode4.text.clear()
        etCode1.requestFocus()
    }

    private fun resendOTP() {
        Toast.makeText(this, "OTP Resent to ${tvEmail.text}", Toast.LENGTH_SHORT).show()
        // Implement OTP resend logic (API Call, Firebase, etc.)
    }
}