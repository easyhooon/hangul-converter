# Release Notes

## v1.0.0 (2025-11-05)

### 🎉 Initial Release

QWERTY 키보드로 입력한 영타를 한글로 변환하는 순수 Kotlin 라이브러리의 첫 번째 릴리즈입니다.

### ✨ Features

- QWERTY 키보드 영타를 한글로 변환
- 초성, 중성, 종성 완벽 지원
- 단독 자음 처리 (ㄱ, ㄴ, ㄷ 등)
- 복합 모음 지원 (ㅘ, ㅙ, ㅚ, ㅝ, ㅞ, ㅟ, ㅢ)
- 복합 자음 지원 (ㄳ, ㄵ, ㄶ, ㄺ, ㄻ, ㄼ, ㄽ, ㄾ, ㄿ, ㅀ, ㅄ)
- 순수 Kotlin JVM 라이브러리 (Android 의존성 없음)

### 📦 Installation

```kotlin
dependencies {
    implementation("io.github.easyhooon:qwerty2hangul:1.0.0")
}
```

### 💡 Usage Example

```kotlin
Qwerty2Hangul.engToKor("dkssudgktpdy") // 안녕하세요
```

### 🎯 Use Cases

- 채팅 앱에서 영타로 입력된 메시지 변환
- 서버 사이드 텍스트 처리