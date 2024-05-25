package chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

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
fun UserListRoute(
    modifier: Modifier = Modifier,
    participants: List<Participant>,
    onClick: (Participant) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .testTag("Participants list"),
    ) {
        itemsIndexed(participants) { index, participant ->
            val isNotLastItem = index != participants.size - 1
            User(
                modifier = Modifier.padding(8.dp),
                participant = participant,
                showDivider = true,
                onClick = {
                    onClick(participant)
                }
            )
            Spacer(Modifier.height(8.dp))

        }

    }
}


//TODO: User section


@Composable
private fun User(
    modifier: Modifier = Modifier,
    participant: Participant,
    showDivider: Boolean,
    onClick: () -> Unit,
) {

    Row(modifier.clickable { onClick() }) {
        _UserImage(userName = participant.title)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier) {
            _TitleNTimeStamp(userName = participant.title, timeStamp = participant.timeStamp)
            _LastMessage(msg = participant.lastMsg)
            if (showDivider) {
                Spacer(Modifier.height(4.dp))
                HorizontalDivider()
            }

        }

    }


}

@Composable
private fun _LastMessage(modifier: Modifier = Modifier, msg: String) {
    Text(
        modifier = modifier,
        text = msg,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )
}

@Composable
private fun _TitleNTimeStamp(modifier: Modifier = Modifier, userName: String, timeStamp: Long) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = userName,
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(1f, true)
        )
        Text(
            text ="",
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Composable
private fun _UserImage(modifier: Modifier = Modifier, userName: String) {
    val bg = MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center


    ) {
        val text = userName.split(" ").take(2).joinToString("") { it.take(1) }
        Text(
            text = text, color = MaterialTheme.colorScheme.contentColorFor(bg)
        )
    }
}

/**
 * @param title is the friend name,for group message use group name,
 */

data class Participant(
    val title: String,
    val lastMsg: String,
    val timeStamp: Long,
    val isGroupMessage: Boolean,
)
