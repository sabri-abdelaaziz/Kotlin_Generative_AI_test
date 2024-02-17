package com.wagdev.geminitest.data

import android.graphics.Bitmap
import android.hardware.biometrics.BiometricPrompt
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChatData {
    private const val apiKey:String="your api Key from google ai studio"
    suspend fun getResponse(prompt:String):Chat{
        val model = GenerativeModel(
            // modelName = "gemini-pro-vision", for only images and prompts together

            modelName = "gemini-pro",
        apiKey= apiKey)

        try {
            val response= withContext(Dispatchers.IO){
                model.generateContent(prompt)
            }
            return  Chat(
                prompt=response.text?:"error",
                bitmap = null,
                isFromUser = false
            )
        }catch (e:Exception){
            return  Chat(
                prompt=e.message?:"error",
                bitmap = null,
                isFromUser = false
            )
        }



    }
    suspend fun getResponseWithImage(prompt:String,bitmap: Bitmap):Chat{
        val model = GenerativeModel(
           // modelName = "gemini-pro-vision", for only images and prompts together
            modelName = "gemini-pro-vision",
            apiKey= apiKey
        )

        try {
            val inputContent =content{
                image(bitmap)
                text(prompt)

            }
            val response= withContext(Dispatchers.IO){
                model.generateContent(inputContent)
            }
            return  Chat(
                prompt=response.text?:"error",
                bitmap = null,
                isFromUser = false
            )
        }catch (e:Exception){
            return  Chat(
                prompt=e.message?:"error",
                bitmap = null,
                isFromUser = false
            )
        }



    }

}