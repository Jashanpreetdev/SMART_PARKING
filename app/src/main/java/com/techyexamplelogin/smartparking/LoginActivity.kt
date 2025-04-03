package com.techyexamplelogin.smartparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private fun HangleLogin(email:String,password: String) {
        val apiService = RetrofitInstance.instance.create(ApiService::class.java)
        val user = LoginData(email, password)

        apiService.loginUser(user).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val responseData = response.body()?.data
                    val sharedPrefUser = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    with(sharedPrefUser.edit()) {
                        putString("USERNAME", response.body()?.data?.user?.name)
                        putString("EMAIL", response.body()?.data?.user?.email)
                        putString("PHONE", response.body()?.data?.user?.phone)
                        apply()
                    }


                    responseData?.token?.let { token ->
                        val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("TOKEN", token)
                            apply()
                        }
                        Toast.makeText(this@LoginActivity, "User Verified!", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this@LoginActivity, NavigationActivity::class.java))
                        finish()
                    } ?: run {
                        Toast.makeText(
                            this@LoginActivity,
                            "Token not received!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    println("Signup failed: ${response.errorBody()?.string()}")
                    Toast.makeText(
                        this@LoginActivity,
                        "Incorrect Credentials! Try again.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                println("Network error: ${t.message}")
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI components
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Login button click listener
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Placeholder for actual authentication logic
                HangleLogin(email,password)
            } else {
                Toast.makeText(this, "Please enter email and password!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}