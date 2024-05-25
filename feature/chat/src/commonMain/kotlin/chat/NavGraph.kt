package chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * https://github.com/PatilShreyas/generative-ai-kmp
 */
/*
* - This file show how to use the Chat UI...
* - This just a guideline
*/


object RoutesDe {
    const val USER_LIST = "UserList"
    const val CONVERSATION = "Conversation"
    const val USER_N_CONVERSATION = "UserAndConversation"
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ChatNavGraph() {

    val vm= androidx.lifecycle.viewmodel.compose.viewModel { ViewModelImpl() }
     val viewModel =vm.chatViewModel
    val navController = rememberNavController()
    val windowSize = calculateWindowSizeClass().widthSizeClass
    LaunchedEffect(windowSize) {
        when (windowSize) {
            WindowWidthSizeClass.Compact -> navController.navigate(RoutesDe.USER_LIST)
            else -> navController.navigate(RoutesDe.USER_N_CONVERSATION)
        }
    }


    NavHost(
        navController = navController,
        startDestination = RoutesDe.USER_LIST
    ) {
        composable(RoutesDe.USER_LIST) {
            UserListPreview {
                navController.navigate(RoutesDe.CONVERSATION)
            }
        }
        composable(RoutesDe.CONVERSATION) {
            ChatUIPreview(showNavigationIcon = true, viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable(RoutesDe.USER_N_CONVERSATION) {
            _UsersNConversationScreen(viewModel)
        }

    }

}


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun _UsersNConversationScreen(
    viewModel: ChatViewModel,
) {
    val windowSize = calculateWindowSizeClass().widthSizeClass
    val scannedDeviceWeight =
        if (windowSize == WindowWidthSizeClass.Expanded) 0.35f else 0.5f//On medium take 50%,on Expanded takes 35%
    Row {
        Box(Modifier.weight(scannedDeviceWeight)) {
            UserListPreview()
        }
        Spacer(Modifier.width(16.dp))
        Box(Modifier.weight(1f - scannedDeviceWeight).fillMaxHeight()) {
            ChatUIPreview(
                showNavigationIcon = false,
                viewModel = viewModel
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Suppress("Unused")
@Composable
fun ChatUIPreview(
    viewModel: ChatViewModel,
    showNavigationIcon: Boolean,
    onExitRequest: () -> Unit = {},
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (showNavigationIcon) {
                        IconButton(
                            onClick = onExitRequest
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Navigate back")
                        }
                    }
                }
            )
        }
    ) {
        Box(Modifier.fillMaxSize()){
            ChatRoute(
                modifier = Modifier.padding(it),
                controller = viewModel
            )
            if (viewModel.waitingForResponse.collectAsState().value){
                CircularProgressIndicator(Modifier.size(50.dp).align(Alignment.Center))
            }
        }

    }

}






/*
TODO: Participant list preview
TODO: Participant list preview
TODO: Participant list preview

 */

@Composable
fun UserListPreview(
    onClick: (Participant) -> Unit = {}
) {
    UserListRoute(
        participants = _getDummyParticipants(),
        onClick = onClick
    )
}


@Suppress("FunctionName")
private fun _getDummyParticipants() = listOf(
    Participant(
        title = "Chat Bot",
        lastMsg = "No message...",
        timeStamp = System.currentTimeMillis(),
        isGroupMessage = false
    ),

    )


/*
TODO:
 */

