package com.techyexamplelogin.smartparking

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var etCode1: EditText
    private lateinit var etCode2: EditText
    private lateinit var etCode3: EditText
    private lateinit var etCode4: EditText
    private lateinit var btnResendCode: Button
    private lateinit var tvEmail: TextView
    private lateinit var tvChangeEmail: TextView

    private val correctOTP = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otp)


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
        if (enteredOTP == correctOTP) {
            Toast.makeText(this, "OTP Verified!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java)) // Change to next activity
            finish()
        } else {
            Toast.makeText(this, "Incorrect OTP! Try again.", Toast.LENGTH_SHORT).show()
            clearOTPFields()
        }
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