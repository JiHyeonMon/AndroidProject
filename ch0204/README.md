# 계산기

<img src = "https://user-images.githubusercontent.com/50662636/126312983-b6389f6e-053c-4ed3-ad32-595722ea12b1.png" width = "300" height = "500"/><img src = "https://user-images.githubusercontent.com/50662636/126313017-6eb4e1ca-3bca-43b7-9544-2d9ea0c9155b.png" width = "300" height = "500"/>

- ### TableLayout

  layout_constraintVertical_weight을 통해 레이아웃 비율을 정할 수 있다.

tableLayout 속성으로 shrinkColumns를 \*(별) 로 줌으로써 버튼들의 길이가 밖으로 삐져나가지 않고 균일한 크기로 나눠갖게 됨.
shrinkColumns="\*(별)"

버튼 눌렀을 때 약간 들어가는 효과 == ripple
shapeDrawable 설정할 때 ripple 설정

```
// 눌렀을 때 리플 값
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/buttonPressGrey">

    <item android:id="@android:id/background">
        <shape android:shape="rectangle">
            <solid android:color="@color/buttonGrey" />
            <corners android:radius="100dp" />
            <stroke
                android:width="1dp"
                android:color="@color/buttonPressGrey" />
        </shape>
    </item>
</ripple>
```

xml에서 같은 버튼 여러 개를 나중에 코드에서 하나하나 onClickListener달기 어렵 --> onClick 속성과 함수명을 주면 함수에서 component의 id로 찾는다.

```
android:onClick="buttonClicked"
```

android button클릭시 튀어나와 보이는 애니메이션 기본으로 있는데 없애고 싶다면 아래 코드.

```
android:stateListAnimator="@null"
```

- ### Room
  HistoryModel에 명시된 data class자체를 테이블로 사용하기 위해서 historyClass위에 Entity라는 Annotation추가 --> 이전에 Gradle가서 Room을 사용하기 위한 import 해준다.

```

    //상단 plugin
    id 'kotlin-kapt'

    // Dependency에 아래 추가
    implementation 'androidx.room:room-runtime:2.3.0'
    kapt 'androidx.room:room-compiler:2.3.0'

```

각각의 변수들도 DB에 어떻게 저장이 될지 명시를 해야하기에 unique변수인 uid에는 PrimaryKey를, 다른 변수에는 ColumnInfo Annotation을 추가해준다.

```
@Entity
data class HistoryModel(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "expression") val expression: String?,
    @ColumnInfo(name = "result")val result: String?
)
```

다음으로 DAO File을 만들어보자.

Dao 파일에 interface를 정의해줘야한다.

이 dao에 historyEntity 저장을 어떻게 하고, 지우는지 정의를 한다.

우선 history를 전부 가져오는 코드를 만들어보자.
dao에서 바로 query문을 작성하면 된다.

아래와 같이 우리가 사용할 함수들을 구현.

```
@Dao
interface HistoryDao{
    @Query("SELECT * FROM HISTORYMODEL")
    fun getAll(): List<HistoryModel>

    @Insert
    fun insertHistory(history: HistoryModel)

    @Query("DELETE FROM HistoryModel")
    fun deleteAll()
}
```

추가로 Dao를 사용하고 싶을 때, 아래와 같은 방식으로 사용할 수 있다.

```
@Dao
interface HistoryDao{
  // 특정 history 삭제
    @DELETE
    fun delete(history: HistoryModel)

  // result 결과 값을 갖는 history만 반환하는 함수
    @Query("SELECT * FROM  HistoryModel WHERE result LIKE :result")
    fun findByResult(result: String): List<HistoryModel>

  // result 결과 값을 갖는 history만 반환하는 함수 - 하나만 반환하고 싶을 때
    @Query("SELECT * FROM  HistoryModel WHERE result LIKE :result LIMIT 1")
    fun findByResult(result: String): HistoryModel

}
```

이제 실제 데이터베이스 파일을 만들어보자 .

파일 상단 annotation으로 Database를 명시해주고 어떤 entitity를 사용하는지 적어줘야 한다. 우리는 HistoryModel class를 리스트로 사용하고 버전은 1로 설정한다. (이후 구조 바뀔 시 버전 업데이트 하며 마이그레이션시 필요)

```
@Database(entities = [HistoryModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    // AppData를 생성할 때 historyDao를 가져와서 사용할 수 있게 됨.
    abstract fun historyDao(): HistoryDao

}
```

이후 MainActivity에서

```
    // 전역 변수로 db 선언
    lateinit var db: AppDatabase


    // onCreate 이후
    db = Room.databaseBuilder(
          applicationContext,
          AppDatabase::class.java,
          "historyDB")
        .build()
```

1. AppDatabase 라는 abstract class를 만들어서 데이터 베이스를 먼저 선언
2. 거기 들어가는 Dao를 구현. 이 Dao는 interface형식으로 구현 (Annotation DAO) - 이 Dao에는 각각의 Query를 실행할 수 있는 메소드를 넣을 수 있다. (Query를 통해서 Room에서 알아서 데이터 가져올 수 있다. )
3. Room에 사용되는 모델 Entitiy를 DataClass형식으로 구현 (Annotation Entity)

- ### Thread, runOnUiThread, LayoutInfrator

Thread 사용법은 Thread를 선언하고 인자로 Runnable 구현자를 넣고 start() 해주면 된다.

Thread에서는 네트워크 작업이나 DB작업과 같이 메인 스레드에서 기에 무거운 작업을 새로운 스레드 만들어 작업한다.

Thread 작업이 끝나고 View에 뭔가 다시 그려야 할 때는 runOnUiThread를 사용해 UiThread에서 작업을 진행한다.

View에 DB에서 가져온 값 할당하기 - 그러나 아래 스레드는 메인 스레드가 아니다.

뷰를 수정하려면 메인 스레드 할당이 필요하다. --> runOnUiThread{}를 통해 UiThread연다.

```
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
```

Handler는 스레드와 스레드 간에 통신을 하는 방법

- ### Theme

- # Kotlin
- ### 확장함수 사용하기
  클래스 이름 + . + 확장함수 이름으로 명시
  마치 이 함수가 이 클래스에 있었던 것처럼 사용 가능

```
// String에 대한 확장 함수
fun String.isNumber(): Boolean {
    return try {
        this.toBigInteger()
        true
    } catch (e: NumberFormatException) {
        false
    }
}
```
