package com.wagdev.geminitest

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wagdev.geminitest.data.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.wagdev.geminitest.data.ChatData
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
     var prom=MutableStateFlow("")
    private  val _chatState= MutableStateFlow(ChatState())
    val chatState=_chatState.asStateFlow()
    fun updatePrompt(newPrompt: String) {
        prom.value += newPrompt
    }
    fun onEvent(event :ChatUiEvent){
        when(event){
            is ChatUiEvent.SendPrompt->{
                 if(chatState.value.prompt.isNotEmpty()){
                    addPrompt(event.prompt,event.bitmap)
                     if (event.bitmap!=null){
                         getResponseWithImage(event.prompt,event.bitmap)
                     }else{
                         getResponse(event.prompt)
                     }
                 }else{
                     addPrompt("What does this image contain",event.bitmap)
                     if (event.bitmap!=null){
                         getResponseWithImage("What does this image contain",event.bitmap)
                     }else{
                         getResponse("Hello")
                     }
                 }
            }
            is ChatUiEvent.UpdatePrompt->{
                ChatUiEvent.UpdatePrompt(event.newPrompte)
                _chatState.update {
                    it.copy(prompt =event.newPrompte)
                }
              //  _chatState.value=_chatState.value.copy(prompt =+ event.newPrompte)
            }
        }

    }
    fun addPrompt(prompt:String,bitmap: Bitmap?){
        _chatState.update {
                it.copy(
                    chatList =it.chatList.toMutableList().apply {
                        add(0, Chat(
                            prompt,bitmap,true
                        )

                        )

                    },
                    prompt="",
                    bitmap=null

                )

        }

    }

    private fun getResponse(prompt: String){
        viewModelScope.launch {
            val chat =ChatData.getResponse(prompt)
            _chatState.update {
                it.copy(
                    chatList =  it.chatList.toMutableList().apply {
                        add(0,chat)
                    }
                )
            }
        }
    }
    private fun getResponseWithImage(prompt: String,bitmap: Bitmap ){
        viewModelScope.launch {
            val chat =ChatData.getResponseWithImage(prompt,bitmap)
            _chatState.update {
                it.copy(
                    chatList =  it.chatList.toMutableList().apply {
                        add(0,chat)
                    }
                )
            }
        }
    }


}