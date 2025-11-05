package com.github.easyhooon.qwerty2hangul

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.easyhooon.qwerty2hangul.ui.theme.Qwerty2HangulTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Qwerty2HangulTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Qwerty2HangulApp()
                }
            }
        }
    }
}

@Composable
fun Qwerty2HangulApp() {
    var inputText by remember { mutableStateOf("") }
    var outputText by remember { mutableStateOf("") }
    var showCopiedMessage by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboardManager = LocalClipboardManager.current

    fun convertToHangul() {
        outputText = Qwerty2Hangul.engToKor(inputText)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "영어 → 한글 변환기",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("영어로 입력된 한글") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    convertToHangul()
                    keyboardController?.hide()
                }
            )
        )

        Button(
            onClick = { convertToHangul() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("변환하기")
        }

        OutlinedTextField(
            value = outputText,
            onValueChange = { },
            label = { Text("변환된 한글") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            readOnly = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    inputText = ""
                    outputText = ""
                }
            ) {
                Text("초기화")
            }

            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(outputText))
                    showCopiedMessage = true
                }
            ) {
                Text("복사하기")
            }
        }

        if (showCopiedMessage) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showCopiedMessage = false
            }

            Text(
                text = "클립보드에 복사되었습니다!",
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "사용 방법",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("1. 영어 키보드로 입력한 한글을 입력창에 붙여넣기")
                Text("2. '변환하기' 버튼을 클릭")
                Text("3. 변환된 한글을 복사하여 사용")

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "예시",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("• dlwlgns → 안녕")
                Text("• dlatlqkftodi → 반갑습니다")
                Text("• dnjwhsgkwkrh → 감사합니다")
            }
        }
    }
}