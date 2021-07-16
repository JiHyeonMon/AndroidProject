package com.example.ch02_01

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val height: EditText = findViewById(R.id.main_edit_height)
        val weight = findViewById<EditText>(R.id.main_edit_weight)

        val resultButton = findViewById<Button>(R.id.main_btn_result)

        resultButton.setOnClickListener {
            if (height.text.isEmpty() || weight.text.isEmpty()) {
                Toast.makeText(this, "입력해주세요. ", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("height", height.text.toString().toInt())
            intent.putExtra("weight", weight.text.toString().toInt())
            startActivity(intent)
        }
    }
}