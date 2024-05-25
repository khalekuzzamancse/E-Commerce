package chat
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import netwok.APIFacade

class ChatViewModel : ChatUIController() {
    private val _waitingForResponse= MutableStateFlow(false)
    val waitingForResponse=_waitingForResponse.asStateFlow()

    val apiKey =
        "AIzaSyCX5T-wMgAOJGo5xol-8q71alX2DgX7AZA" //move to it local property so keep it hidden
    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = apiKey
    )

    override fun onSendRequest() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val context=getContext()

                //    val cookieImageData: ByteArray = // ...
                val inputContent = content {
                    //   image(PlatformImage(cookieImageData))
                    val sendMsg = this@ChatViewModel.getTypedMessage()
                    text(sendMsg+"\n"+context)
                    onCurrentMessageSuccessfullySent()
                    _waitingForResponse.update { true }
                }
                val response = generativeModel.generateContent(inputContent)
                val receivedMsg = response.text
                if (receivedMsg != null) {
                    addToConversation(
                        ChatMessage(
                            senderName = "Bot",
                            message = receivedMsg,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                _waitingForResponse.update { false }

            } catch (e: Exception) {
                _waitingForResponse.update { false }
                print("Response:Error:$e")
                updateSnackBarMessage(SnackBarMessage.Error("Failed to sent:${e.message}"))
            }


        }


    }

    override fun onSpeechToTextRequest() =
        updateSnackBarMessage(SnackBarMessage.Error("Not Implemented Yet..."))

    override fun onAttachmentClick() =
        updateSnackBarMessage(SnackBarMessage.Success("Not Implemented Yet..."))



}
suspend fun getContext(): String {
   return "Answer the  question by using the concept from here,if the question is not " +
            "under the concept/paragraph then reply as =\"Sorry\"." +
            "here is the concept:${ APIFacade().fetchProducts().getOrDefault(emptyList()).map {
                "Name: ${it.name}, Price: ${it.price},Description: ${it.description}"
            }}"



}