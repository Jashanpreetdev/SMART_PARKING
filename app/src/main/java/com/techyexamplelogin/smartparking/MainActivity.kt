package com.techyexamplelogin.smartparking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.techyexamplelogin.smartparking.R

class MainActivity : AppCompatActivity() {


    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvAlreadyRegistered: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvAlreadyRegistered = findViewById(R.id.tvAlreadyRegistered)


        tvAlreadyRegistered.setOnClickListener {
            // Perform action when clicked (e.g., show a Toast or navigate to another activity)
            Toast.makeText(this, "Already registered clicked", Toast.LENGTH_SHORT).show()
        }


        btnRegister.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()


            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()


                etName.setText("")
                etEmail.setText("")
                etPhone.setText("")
                etPassword.setText("")
            }

        }
    }
}
