package com.example.ch0203

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    //MainActivity가 생성될 시점엔 아직 뷰가 다 그려지기 않았기 때문에 lazy로 선언하여 뷰가 다 그려지고 난 후 (onCreate된 후)에 초기화 진행
    private val numberPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker1).apply {
            minValue = 0
            maxValue = 9
        }
    }

    private val numberPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker2).apply {
            minValue = 0
            maxValue = 9
        }
    }

    private val numberPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker3).apply {
            minValue = 0
            maxValue = 9
        }
    }

    private val openButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.openButton)
    }

    private val changePasswordButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.changePasswordButton)
    }

    // 패스워드 바꿀 때 로그인 할 수 없게 예외처리 하기 위한 전역변수
    private var changePasswordMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize
        numberPicker1
        numberPicker2
        numberPicker3

        openButton.setOnClickListener {

            if (changePasswordMode) {
                Toast.makeText(this, "비밀번호 변경 중입니다. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)

            val passwordFromUser =
                "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            if (passwordPreference.getString("password", "000").equals(passwordFromUser)) {
                //성공
                val intent = Intent(this, DiaryActivity::class.java)
                startActivity(intent)
            } else {
                showErrorAlertDialog()
            }
        }

        changePasswordButton.setOnClickListener {
            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser =
                "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
            if (changePasswordMode) {
                // 변경된 비밀번호 저장하는 기능
                // edit을 이용해서 에디터 만들 수 있다.
                // 저장할 때 commit과 apply 두가지 방식이 있다.
                // commit 은 파일 저장이 다 될 때까지 UI를 기다리는 기능, apply는 비동기적으로 저장 ( 여기선 commit써보자. )
                passwordPreference.edit(true) {
                    putString("password", passwordFromUser)
                }
                changePasswordMode = false
                changePasswordButton.setBackgroundColor(Color.BLACK)
            } else {
                // changePasswordMode가 활성화 + 비밀번호 맞는지 활성화
                if (passwordPreference.getString("password", "000").equals(passwordFromUser)) {
                    // 성공 부분 - 패스워드 변경으로 들어가는 부분
                    changePasswordMode = true
                    Toast.makeText(this, "변경할 패스워드를 입력해주세요. ", Toast.LENGTH_SHORT).show()
                    changePasswordButton.setBackgroundColor(Color.RED)
                } else {
                    showErrorAlertDialog()
                }
            }
        }

    }

    private fun showErrorAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("실패")
            .setMessage("비밀번호가 잘못되었습니다. ")
            .setPositiveButton("확인") { dialog, which -> }
            .create()
            .show()
    }
}