package com.wagdev.geminitest

import android.graphics.Bitmap

sealed class ChatUiEvent{
    data class UpdatePrompt(val  newPrompte:String ):ChatUiEvent()
    data class SendPrompt(val  prompt:String,val bitmap:Bitmap? ):ChatUiEvent()
}
