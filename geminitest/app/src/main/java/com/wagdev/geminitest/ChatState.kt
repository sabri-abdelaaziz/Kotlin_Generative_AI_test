package com.wagdev.geminitest

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.wagdev.geminitest.data.Chat

 data class ChatState(
     val chatList: MutableList<Chat> = mutableListOf<Chat>(),
     val prompt: String ="",
     val bitmap: Bitmap? = null
 )
