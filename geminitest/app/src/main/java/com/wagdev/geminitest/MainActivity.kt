package com.wagdev.geminitest

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowDropDown

import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.wagdev.geminitest.ui.theme.GeminitestTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {
    private val uriState=MutableStateFlow("")
    private val imagePicker=registerForActivityResult<PickVisualMediaRequest, Uri>(
        ActivityResultContracts.PickVisualMedia()
    ){uri->
        uri?.let {
            uriState.update{
                uri.toString()
            }
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeminitestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   Scaffold(
                      topBar = {
                          Box(
                              modifier= Modifier
                                  .fillMaxWidth()
                                  .background(MaterialTheme.colorScheme.primary)
                                  .height(55.dp)
                                  .padding(horizontal = 16.dp)
                          ){
                              Text(
                                  stringResource(id = R.string.app_name),
                                  modifier=Modifier.align(Alignment.CenterStart),
                                  fontSize = 26.sp,
                                  color = MaterialTheme.colorScheme.onPrimary

                              )

                          }
                      }
                   ) {
                       ChatScreen()
                   }
                }
            }
        }
    }
    @Composable
    fun ChatScreen(){
          val chatViewModel=viewModel<ChatViewModel>()

        val chatState by chatViewModel.chatState.collectAsState()
        val bitmap=getBitmap()
       Column(
        modifier= Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
           verticalArrangement = Arrangement.Center
        
       ) {
           LazyColumn(
               modifier= Modifier
                   .weight(1f)
                   .fillMaxWidth()
                   .padding(horizontal = 8.dp),
               reverseLayout = true
           ){
               itemsIndexed(chatState.chatList){
                   index, chat ->
                   if(chat.isFromUser){
                       UserchatItem(
                           prompt =chat.prompt,
                           bitmap = chat.bitmap
                       )
                   }else{
                       ModelchatItem(prompt = chat.prompt)
                   }

               }
           }
           
           Row(modifier = Modifier
               .fillMaxWidth()
               .padding(bottom = 22.dp),
               verticalAlignment = Alignment.CenterVertically
           
           ) {
               Column() {
                   bitmap?.let {
                       Image(
                           contentDescription="picked image",
                           bitmap=it.asImageBitmap(),
                           modifier = Modifier
                               .width(60.dp)
                               .height(40.dp)
                               .padding(bottom = 2.dp)
                               .clip(RoundedCornerShape(6.dp)),
                           contentScale = ContentScale.Crop)}
                   Icon(
                       modifier= Modifier
                           .size(60.dp)
                           .padding(10.dp)
                           .clickable {
                               imagePicker.launch(
                                   PickVisualMediaRequest
                                       .Builder()
                                       .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                       .build()
                                   )
                           },
                       imageVector = Icons.Rounded.AddAPhoto,
                       contentDescription = "add photo",
                       tint = MaterialTheme.colorScheme.primary
                   )}
                   Row(modifier=Modifier.fillMaxWidth()){

                       TextField(value = chatState.prompt,
                           onValueChange ={v->
                               chatViewModel.onEvent(ChatUiEvent.UpdatePrompt(v.toString()))
                           },
                           placeholder = {
                               Text(text="Type a prompt")
                           }

                       )

                       Icon(
                           modifier= Modifier
                               .size(60.dp)
                               .padding(10.dp)
                               .clickable {
                                   chatViewModel.onEvent(ChatUiEvent.SendPrompt(chatState.prompt, bitmap ))
                               },
                           imageVector = Icons.Rounded.Send,
                           contentDescription = "send prompt",
                           tint = MaterialTheme.colorScheme.primary
                       )





               }

           }

       }


    }
    @Composable
    fun UserchatItem(
        prompt:String,
        bitmap: Bitmap?
    ) {
        Column(
            modifier=Modifier.padding(
                start = 100.dp, bottom = 8.dp
            )
        ) {
            bitmap?.let {
                Image(
                    contentDescription="image",
                    bitmap=it.asImageBitmap(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(135.dp)
                        .padding(bottom = 2.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop)
           }
            Text(prompt ,
                modifier= Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.secondary
                    )
                    .padding(16.dp), fontSize = 17.sp,color=MaterialTheme.colorScheme.onSecondary)


        }

    }
    @Composable
    fun ModelchatItem(
        prompt:String
    ) {
        Column(
            modifier=Modifier.padding(
                end = 100.dp, bottom = 8.dp
            )
        ) {

            LimitedText(prompt)
        }

    }

@Composable
    private fun getBitmap():Bitmap?{
        val uri=uriState.collectAsState().value
       val imageState: AsyncImagePainter.State=rememberAsyncImagePainter(
           model=ImageRequest.Builder(
               LocalContext.current
           ).data(uri)
               .size(Size.ORIGINAL)
               .build()
       ).state
    if (imageState is AsyncImagePainter.State.Success){
        return  imageState.result.drawable.toBitmap()
    }

    return null
    }

}

@Composable
fun LimitedText(prompt: String) {
    // State to track whether the full text is visible or not
    var isFullTextVisible by remember { mutableStateOf(false) }

    // Create a substring containing the first 200 letters
    val limitedText = if (prompt.length > 200 && !isFullTextVisible) {
        prompt.substring(0, 137) + "..."
    } else {
        prompt
    }

    Column {
        Text(
            text = limitedText,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp),
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        // Show the "See More" button only if the text is truncated
        if (prompt.length > 150 && !isFullTextVisible) {
            Button(
                onClick = { isFullTextVisible = true },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "See More")
            }
        }
    }
}

