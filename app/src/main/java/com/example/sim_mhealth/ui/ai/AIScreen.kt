package com.example.sim_mhealth.ui.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sim_mhealth.BuildConfig
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.ui.theme.Gray700
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.content
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

// Rate Limiter Class
class RateLimiter(
    private val maxRequests: Int = 5,
    private val timeWindowMs: Long = 60_000L
) {
    private val requests = mutableListOf<Long>()

    fun canMakeRequest(): Boolean {
        val now = System.currentTimeMillis()
        requests.removeAll { it < now - timeWindowMs }
        return requests.size < maxRequests
    }

    fun recordRequest() {
        requests.add(System.currentTimeMillis())
    }

    fun getRemainingRequests(): Int {
        val now = System.currentTimeMillis()
        requests.removeAll { it < now - timeWindowMs }
        return maxRequests - requests.size
    }
}

@Composable
fun AIScreen(navController: NavController) {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }

    var messageText by remember { mutableStateOf("") }
    var chatMessages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var isLoading by remember { mutableStateOf(false) }

    // Rate limiting & optimization
    val responseCache = remember { mutableStateMapOf<String, String>() }
    val rateLimiter = remember { RateLimiter(maxRequests = 15, timeWindowMs = 60_000L) }
    var lastSendTime by remember { mutableStateOf(0L) }
    val minTimeBetweenSends = 2000L

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (chatMessages.isEmpty()) {
            chatMessages = listOf(
                ChatMessage(
                    id = "welcome",
                    text = "Haloo, " + (prefsManager.getUsername()
                        ?: "User") + " \n\nTanyakan Seputar Kesehatan\nDengan Asisten AI",
                    isFromUser = false
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Asisten Kesehatan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Gemini 2.0 Flash • ${rateLimiter.getRemainingRequests()}/15 request",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = { navController.navigate("notification_screen") },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.Black
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (chatMessages.isEmpty() || (chatMessages.size == 1 && !chatMessages[0].isFromUser)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Haloo, ${prefsManager.getUsername()}",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tanyakan Seputar Kesehatan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Dengan Asisten AI",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFF2196F3), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    reverseLayout = false
                ) {
                    items(chatMessages) { message ->
                        ChatMessageBubble(message)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (isLoading) {
                        item {
                            TypingIndicator()
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = {
                        if (it.length <= 500) { // Batasi 500 karakter
                            messageText = it
                        }
                    },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(color = Gray700),
                    placeholder = {
                        Text(
                            "Tanyakan apapun seputar kesehatan...",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        disabledBorderColor = Color(0xFFE0E0E0)
                    ),
                    maxLines = 4,
                    enabled = !isLoading,
                    supportingText = {
                        Text(
                            text = "${messageText.length}/500",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                )

                IconButton(
                    onClick = {
                        val currentTime = System.currentTimeMillis()

                        // Check debounce
                        if (currentTime - lastSendTime < minTimeBetweenSends) {
                            return@IconButton
                        }

                        // Check rate limit
                        if (!rateLimiter.canMakeRequest()) {
                            chatMessages = chatMessages + ChatMessage(
                                id = System.currentTimeMillis().toString(),
                                text = "⚠️ Terlalu banyak request. Kamu sudah mencapai limit 15 request per menit. Tunggu sebentar ya!",
                                isFromUser = false
                            )
                            scope.launch {
                                listState.animateScrollToItem(chatMessages.size)
                            }
                            return@IconButton
                        }

                        if (messageText.isNotBlank() && !isLoading) {
                            lastSendTime = currentTime
                            rateLimiter.recordRequest()

                            val userMessage = ChatMessage(
                                id = System.currentTimeMillis().toString(),
                                text = messageText,
                                isFromUser = true
                            )
                            chatMessages = chatMessages + userMessage

                            val query = messageText.trim()
                            val cacheKey = query.lowercase()
                            messageText = ""

                            scope.launch {
                                listState.animateScrollToItem(chatMessages.size)
                            }

                            scope.launch {
                                isLoading = true
                                try {
                                    val cachedResponse = responseCache[cacheKey]
                                    if (cachedResponse != null) {
                                        val aiResponse = ChatMessage(
                                            id = System.currentTimeMillis().toString(),
                                            text = "📋 *(dari cache)*\n\n$cachedResponse",
                                            isFromUser = false
                                        )
                                        chatMessages = chatMessages + aiResponse
                                        isLoading = false
                                        scope.launch {
                                            listState.animateScrollToItem(chatMessages.size)
                                        }
                                        return@launch
                                    }

                                    // Generate with optimized settings
                                    val aiText = generateWithRetry(query)

                                    // Save to cache
                                    responseCache[cacheKey] = aiText

                                    // Keep cache size manageable (max 50 items)
                                    if (responseCache.size > 50) {
                                        val oldestKey = responseCache.keys.first()
                                        responseCache.remove(oldestKey)
                                    }

                                    val aiResponse = ChatMessage(
                                        id = System.currentTimeMillis().toString(),
                                        text = aiText,
                                        isFromUser = false
                                    )
                                    chatMessages = chatMessages + aiResponse

                                } catch (e: RateLimitException) {
                                    chatMessages = chatMessages + ChatMessage(
                                        id = System.currentTimeMillis().toString(),
                                        text = "⏱️ ${e.message}\n\nSisa kuota menit ini: ${rateLimiter.getRemainingRequests()}/15",
                                        isFromUser = false
                                    )
                                    isLoading = false
                                } catch (e: Exception) {
                                    val errorResponse = ChatMessage(
                                        id = System.currentTimeMillis().toString(),
                                        text = "❌ Maaf, terjadi kesalahan: ${e.message}",
                                        isFromUser = false
                                    )
                                    chatMessages = chatMessages + errorResponse
                                } finally {
                                    isLoading = false
                                    scope.launch {
                                        listState.animateScrollToItem(chatMessages.size)
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (messageText.isNotBlank() && !isLoading)
                                Color(0xFF2196F3)
                            else
                                Color(0xFFE0E0E0),
                            shape = CircleShape
                        ),
                    enabled = messageText.isNotBlank() && !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (messageText.isNotBlank() && !isLoading)
                            Color.White
                        else
                            Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// Generate with retry and exponential backoff
suspend fun generateWithRetry(
    query: String,
    maxRetries: Int = 3
): String {
    var lastException: Exception? = null

    repeat(maxRetries) { attempt ->
        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash-lite",
                apiKey = BuildConfig.GEMINI_API_KEY,
                systemInstruction = content {
                    text(
                        """
                        Kamu adalah asisten kesehatan Indonesia yang profesional dan ramah.
                        
                        ATURAN PENTING:
                        - Berikan jawaban yang singkat, padat, dan jelas
                        - Batasi jawaban maksimal 150 kata kecuali pertanyaan memerlukan penjelasan detail
                        - Gunakan bahasa Indonesia yang mudah dipahami
                        - Fokus pada informasi yang paling penting
                        - Jika perlu penjelasan panjang, buat poin-poin ringkas
                        - Selalu ingatkan untuk konsultasi dengan dokter untuk diagnosa yang akurat
                    """.trimIndent()
                    )
                }
            )

            val response = generativeModel.generateContent(query)
            return response.text ?: "Maaf, tidak ada response"

        } catch (e: ServerException) {
            // TANGKAP ERROR 429 SPECIFICALLY
            if (e.message?.contains("429") == true || e.message?.contains("quota") == true) {
                lastException = e
                val waitTime = 60_000L * (attempt + 1) // 1 menit, 2 menit, 3 menit
                if (attempt < maxRetries - 1) {
                    // TAMPILKAN PESAN KE USER
                    throw RateLimitException(
                        "Terkena rate limit server. Akan coba lagi dalam ${waitTime / 1000} detik...",
                        waitTime
                    )
                }
            } else {
                lastException = e
                delay(1000L * (attempt + 1))
            }
        } catch (e: Exception) {
            lastException = e
            delay(1000L * (attempt + 1))
        }
    }

    throw lastException ?: Exception("Unknown error")
}

class RateLimitException(message: String, val waitTimeMs: Long) : Exception(message)

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser)
            Arrangement.End
        else
            Arrangement.Start
    ) {
        if (!message.isFromUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF2196F3), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = if (message.isFromUser) 16.dp else 4.dp,
                topEnd = if (message.isFromUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser)
                    Color(0xFF2196F3)
                else
                    Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                if (message.isFromUser) {
                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                } else {
                    MarkdownText(
                        markdown = message.text,
                        fontSize = 14.sp,
                        color = Color.Black,
                        linkColor = Color(0xFF2196F3),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (message.isFromUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF9E9E9E), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF2196F3), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = "AI",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Card(
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF2196F3), CircleShape)
                    )
                }
            }
        }
    }
}