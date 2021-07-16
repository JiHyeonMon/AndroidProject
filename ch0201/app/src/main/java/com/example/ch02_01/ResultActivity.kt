package com.example.ch02_01

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)


        val height = intent.getIntExtra("height", -1)
        val weight = intent.getIntExtra("weight", -1)

        val bmi = weight / (height / 100.0).pow(2.0)

        val result = when {
            bmi >= 35 -> "고도비만"
            bmi >= 30 -> "증정도비만"
            bmi >= 25 -> "경도비만"
            bmi >= 23 -> "정상체중"
            else -> "저체중"
        }

        val result_txt_bmi: TextView = findViewById(R.id.result_txt_bmi)
        val result_txt_result: TextView = findViewById(R.id.result_txt_result)

        result_txt_bmi.text = bmi.toString()
        result_txt_result.text = result

    }
}