# Qwerty2Hangul

QWERTY 키보드로 입력한 영타를 한글로 변환하는 순수 Kotlin 라이브러리입니다.

## Background

한국어를 사용하는 사람들은 종종 한글 입력 모드로 전환하지 않고 영문 키보드로 한글을 입력하는 실수를 합니다.

특히 **채팅 앱**에서 메시지를 보낸 후에야 영타로 입력했다는 것을 깨닫는 경우가 많습니다.

예를 들어 "안녕하세요"를 입력하려고 했지만 "dkssudgktpdy"로 전송되는 상황입니다.

이 라이브러리는 이러한 영타 메시지를 원래 의도한 한글로 변환할 수 있게 해줍니다.

https://github.com/user-attachments/assets/29b9bb1f-bdb7-4d00-8062-b49578a7efa1

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.easyhooon:qwerty2hangul:<latest_version>")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'io.github.easyhooon:qwerty2hangul:<latest_version>'
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.easyhooon</groupId>
    <artifactId>qwerty2hangul</artifactId>
    <version>latest_version</version>
</dependency>
```

최신 버전은 [Releases](https://github.com/easyhooon/qwerty2hangul/releases)에서 확인하세요.

## Usage

### 기본 사용

```kotlin
import com.github.easyhooon.qwerty2hangul.Qwerty2Hangul

fun main() {
    val input = "dkssudgktpdy"
    val output = Qwerty2Hangul.engToKor(input)
    println(output) // 안녕하세요
}
```

### 채팅 앱에서의 사용 예시

> 💡 **전체 예제 코드는 [샘플 앱](https://github.com/easyhooon/qwerty2hangul/tree/main/app/src/main/java/com/github/easyhooon/qwerty2hangul)에서 확인할 수 있습니다.**

```kotlin
data class Message(
    val id: String,
    val content: String,
    val isConverted: Boolean = false,
    val originalContent: String? = null
)

class ChatViewModel : ViewModel() {
    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    fun convertMessage(messageId: String) {
        val index = _messages.indexOfFirst { it.id == messageId }
        if (index != -1) {
            val message = _messages[index]

            // 이미 변환된 경우 원본으로 복원
            if (message.isConverted) {
                message.originalContent?.let { original ->
                    _messages[index] = message.copy(
                        content = original,
                        isConverted = false,
                        originalContent = null
                    )
                }
                return
            }

            // 영타를 한글로 변환
            val converted = Qwerty2Hangul.engToKor(message.content)
            if (converted != message.content) {
                _messages[index] = message.copy(
                    content = converted,
                    isConverted = true,
                    originalContent = message.content
                )
            }
        }
    }
}
```

### Hilt를 통한 싱글톤 주입

```kotlin
// Module
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideQwerty2Hangul(): Qwerty2Hangul {
        return Qwerty2Hangul
    }
}

// ViewModel
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val qwerty2Hangul: Qwerty2Hangul
) : ViewModel() {

    fun convertMessage(text: String): String {
        return qwerty2Hangul.engToKor(text)
    }
}
```

### 더 많은 예제

```kotlin
// 기본 변환
Qwerty2Hangul.engToKor("dkssud") // 안녕

// 자음만 있는 경우
Qwerty2Hangul.engToKor("r") // ㄱ
Qwerty2Hangul.engToKor("rk") // 가

// 받침이 있는 경우
Qwerty2Hangul.engToKor("gksrmf") // 한글

// 복잡한 문장
Qwerty2Hangul.engToKor("gksmf qkdgks dlqfur") // 한글 변환 테스트
```

## Features

- ✅ 순수 Kotlin JVM 라이브러리 (Android 의존성 없음)
- ✅ 초성, 중성, 종성 완벽 지원
- ✅ 단독 자음 처리
- ✅ 복합 모음 지원 (ㅘ, ㅙ, ㅚ 등)
- ✅ 복합 자음 지원 (ㄳ, ㄵ, ㄶ 등)

## 지원 범위

이 라이브러리는 **QWERTY 키보드 전용**입니다.

천지인, 나랏글 등의 한글 전용 키보드는 애초에 한글 입력 모드만 지원하므로 영타 문제가 발생하지 않습니다.

## Release Article
https://shorturl.at/Sqm3P

## Contribution

이슈와 PR은 언제나 환영합니다!

## License

```
MIT License

Copyright (c) 2025 Lee jihun

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
