package chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 *
 * This file is designed to be easily copy-pasted into another project without
 * worrying about dependent components.
 *
 * It helps in developing a project faster initially by using already developed components.
 *
 * Once the first release of the project is published, you can easily refactor each component
 * into a separate file, package, or module to make the project scalable and maintainable.
 * - The component are designed from bottom to top,means to understand the code read the file from bottom
 * to top
 */


@Composable
 fun ChatRoute(
    modifier: Modifier = Modifier,
    controller: ChatUIController
){
    _ChatRoute(
        modifier=modifier,
        conversations = controller.conversations.collectAsState().value,
        msgInputController = controller.msgFieldController,
        snackBarMessage = controller.message.collectAsState().value,
        onAttachmentClick =controller::onAttachmentClick ,
        onSpeechToTextRequest =controller::onSpeechToTextRequest ,
        onSendRequest = controller::onSendRequest
    )

}




/**
 * - Viewmodel need to Inherit this class
 * - This will reduce the boiler plate code for viewmodel and make the UI to works
 * without any viewmodel or ...so it will reduce couping
 */

 abstract class ChatUIController{
    //Controller and states
    private val _conversation= MutableStateFlow<List<ChatMessage>>(emptyList())
    val conversations=_conversation.asStateFlow()
    private val _message= MutableStateFlow<SnackBarMessage?>(null)
    val message=_message.asStateFlow()
    val msgFieldController= MessageFieldController()

    //
     fun getTypedMessage()=msgFieldController.getTypedMessage()
    fun addToConversation(message: ChatMessage){
        _conversation.update { conversations->
            conversations+message
        }
    }

    /**
     * -It will added to conversation list and clear the input field,so call it only
     * when a message is sent successfully
     */
    fun onCurrentMessageSuccessfullySent(){
        _conversation.update { conversations->
            conversations+ ChatMessage(
                message = getTypedMessage(),
                timestamp =System.currentTimeMillis(),
                senderName = null,
            )
        }
        msgFieldController.clearInputField()
    }

   fun updateSnackBarMessage(msg: SnackBarMessage){
       CoroutineScope(Dispatchers.Default).launch {
           _message.update { msg }
           //clear the message after 3 sec
           delay(3_000)
           _message.update { null }
       }
   }




    //events
    abstract fun onSendRequest()
    abstract fun onSpeechToTextRequest()
    abstract fun onAttachmentClick()


}



/**
 *
 */

@Composable
private fun _ChatRoute(
    modifier: Modifier = Modifier,
    conversations: List<ChatMessage>,
    msgInputController: MessageFieldController,
    snackBarMessage: SnackBarMessage?,
    onSendRequest: () -> Unit,
    onSpeechToTextRequest: () -> Unit,
    onAttachmentClick: () -> Unit,
) {
    _SnackBarDecorator(
        snackBarMessage
    ) { scaffoldPadding ->
        _ConversionBase(
            modifier = modifier.padding(scaffoldPadding),
            conversations = conversations,
            controller = msgInputController,
            onSendButtonClick = onSendRequest,
            onAttachmentClick = onAttachmentClick,
            onSpeechToTextRequest = onSpeechToTextRequest,
        )
    }


}


/**
 * - Based on type of message ,Different color snack bar will appear
 */

sealed interface SnackBarMessage {
    val msg: String
    data class Success(override val msg: String) : SnackBarMessage

    data class Error(override val msg: String) : SnackBarMessage
}

@Composable
private fun _SnackBarDecorator(
    message: SnackBarMessage?,
    content: @Composable (PaddingValues) -> Unit
) {

    val errorColor = MaterialTheme.colorScheme.error
    val successColor = Color.Blue
    val color= when (message) {
        is SnackBarMessage.Error -> errorColor
        is SnackBarMessage.Success -> successColor
        null -> Color.Unspecified
    }

    Scaffold(
        snackbarHost = {
            if (message!=null){
                Snackbar(
                    modifier = Modifier,
                    containerColor =color
                ){
                    Text(
                       text =  message.msg,
                        color = MaterialTheme.colorScheme.contentColorFor(color)
                    )
                }
            }
        }
    ) { scaffoldPadding ->
        content(scaffoldPadding)
    }
}


@Composable
private fun _ConversionBase(
    modifier: Modifier = Modifier,
    conversations: List<ChatMessage>,
    controller: MessageFieldController,
    onSendButtonClick: () -> Unit,
    onSpeechToTextRequest: () -> Unit,
    onAttachmentClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        _ChatList(
            modifier = Modifier.weight(1f),
            conversations = conversations,
        )
        _MessageInputField(
            controller = controller,
            onSendRequest = onSendButtonClick,
            onAttachmentLoadRequest = onAttachmentClick,
            onSpeechToTextRequest = onSpeechToTextRequest,
            modifier = Modifier.testTag("MessageInputBox")
        )
    }
}


/*
* TODO: Chat List Section --Chat List Section -- Chat List Section
* TODO: Chat List Section --Chat List Section -- Chat List Section
* TODO: Chat List Section --Chat List Section -- Chat List Section
*/

@Composable
private fun _ChatList(
    modifier: Modifier = Modifier,
    conversations: List<ChatMessage>,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ConversationList"),  // Test tag for the message list
        reverseLayout = true
    ) {
        items(conversations.reversed()) { msg ->
            val isSender = msg.senderName == null
            val alignment = if (isSender) Alignment.End else Alignment.Start
            Column(
                modifier = Modifier.fillMaxWidth()
                    .testTag(if (isSender) "SenderMessage" else "ReceiverMessage")
            ) {
                _ChatBubble(
                    modifier = Modifier.testTag("MessageInputBox"),
                    senderName = msg.senderName,
                    message = msg.message,
                    timeStamp = msg.timestamp,
                    shape = if (isSender) RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 32.dp,
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                    else RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomStart = 8.dp,
                        bottomEnd = 32.dp
                    ),
                    alignment = alignment,
                    backgroundColor =
                    if (isSender) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    //telegram uses white for received message that is equivalent to surface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

}


/**
 * @param senderName ,if device is sender then null
 */
data class ChatMessage(
    val senderName: String?,
    val message: String,
    val timestamp: Long,
)

// TODO: Chat Bubble Section --Chat Bubble Section Section -- Chat Bubble Section Section
/**
 * @param senderName ,if device is sender then null
 */
@Composable
private fun ColumnScope._ChatBubble(
    modifier: Modifier = Modifier,
    senderName: String?,
    message: String,
    timeStamp: Long,
    shape: Shape,
    alignment: Alignment.Horizontal,
    backgroundColor: Color
) {
    Column(modifier.align(alignment)) {
        Text(text = senderName ?: "Me", style = MaterialTheme.typography.bodyMedium)
        Surface(
            shape = shape,
            modifier = Modifier,
            shadowElevation = 1.dp,
            color = backgroundColor
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                SelectionContainer{
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                Text(
                    text = formatCurrentTimeMsToString(timeStamp),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End)
                        .alpha(0.5f) // Set the alpha value (0.5f for 50% transparency)
                )
            }

        }
    }

}
private fun formatCurrentTimeMsToString(currentTimeMs: Long): String {
    val currentDate = Date()
    val inputDate = Date(currentTimeMs)

    val oneMinuteInMs = 60 * 1000
    val oneDayInMs = 24 * 60 * 60 * 1000

    // Check if the difference is less than 1 minute
    if (currentDate.time - currentTimeMs < oneMinuteInMs) {
        return "Now"
    }

    // Create formatters
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)
    val dateTimeFormatter = SimpleDateFormat("MMM dd hh:mm a", Locale.US)

    // Check if the date is today
    val currentCalendar = Calendar.getInstance()
    val inputCalendar = Calendar.getInstance()
    inputCalendar.time = inputDate

    val isSameDay = currentCalendar.get(Calendar.YEAR) == inputCalendar.get(Calendar.YEAR) &&
            currentCalendar.get(Calendar.DAY_OF_YEAR) == inputCalendar.get(Calendar.DAY_OF_YEAR)

    return if (isSameDay) {
        timeFormatter.format(inputDate)
    } else {
        dateTimeFormatter.format(inputDate)
    }
}

/*
 * TODO: Message Input Field Section -- Message Input Field Section -- Message Input Field Section
 * TODO: Message Input Field Section -- Message Input Field Section -- Message Input Field Section
 * TODO: Message Input Field Section -- Message Input Field Section -- Message Input Field Section
 */

@Composable
private fun _MessageInputField(
    modifier: Modifier = Modifier,
    controller: MessageFieldController,
    onAttachmentLoadRequest: () -> Unit,
    onSpeechToTextRequest: () -> Unit,
    onSendRequest: () -> Unit
) {
    val emptyMessage = controller.message.collectAsState().value.trim().isEmpty()

    Box(
        modifier = modifier
            .heightIn(min = 60.dp, max = 150.dp)
            .fillMaxWidth()
    ) {
        TextField(
            value = controller.message.collectAsState().value,
            onValueChange = controller::onTextInput,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
            modifier = Modifier
                .heightIn(min = 60.dp, max = 150.dp)
                .fillMaxWidth()
                .testTag("MessageInputField"),
            placeholder = {
                Text("Type a message", modifier = Modifier.testTag("MessagePlaceholder"))
            }
        )
        if (emptyMessage) {
            Row(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                IconButton(
                    onClick = onAttachmentLoadRequest,
                    modifier = Modifier.testTag("AttachmentButton")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Attachment,
                        contentDescription = "Attach file",
                        tint = MaterialTheme.colorScheme.primary//Important,since clickable
                    )
                }
                IconButton(
                    onClick = onSpeechToTextRequest,
                    modifier = Modifier.testTag("SpeechToTextButton")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Activate microphone",
                        tint = MaterialTheme.colorScheme.primary//Important,since clickable
                    )
                }
            }
        } else {
            IconButton(
                onClick = onSendRequest,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .testTag("SendMessageButton")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message",
                    tint = MaterialTheme.colorScheme.primary//Important,since clickable
                )
            }
        }
    }
}

/**
 * - Hold the state of input message and observe the typing
 * - It will reduce the boiler plat code for the client code that is going to use this component
 */
class MessageFieldController {
    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()
    fun onTextInput(text: String) {
        _message.value = text
    }

    //using delegation pattern
    fun getTypedMessage() = _message.value

    fun clearInputField() {
        _message.value = ""
    }
}
