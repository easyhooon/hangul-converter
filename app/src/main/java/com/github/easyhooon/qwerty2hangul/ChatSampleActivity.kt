package com.github.easyhooon.qwerty2hangul

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.easyhooon.qwerty2hangul.ui.theme.Qwerty2HangulTheme
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
            Qwerty2HangulTheme {
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
    val isConverted: Boolean = false,
    val originalContent: String? = null
)

class ChatViewModel : ViewModel() {
    var messageText by mutableStateOf("")
    var messages = mutableStateListOf<Message>()

    init {
        addInitialMessages()
    }

    private fun addInitialMessages() {
        val initialMessages = listOf(
            Message(content = "dkssudgktpdy!", isFromMe = false), // 안녕하세요!
            Message(content = "qksrkdnjdy!", isFromMe = true), // 반가워요!
            Message(content = "wjeh qksrkdnjdy!!", isFromMe = false), // 저도 반가워요!
            Message(content = "자주 연락해요~", isFromMe = true),
            Message(content = "dkfrpTdjdy~ whgdms gkfn qhsody~", isFromMe = false) // 알겠어요~ 좋은 하루 보내요~
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

                // 항상 영타 한글로 랜덤 응답하여 변환 기능을 시연할 수 있도록 함
                val responseText = getRandomResponse()

                messages.add(Message(content = responseText, isFromMe = false))
            }
        }
    }

    // 랜덤 응답 생성
    private fun getRandomResponse(): String {
        val responses = listOf(
            "rhoscksgdk!", // 괜찮아!
            "dhsmf gkfn wkf qhso~", // 오늘 하루 잘 보내~
            "rhakdnj!", // 고마워!
            "dkssud!", // 안녕!
            "wjeh qksrkdnjdy!", // 저도 반가워요!
            "tnrhgktpdy~" // 수고하세요~
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
                message.originalContent?.let { original ->
                    messages[index] = message.copy(
                        content = original,
                        isConverted = false,
                        originalContent = null
                    )
                }
                return
            }

            // 영어로 타이핑된 한글을 변환
            val converted = QwertyHangul.engToKor(message.content)

            // 변환 결과가 다를 경우에만 변환된 메시지 추가
            if (converted != message.content) {
                messages[index] = message.copy(
                    content = converted,
                    isConverted = true,
                    originalContent = message.content
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("한글 변환 채팅 샘플") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
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
                            text = "메시지 버블을 탭하면 한글 변환 옵션이 표시됩니다.",
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp),
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
                            if (message.isConverted) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                alpha = 0.7f
                            )
                            else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        } else {
                            if (message.isConverted) MaterialTheme.colorScheme.onTertiaryContainer.copy(
                                alpha = 0.7f
                            )
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

class MessagePreviewParameterProvider : PreviewParameterProvider<Message> {
    override val values = sequenceOf(
        Message(
            id = UUID.randomUUID().toString(),
            content = "안녕하세요!",
            isFromMe = false,
            isConverted = false
        ),
        Message(
            id = UUID.randomUUID().toString(),
            content = "dkssudgktpdy!",
            isFromMe = false,
            isConverted = false
        ),
        Message(
            id = UUID.randomUUID().toString(),
            content = "안녕하세요!",
            isFromMe = false,
            isConverted = true
        ),
        Message(
            id = UUID.randomUUID().toString(),
            content = "반갑습니다!",
            isFromMe = true,
            isConverted = false
        )
    )
}

@Preview(
    name = "Chat Message Item",
    showBackground = true,
    backgroundColor = 0xFFF0F0F0,
    widthDp = 360
)
@Composable
fun ChatMessageItemPreview(
    @PreviewParameter(MessagePreviewParameterProvider::class) message: Message
) {
    Qwerty2HangulTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {
            ChatMessageItem(
                message = message,
                onLongClick = {},
                onCopyClick = {}
            )
        }
    }
}

@Preview(
    name = "Chat App",
    showBackground = true,
    widthDp = 360,
    heightDp = 720
)
@Composable
fun ChatSampleAppPreview() {
    Qwerty2HangulTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val mockViewModel = ChatViewModel().apply {
                messages.clear()
                messages.addAll(
                    listOf(
                        Message(content = "안녕하세요!", isFromMe = false),
                        Message(content = "반갑습니다", isFromMe = true),
                        Message(content = "dlwlgns!", isFromMe = false),
                        Message(content = "감사합니다", isFromMe = true, isConverted = true),
                        Message(content = "dkssudgktpdy?", isFromMe = false),
                        Message(content = "내일 뵐게요", isFromMe = false)
                    )
                )
            }

            ChatSampleApp(viewModel = mockViewModel)
        }
    }
}

@Preview(
    name = "Message Options",
    showBackground = true,
    widthDp = 360
)
@Composable
fun MessageOptionsPreview() {
    Qwerty2HangulTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "메시지 옵션",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val message = Message(
                id = UUID.randomUUID().toString(),
                content = "dlwlgns!",
                isFromMe = false,
                isConverted = false
            )

            ChatMessageItem(
                message = message,
                onLongClick = {},
                onCopyClick = {}
            )

            val convertedMessage = Message(
                id = UUID.randomUUID().toString(),
                content = "안녕!",
                isFromMe = false,
                isConverted = true
            )

            ChatMessageItem(
                message = convertedMessage,
                onLongClick = {},
                onCopyClick = {}
            )
        }
    }
}

@Preview(
    name = "Full Chat Interface",
    showBackground = true,
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=portrait"
)
@Composable
fun FullChatInterfacePreview() {
    Qwerty2HangulTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val mockViewModel = ChatViewModel().apply {
                messages.clear()
                messages.addAll(
                    listOf(
                        Message(content = "dkssudgktpdy!", isFromMe = false), // 안녕하세요!
                        Message(content = "qksrkdnjdy!", isFromMe = true), // 반가워요!
                        Message(content = "wjeh qksrkdnjdy!!", isFromMe = false), // 저도 반가워요!
                        Message(content = "자주 연락해요~", isFromMe = true),
                        Message(content = "dkfrpTdjdy~! whgdms gkfn qhsody~", isFromMe = false) // 알겠어요~ 좋은 하루 보내요~
                    )
                )
                messageText = ""
            }

            ChatSampleApp(viewModel = mockViewModel)
        }
    }
}