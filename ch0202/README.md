# 로또 추첨기 
<img src = "https://user-images.githubusercontent.com/50662636/126033878-30653fb9-9b97-44b9-96fb-73df280eacd5.png" width = "400" height = "650"/>

- ### ConstraintLayout
제약 조건을 통해서 뷰들을 배치

```
app:layout_constraintEnd_toEndOf="parent"
app:layout_constraintStart_toStartOf="parent"
app:layout_constraintTop_toTopOf="parent

// chainStyle - packed을 줌으로써 가운데 모임
app:layout_constraintHorizontal_chainStyle="packed"

```

- ### NumberPicker
범위 설정 - 범위 설정해야 사용 가능 
```
numberPicker.minValue = 1
numberPicker.maxValue = 45
```
값 가져오기  
```
val value = numberPicker.value
```

- ### ShapeDrawable
```
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <solid
        android:color="#b0d840"/>
    <size
        android:width="44dp"
        android:height="44dp"/>
</shape>
```

background로 drawable넣을 때
```
//안드로이드 내부 resource파일 사용하기에 Context를 쓴다. - ContextCompat에 getDrawable을 이용하여 drawable 설정
textView.background = ContextCompat.getDrawable(this, R.drawable.circle_grey)
```
- # Kotlin
- ### apply
mutableList설정하는 부분에서 apply사용
apply를 사용했던 객체 자체를 this로 블럭에서 사용할 수 있기에 주로 설정, 초기화 때 사용
```
      val numberList = mutableListOf<Int>().apply {
          for (i in 1..45) {
              if (pickNumberSet.contains(i)) {
                  continue
              }
              this.add(i)
          }
      }
```

- ### when
when문의 결과과 바로 return으로 나가는 expression으로 사용
in 연산자를 통해 범위로 check
```
    when (number) {
        in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yello)
        in 11..20 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
        in 21..30 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
        in 31..40 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_grey)
        else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
    }
```
- ### Random
Random()함수를 쓰면 seed를 추가하면 해당 seed로 랜덤 알고리즘을 돌려 같은 seed면 같은 값 나올 수 있다. 
그냥 Random()을 사용하면 나노세컨의 시간이 seed값으로 들어가기에 같은 결과가 나올리 거의 없다고 볼 수 있다. 

- ### Collection - Set, List
Set은 중복된 데이터가 들어가지 않는 자료형
List는 순차적으로 데이터가 쌓이는 자료형

- # Code
<details>
  <summary>'번호 추가하기' 버튼</summary>
  
  ```
  // 상단 초기화 
    private val addButton: Button by lazy {
        findViewById(R.id.addButton)
    }
  
  //번호 추가하기 버튼 동작
    private fun initAddButton() {
        addButton.setOnClickListener {
            if (didRun) {
                Toast.makeText(this, "초기화 후에 시도해주세요. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pickNumberSet.size >= 6) {
                Toast.makeText(this, "번호는 여섯 개까지 선택할 수 있습니다. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택한 번호입니다. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textView = numberTextViewList[pickNumberSet.size]
            textView.isVisible = true
            textView.text = numberPicker.value.toString()

            setNumberBackground(numberPicker.value, textView)

            pickNumberSet.add(numberPicker.value)

        }
    }
  ```
</details>
<details>
  <summary>'초기화' 버튼</summary>
  
  ```
  // 상단 초기화 
  private val clearButton: Button by lazy {
          findViewById(R.id.clearButton)
      }
  
  //초기화 버튼 동작
  private fun initClearButton() {
    clearButton.setOnClickListener {
        pickNumberSet.clear()
        numberTextViewList.forEach {
            it.isVisible = false
        }
        didRun = false

      }
  }
  ```
</details>
<details>
  <summary>'자동 생성 시작' 버튼</summary>
  
  ```
  // 상단 초기화 
    private val runButton: Button by lazy {
        findViewById(R.id.runButton)
    }
  
  //자동 추첨 버튼 동작
    private fun initRunButton() {
        runButton.setOnClickListener {
            didRun = true
            val list = getRandomNumber()

            list.forEachIndexed { index, number ->
                val textView = numberTextViewList[index]
                textView.text = number.toString()
                textView.isVisible = true
                setNumberBackground(number, textView)

            }
        }
    }
  
  //번호 가져오기
    private fun getRandomNumber(): List<Int> {
        val numberList = mutableListOf<Int>().apply {
            for (i in 1..45) {
                if (pickNumberSet.contains(i)) {
                    continue
                }
                this.add(i)
            }
        }

        numberList.shuffle()

        val newList = pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size)
        return newList.sorted()
    }
  ```
</details>
