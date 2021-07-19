package com.example.ch0203

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity : AppCompatActivity() {

    // MainLooper를 넣어 MainThread와 연결하는 핸들러 생성
    private val handler = Handler(Looper.getMainLooper())

    private val diaryEditText: EditText by lazy {
        findViewById<EditText>(R.id.diaryEditText)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val detailPreference = getSharedPreferences("diary", Context.MODE_PRIVATE)
        diaryEditText.setText(detailPreference.getString("detail", ""))

        // diaryEditText.addTextChangedListener 를 쓸 경우 한 글자 수정될 때마다 저장된다.
        // 이것 대신에 잠깐 멈칫 했을 때, 저장되기 위해 스레드 기능을 구현해볼 것이다.

        val runnable = Runnable {
            // commit 방식 아닌 apply 방식으로 비동기 처리를 한다.
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit {
                putString("detail", diaryEditText.text.toString())
            }
        }

        diaryEditText.addTextChangedListener {
            // 메인 스레드 (UI스레드) 와 아닌 스레드 사이 연결시 핸들러가 필요하다.
            //0.5초 이전에 아직 실행되지 않고 pending 되어있는 RUNNABLE이 있다면 지워주기 위해서
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 500)
        }
    }
}