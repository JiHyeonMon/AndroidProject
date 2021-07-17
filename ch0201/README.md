# BMI 계산기

<img src="https://user-images.githubusercontent.com/50662636/125940916-94f3fb5b-36c5-4cfd-90fb-5066fcb8300a.png" width="300" height="500" />  <img src="https://user-images.githubusercontent.com/50662636/125940949-4a6dab08-c19d-4b01-a17e-8cc5d948ff5e.png" width="300" height="500" />

- ### LinearLayout으로 그리기

- ### When 분기문

|bmi 수치| 결과 |
|------|---|
|35 이상| 고도 비만 |
|30~35| 중정도 비만|
|25~30| 경고 비만|
|23~25| 과체중 |
|18.5~23| 정상 체중 |
|28.5 미만| 저체중 |
```
val result = when {
            bmi >= 35 -> "고도비만"
            bmi >= 30 -> "증정도비만"
            bmi >= 25 -> "경도비만"
            bmi >= 23 -> "과체중"
            bmi >= 18.5 -> "정상체중"
            else -> "저체중"
        }
```

- ### Intent

```
val intent = Intent(this, ResultActivity::class.java)
intent.putExtra("height", height.text.toString().toInt())
intent.putExtra("weight", weight.text.toString().toInt())
startActivity(intent)
```

```
val height = intent.getIntExtra("height", -1)
val weight = intent.getIntExtra("weight", -1)
```
