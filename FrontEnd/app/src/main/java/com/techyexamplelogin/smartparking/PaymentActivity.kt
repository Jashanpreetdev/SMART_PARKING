package com.techyexamplelogin.smartparking

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PaymentMethodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment) // ✅ matches your XML

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val topUpButton: Button = findViewById(R.id.topUpButton)
        val addCardButton: Button = findViewById(R.id.addCardButton)

        // Back button
        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Top Up button
        topUpButton.setOnClickListener {
            Toast.makeText(this, "Top up clicked", Toast.LENGTH_SHORT).show()
            // TODO: Implement top-up logic
        }

        // Add Card button
        addCardButton.setOnClickListener {
            Toast.makeText(this, "Add Card clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate or show dialog to add card
        }
    }
}
