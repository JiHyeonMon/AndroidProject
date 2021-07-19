# 비밀 다이어리

<img src = "https://user-images.githubusercontent.com/50662636/126094215-fa5cfa2a-9e72-4e79-9928-c2922319afaf.png" width = "300" height = "500"/><img src = "https://user-images.githubusercontent.com/50662636/126094224-baf577fb-204c-487d-b77f-6040cce2cb05.png" width = "300" height = "500"/>

<img src = "https://user-images.githubusercontent.com/50662636/126094256-f2b558f9-2763-42a9-a7c2-831c0bd42a33.png" width = "300" height = "500"/><img src = "https://user-images.githubusercontent.com/50662636/126094266-277ea069-ab92-4556-bf74-60b444d42523.png" width = "300" height = "500"/>

다이어리 처럼 UI 꾸며보기

비밀번호를 저장하는 기능, 변경하는 기능

다이어리 내용을 앱이 종료되더라도 기기에 저장하는 기능

- ### SharedPreference
  저장하는 방법이 두 가지 - Local DB에 저장하는 방식, File에다 저장하는 방식

파일 중 preference 파일을 좀 더 쉽게 관리 해주는 게 sharedPreference이다.

getSharedPreference() 함수를 통하여 저장된 값을 가져올 수 있다.

인자로 name: String, mode: Int 두 가지를 갖는다.

name은 파일의 이름, mode는 Context.MODE_PRIVATE

SharedPreference는 말 그대로 preference파일을 다른 데서 사용할 수 있게 share해주는 것.

우리는 Password를 다른 앱과 공유하지 않고 우리 앱에서만 사용하길 원하기에 MODE_PRIVATE으로 우리 앱에서만 사용할 수 있게 선언.

sharedPreference 값을 가져올 때는

```
sharedPreference.getString("키 값", "디폴트")
```

sharedPreference 값을 저장할 때는 KTX function형식으로 저장

```
sharedPreference.edit {
    purString()
}
```

저장에는 두가지 방법이 있다. commit과 apply

commit은 UI thread를 잠깐 block하고 실제 데이터가 다 저장될 때까지 기다리는 기능 (화면이 멈추는 것이기에 너무 무거운 작업은 하지 않는 것이 좋다. )

apply는 기다리지 않고 비동기적으로 처리.

- ### Handler
  스레드와 스레드간 통신, 스레드들을 엮어주는 안드로이드에서 제공하는 기능.

스레드는 아래와 같은 방식으로 만든다.

```
        val t = Thread(Runnable{
        Log.e("aa", "aa")

            // 네트워크 작업
            textview.text = "aaa" --> 안됨.

            runOnUiThread{
                textview.text = "aaa"
            }

            hanler.post {
                // UI 작업 가능 . 이때 handler 만들 때 MainLopper로 만들어야 됨. (MainThread에 연결된 handler)
                // 이 핸들러를 다른 스레드에서 참조하여 사용해서 메인 스레드를 실행시켜 주는 것
            }

        }).start()
```

바깥에서 항상 동작하는 스레드는 메인스레드다.

네트워크 통신이나 파일을 쓰는 메인 스레드가 느려지는 작업을 할 때, 별도의 스레드를 열어서 UI스레드와 별개로 작업을 한다.

가령 위 스레드 안에서 네트워크 통신 작업을 하고 이후 UI Update를 하려고 하면 접근이 불가능하다.

그래서 UI를 그릴 작업이 있다면 잠깐 UI Thread를 불러오는 runOnUiThread{} 를 통해 안에서 작업한다.

이때, 이 runOnUiThread에서 사용하는 기능이 handler이다.

handler에 postDelayed함수를 사용해 n초 이후에 실행시키는 작업이 가능하다.

- ### Theme

```
    // NoActionBar Style 생성
    <style name="Theme.Ch0203.NoOptionBar" parent="Theme.MaterialComponents.DayNight.NoActionBar"/>

    // Manifest file에서 명시
    <activity android:name=".DiaryActivity"
        android:theme="@style/Theme.Ch0203.NoOptionBar"/>
```

안드로이드 기본 속성 테마에 영향을 받지 않는 버튼 - AppcompatButton을 사용

```
    <androidx.appcompat.widget.AppCompatButton
        android:id="@+id/openButton"
        android:layout_width="40dp"
        android:layout_height="60dp"
        android:layout_marginEnd="10dp"
        android:background="#a3a3a3"
        app:layout_constraintBottom_toBottomOf="@id/numberPicker1"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toStartOf="@id/numberPicker1"
        app:layout_constraintTop_toTopOf="@id/numberPicker1" />
```

- ### AlertDialog
  Builder 패턴을 통해서 AlertDialog Builder를 만들 수 있다.

```
  AlertDialog.Builder(this)
    .setTitle("실패")
    .setMessage("비밀번호가 잘못되었습니다. ")
    .setPositiveButton("확인") { dialog, which -> }
    .create()
    .show()
```

- # Kotlin
- ### android-ktx로 sharedPreference 사용하기 (Kotlin Android Extension)

  sharedPreference의 editor여는 기능 실습
