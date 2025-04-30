package com.github.easyhooon.hangul_converter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.easyhooon.hangul_converter.ui.theme.HangulConverterTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatSampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            HangulConverterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatSampleApp()
                }
            }
        }
    }
}

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = true,
    val isConverted: Boolean = false
)

class ChatViewModel : ViewModel() {
    var messageText by mutableStateOf("")
    var messages = mutableStateListOf<Message>()

    init {
        addInitialMessages()
    }

    private fun addInitialMessages() {
        val initialMessages = listOf(
            Message(content = "안녕하세요!", isFromMe = false),
            Message(content = "반갑습니다", isFromMe = true),
            Message(content = "dlwlgns!", isFromMe = false), // 영어로 타이핑한 한글
            Message(content = "dkssudgktpdy?", isFromMe = false), // 영어로 타이핑한 한글
            Message(content = "rkatkgkqslek", isFromMe = true), // 영어로 타이핑한 한글
            Message(content = "내일 뵐게요", isFromMe = false)
        )

        messages.addAll(initialMessages)
    }

    fun sendMessage() {
        if (messageText.isNotBlank()) {
            val newMessage = Message(content = messageText)
            messages.add(newMessage)
            messageText = ""

            MainScope().launch {
                delay(2000)

                // 간단한 응답 로직 - 영어로 입력된 한글이면 그대로 응답, 아니면 랜덤 응답
                val responseText = if (isEnglishTypedKorean(newMessage.content)) {
                    newMessage.content // 영어로 타이핑된 한글이면 그대로 응답
                } else {
                    getRandomResponse()
                }

                messages.add(Message(content = responseText, isFromMe = false))
            }
        }
    }

    // 단순 영어로 타이핑된 한글인지 확인 (매우 간단한 휴리스틱)
    private fun isEnglishTypedKorean(text: String): Boolean {
        // 한글 자음과 모음에 대응되는 영어 문자들이 일정 비율 이상 포함되면 영타 한글로 간주
        val koreanCharPattern = ".*[qwertyuiopasd fghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM]{2,}.*"
        return text.matches(Regex(koreanCharPattern))
    }

    // 랜덤 응답 생성
    private fun getRandomResponse(): String {
        val responses = listOf(
            "네 알겠습니다",
            "그렇군요",
            "좋은 하루 되세요!",
            "dlwlgns!", // 일부러 영어로 타이핑한 메시지
            "dkssudgktpdy?", // 일부러 영어로 타이핑한 메시지
            "rkatkgkqslek" // 일부러 영어로 타이핑한 메시지
        )
        return responses.random()
    }

    // 메시지 변환
    fun convertMessage(messageId: String) {
        val index = messages.indexOfFirst { it.id == messageId }
        if (index != -1) {
            val message = messages[index]

            // 이미 변환되었다면 원본으로 복원
            if (message.isConverted) {
                // 원본 메시지를 찾는 로직이 필요하지만 이 예제에서는 생략
                return
            }

            // 영어로 타이핑된 한글을 변환
            val converted = HangulConverter.engToKor(message.content)

            // 변환 결과가 다를 경우에만 변환된 메시지 추가
            if (converted != message.content) {
                messages[index] = message.copy(
                    content = converted,
                    isConverted = true
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatSampleApp(viewModel: ChatViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(viewModel.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("한글 변환 채팅 샘플") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = viewModel.messageText,
                        onValueChange = { viewModel.messageText = it },
                        placeholder = { Text("메시지를 입력하세요") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        singleLine = true
                    )

                    IconButton(
                        onClick = { viewModel.sendMessage() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "전송",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "메시지 버블을 길게 탭하면 한글 변환 옵션이 표시됩니다.",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                items(viewModel.messages) { message ->
                    ChatMessageItem(
                        message = message,
                        onLongClick = { id ->
                            scope.launch {
                                viewModel.convertMessage(id)
                            }
                        },
                        onCopyClick = { content ->
                            clipboardManager.setText(AnnotatedString(content))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessageItem(
    message: Message,
    onLongClick: (String) -> Unit,
    onCopyClick: (String) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) {
        dateFormatter.format(Date(message.timestamp))
    }

    var showOptions by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isFromMe) 16.dp else 4.dp,
                    bottomEnd = if (message.isFromMe) 4.dp else 16.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (message.isFromMe) {
                        if (message.isConverted) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.primary
                    } else {
                        if (message.isConverted) MaterialTheme.colorScheme.tertiaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                modifier = Modifier
                    .combinedClickable(
                        onClick = { showOptions = !showOptions },
                        onLongClick = { onLongClick(message.id) }
                    )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = message.content,
                        color = if (message.isFromMe) {
                            if (message.isConverted) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onPrimary
                        } else {
                            if (message.isConverted) MaterialTheme.colorScheme.onTertiaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (message.isFromMe) {
                            if (message.isConverted) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        } else {
                            if (message.isConverted) MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        }
                    )
                }
            }

            if (message.isConverted) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .align(if (message.isFromMe) Alignment.TopStart else Alignment.TopEnd)
                        .offset(x = if (message.isFromMe) (-4).dp else 4.dp, y = (-4).dp)
                ) {
                    Text("변환됨", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        AnimatedVisibility(visible = showOptions) {
            Card(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .align(if (message.isFromMe) Alignment.End else Alignment.Start),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            onLongClick(message.id)
                            showOptions = false
                        }
                    ) {
                        Text(if (message.isConverted) "원본 보기" else "한글 변환")
                    }

                    TextButton(
                        onClick = {
                            onCopyClick(message.content)
                            showOptions = false
                        }
                    ) {
                        Text("복사")
                    }
                }
            }
        }
    }
}