package com.techyexamplelogin.smartparking

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

class SplashActivity : AppCompatActivity() {

    private fun checkLogin() {
        val apiService = RetrofitInstance.instance.create(ApiService::class.java)
        val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", null)

        if (token.isNullOrEmpty()) {
            navigateToLogin()
            return
        }

        apiService.checkLoggedIn("Bearer $token").enqueue(object : Callback<UserLoggedIn> {
            override fun onResponse(call: Call<UserLoggedIn>, response: Response<UserLoggedIn>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SplashActivity, "User Logged In", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                } else {
                    Toast.makeText(this@SplashActivity, "Session Expired! Login Again.", Toast.LENGTH_SHORT).show()
                    sharedPref.edit().remove("TOKEN").apply()

                    navigateToLogin()
                }
            }

            override fun onFailure(call: Call<UserLoggedIn>, t: Throwable) {
                println("Network error: ${t.message}")
                sharedPref.edit().remove("TOKEN").apply()
                navigateToLogin()
            }
        })
    }

    private fun navigateToHome() {
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this, NavigationActivity::class.java))
            finish()
        }, 500) // Delay for 2 seconds
    }

    private fun navigateToLogin() {
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 500) // Delay for 2 seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay splash screen and check login
        Handler(mainLooper).postDelayed({
            checkLogin()
        }, 500) // Show splash screen for 1.5 seconds before checking login
    }
}
