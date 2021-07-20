package com.example.ch0204

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import com.example.ch0204.model.HistoryModel
import java.lang.NumberFormatException

class MainActivity : AppCompatActivity() {

    private val expressionTextView: TextView by lazy {
        findViewById<TextView>(R.id.expressionTextView)
    }

    private val resultTextView: TextView by lazy {
        findViewById<TextView>(R.id.resultTextView)
    }

    private val historyLayout: View by lazy {
        findViewById<View>(R.id.historyLayout)
    }

    private val historyLinearLayout: LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.historyLinearLayout)
    }

    lateinit var db: AppDatabase

    private var isOperator = false
    private var hasOperator = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB")
            .build()
    }


    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")
            R.id.buttonDivide -> operatorButtonClicked("/")
            R.id.buttonModulo -> operatorButtonClicked("%")
            R.id.buttonMulti -> operatorButtonClicked("*")
            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMinus -> operatorButtonClicked("-")
        }

    }

    private fun numberButtonClicked(number: String) {

        if (isOperator) {
            expressionTextView.append("  ")
        }
        isOperator = false

        val expressionText = expressionTextView.text.split(" ")
        if (number == "0" && expressionText.last().isEmpty()) {
            Toast.makeText(this, "0은 제일 먼저 올 수 없습니다.  ", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionText.isNotEmpty() && expressionText.last().length >= 15) {
            Toast.makeText(this, "15자리까지만 사용할 수 있습니다. ", Toast.LENGTH_SHORT).show()
            return
        }

        // 정상
        expressionTextView.append(number)

        // resultTextView 실시간으로 계산 결과를 넣어야하는 기능
        resultTextView.text = calculateExpression()


    }

    private fun operatorButtonClicked(operator: String) {

        if (expressionTextView.text.isEmpty()) {
            return
        }

        when {

            // 이미 연산자 있는데 연산자 누를 경우 :: 기존 연산자 지우고 새로 누른 연산자 넣는다.
            isOperator -> {
                val text = expressionTextView.text.toString()
                expressionTextView.text = text.dropLast(1) + operator
            }
            hasOperator -> {
                Toast.makeText(this, "연산자는 한번만 처리할 수 있습니다. ", Toast.LENGTH_SHORT).show()
                return
            }

            else -> expressionTextView.append(" $operator")

        }

        // expressionTextView에서 연산자만 색깔을 다르게 칠하고 싶을 때 SpannableStringBuilder를 이용.
        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.text.length - 1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // ssb로 새로 나온 값을 다시 editText에 넣어준다.
        expressionTextView.text = ssb
        isOperator = true
        hasOperator = true
    }

    private fun calculateExpression(): String {
        val expressionTexts = expressionTextView.text.split(" ")
        if (hasOperator.not() || expressionTexts.size != 3) {
            return ""
        } else if (expressionTexts[0].isNumber().not() || expressionTexts[1].isNumber().not()) {
            return ""
        }

        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val op = expressionTexts[1]

        return when (op) {
            "+" -> "$exp1+$exp2"
            "-" -> "$exp1-$exp2"
            "*" -> "$exp1*$exp2"
            "/" -> "$exp1/$exp2"
            "%" -> "$exp1%$exp2"
            else -> ""
        }
    }

    fun resultButtonClicked(v: View) {
        val expressionTexts = expressionTextView.text.split(" ")
        Log.e("ee", expressionTexts.toString())

        if (expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            return
        }

        if (expressionTexts.size != 3 && hasOperator) {
            Toast.makeText(this, "아직 완성되지 않은 수식입니다. ", Toast.LENGTH_SHORT).show()
            return
        }

        if (expressionTexts[0].isNumber().not() || expressionTexts[1].isNumber().not()) {
            Toast.makeText(this, "오류가 발생했습니다. ", Toast.LENGTH_SHORT).show()
            return
        }

        // 실제 계산
        val expressionText = expressionTextView.text.toString()
        val resultText = calculateExpression()

        resultTextView.text = ""
        expressionTextView.text = resultText

        // DB에 넣어주는 부분
        // MainThread가 아니라 새로운 thread에서 작업해야 한다.
        Thread(Runnable {
            db.historyDao().insertHistory(HistoryModel(null, expressionText, resultText))
        }).start()

        isOperator = false
        hasOperator = false
    }

    fun clearButtonClicked(v: View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }

    fun historyButtonClicked(v: View) {
        historyLayout.isVisible = true

        // history를 DB에서 꺼내와서 하나하나 LinearLayout에 넣기 전에 한번 싹 지워준다. (LinearLyaout 하위의 뷰들이 삭제가 된다. )
        historyLinearLayout.removeAllViews()
        //  DB에서 모든 기록 가져오기

        // 최신 순으로 보여주기 위해 reversed()함수를 통해 한번 뒤집는다.
        // 하나하나 꺼내와서 작업하기 위해 forEach{} 람다식 사용
        // View에 기능 할당하기 - 그러나 아래 스레드는 메인 스레드가 아니다. 뷰를 수정하려면 메인 스레드 할당이 필요하다. --> runOnUiThread{}를 통해 UiThread연다.
        Thread(Runnable {
            db.historyDao().getAll().reversed().forEach {
                runOnUiThread {
                    // LayoutInfater를 이용하여 View를 하나 만들어보자.
                    val historyView = LayoutInflater.from(this).inflate(R.layout.history_row, null, false)
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "= $it.result"

                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()

    }

    fun closeHistoryButtonClicked(v: View) {
        // historyLayout visibility를 gone으로 바꿔주면 된다.
        historyLayout.isVisible = false

    }

    fun historyClearButtonClicked(v:View) {
        // View에서 모든 기록 삭제
        historyLinearLayout.removeAllViews()

        // DB에서 모든 기록 삭제
        Thread(Runnable {
            db.historyDao().deleteAll()
        }).start()
    }
}

// String에 대한 확장 함수
fun String.isNumber(): Boolean {
    return try {
        this.toBigInteger()
        true
    } catch (e: NumberFormatException) {
        false
    }
}