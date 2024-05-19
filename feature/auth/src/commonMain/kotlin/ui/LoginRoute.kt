package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max

/**
 * Putting all code in single file so that can direcly copy past to other project
 * and used as library
 */


@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    controller: LoginFormController,
    onEvent: (LoginEvent) -> Unit,
) {
    _LoginFormNControls(
        modifier = modifier,
        data = controller.data.collectAsState().value,
        onControlEvent = onEvent,
        formEvent = controller.event
    )
}

object LoginFactory {
    fun createLoginFormController() = LoginFormController()
}

class LoginFormController internal constructor() {
    val event = LoginFormEvent(
        onUserNameChanged = ::onUserNameChanged,
        onPasswordChanged = ::onPasswordChanged
    )
    private val _data = MutableStateFlow(
        FormData(
            username = "smsourav", password = "test@123"
        )
    )
    val data = _data.asStateFlow()
    private fun onUserNameChanged(username: String) {
        _data.update { it.copy(username = username) }
    }

    private fun onPasswordChanged(password: String) {
        _data.update { it.copy(password = password) }
    }


}


//*
data class LoginFormEvent(
    val onUserNameChanged: (String) -> Unit,
    val onPasswordChanged: (String) -> Unit,
)


/*
TODO: Login Route stateless
 */
/**
 * A [Stateless Component]
 * For the Login destination.
 * @param modifier a [Modifier] (optional)
 * @param data for the  [LoginFormData]
 * @param event for the  [LoginFormEvent]
 */
@Composable
private fun _LoginFormNControls(
    modifier: Modifier = Modifier,
    data: FormData,
    formEvent: LoginFormEvent,
    onControlEvent: (LoginEvent) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier,
            shadowElevation = 6.dp,
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Box(Modifier.align(Alignment.CenterHorizontally)) {
                    _LoginForm(
                        data = data,
                        fieldModifier = Modifier.fillMaxWidth(),
                        event = formEvent
                    )
                }
                Spacer(Modifier.height(16.dp))
                _LoginControls(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onLoginRequest = {
                        onControlEvent(LoginEvent.LoginRequest(data.username, data.password))
                    },
                    onPasswordResetRequest = {
                        onControlEvent(LoginEvent.PasswordResetRequest)
                    },
                    onRegisterRequest = {
                        onControlEvent(LoginEvent.RegisterRequest)
                    }

                )

            }
        }
    }
}


/**
 * * Wrapping the event hierarchy within a single interface so that after some time
 * it is easy to find which event is   occurs,it it is easy to access the event name from the
 * client code or module.
 * * Refactor it if needed
 * * Available Events
 *
 */
interface LoginEvent {
    data class LoginRequest(val username: String, val password: String) : LoginEvent
    data object PasswordResetRequest : LoginEvent
    data object RegisterRequest : LoginEvent

}

/**
 * TODO:Login Control section
 */
@Composable
private fun _LoginControls(
    modifier: Modifier,
    onPasswordResetRequest: () -> Unit,
    onLoginRequest: () -> Unit,
    onRegisterRequest: () -> Unit,

    ) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        _ForgetPassword(
            modifier = Modifier.align(Alignment.End),
            onPasswordResetRequest = onPasswordResetRequest
        )
        _VerticalSpacer()
        _LoginOrSignUp(
            modifier = Modifier.padding(start = 16.dp),
            onRegisterRequest = onRegisterRequest,
            onLoginRequest = onLoginRequest
        )

    }


}


@Composable
private fun _LoginOrSignUp(
    modifier: Modifier,
    onRegisterRequest: () -> Unit,
    onLoginRequest: () -> Unit,
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max), // add this modifier
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.width(16.dp))
        Row(
            modifier = Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Don't Have an account ?")
            Spacer(Modifier.width(4.dp))
            TextButton(onClick = onRegisterRequest) {
                Text(
                    text = "Register"
                )
            }
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onLoginRequest
        ) {
            Text(text = "Login".uppercase())
        }

    }


}

@Composable
private fun _ForgetPassword(
    modifier: Modifier,
    onPasswordResetRequest: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onPasswordResetRequest) {
            Text(
                text = "Forget Password ?",
            )
        }
    }
}


@Composable
private fun _VerticalSpacer() {
    Spacer(
        modifier = Modifier
            .height(8.dp)

    )
}


//TODO: Login form section

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun _LoginForm(
    fieldModifier: Modifier = Modifier,
    formModifier: Modifier = Modifier,
    data: FormData,
    event: LoginFormEvent,
) {
    val windowSize = calculateWindowSizeClass()
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            _CompactScreenLoginForm(
                data = data,
                event = event,
                fieldModifier = fieldModifier,
                formModifier = formModifier,

                )
        }

        WindowWidthSizeClass.Expanded, WindowWidthSizeClass.Medium -> {
            _NonCompactScreenLoginForm(
                data = data,
                event = event,
                fieldModifier = Modifier.fillMaxWidth(),
                formModifier = formModifier,
            )
        }
    }


}


//TODO: Section
//
@Composable
private fun _NonCompactScreenLoginForm(
    fieldModifier: Modifier = Modifier,
    formModifier: Modifier = Modifier,
    data: FormData,
    event: LoginFormEvent,
) {
    NonCompactScreenLoginForm(
        fieldModifier = fieldModifier,
        formModifier = formModifier,
        userName = data.username,
        password = data.password,
        onUserNameChanged = event.onUserNameChanged,
        onPasswordChanged = event.onPasswordChanged
    )

}

@Composable
private fun NonCompactScreenLoginForm(
    fieldModifier: Modifier = Modifier,
    formModifier: Modifier = Modifier,
    userName: String,
    onUserNameChanged: (String) -> Unit,
    password: String,
    onPasswordChanged: (String) -> Unit,
) {
    _FormLayout(
        eachRow1stChildMaxWidth = 200.dp,
        verticalGap = 8.dp,
        horizontalGap = 4.dp,
        modifier = formModifier.fillMaxWidth(),
    ) {
        Text("User Name")
        _AuthTextField(
            modifier = fieldModifier,
            value = userName,
            onValueChanged = onUserNameChanged,
            keyboardType = KeyboardType.Text,
            leadingIcon = Icons.Default.Person4,
        )
        Text("Password")
        _AuthPasswordField(
            modifier = fieldModifier,
            value = password,
            onValueChanged = onPasswordChanged,
        )

    }


}


//TODO: FormLayout section --  FormLayout section --


@Composable
private fun _FormLayout(
    eachRow1stChildMaxWidth: Dp,
    verticalGap: Dp,
    horizontalGap: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier, content = content
    ) { measurables, constraints ->

        if (measurables.isEmpty())
            throw IllegalArgumentException("total item can not be 0")
        if (measurables.size % 2 != 0)
            throw IllegalArgumentException("total item can not be odd")

        val firstColumnChildMeasureAbles = measurables.filterIndexed { i, _ -> i % 2 == 0 }
        val secondColumnChildMeasureAbles = measurables.filterIndexed { i, _ -> i % 2 == 1 }
        val eachRow1stChildConstraint = Constraints(
            minWidth = 0,
            minHeight = 0,
            maxWidth = eachRow1stChildMaxWidth.toPx().toInt(),
            maxHeight = Constraints.Infinity
        )
        val firstColumnChildPlaceAbles = firstColumnChildMeasureAbles.map { measurable ->
            measurable.measure(eachRow1stChildConstraint)
        }
        val firstColumnWidth =
            firstColumnChildPlaceAbles.maxOf { it.width } + horizontalGap.toPx().toInt()
        val secondColumnWidth = constraints.maxWidth - firstColumnWidth
        val eachRowSecondChildConstraint = Constraints(
            minWidth = 0,
            minHeight = 0,
            maxWidth = secondColumnWidth,
            maxHeight = Constraints.Infinity
        )
        val secondColumnChildPlaceAbles = secondColumnChildMeasureAbles.map { measurable ->
            measurable.measure(eachRowSecondChildConstraint)
        }
        val totalVerticalGap = verticalGap * firstColumnChildMeasureAbles.size
        val layoutHeight = max(firstColumnChildPlaceAbles.sumOf { it.height },
            secondColumnChildPlaceAbles.sumOf { it.height }) + totalVerticalGap.toPx().toInt()
        val layoutWidth = firstColumnChildPlaceAbles.maxBy { it.width }.width +
                secondColumnChildPlaceAbles.maxBy { it.width }.width + horizontalGap.toPx().toInt()

        layout(width = layoutWidth, height = layoutHeight) {
            var y = 0
            firstColumnChildPlaceAbles.forEachIndexed { i, label ->
                val textField = secondColumnChildPlaceAbles[i]
                //Height
                val rowHeight = max(label.height, textField.height) + verticalGap.toPx().toInt()
                val eachRow1stChildMoveDown = (rowHeight - label.height) / 2
                val eachRow2ndChildMoveDown = (rowHeight - textField.height) / 2
                label.placeRelative(0, y + eachRow1stChildMoveDown)
                textField.placeRelative(firstColumnWidth, y + eachRow2ndChildMoveDown)
                y += rowHeight
            }

        }
    }
}


//TODO: CompactScreenLoginForm section --  CompactScreenLoginForm section --  CompactScreenLoginForm section
//TODO:  CompactScreenLoginForm section --  CompactScreenLoginForm section --  CompactScreenLoginForm section
@Composable
private fun _CompactScreenLoginForm(
    fieldModifier: Modifier = Modifier,
    formModifier: Modifier = Modifier,
    data: FormData,
    event: LoginFormEvent,
) {
    _CompactScreenLoginForm(
        fieldModifier = fieldModifier,
        formModifier = formModifier,
        userName = data.username,
        password = data.password,
        onUserNameChanged = event.onUserNameChanged,
        onPasswordChanged = event.onPasswordChanged
    )

}

data class FormData(
    val username: String,
    val password: String
)


@Composable
private fun _CompactScreenLoginForm(
    fieldModifier: Modifier = Modifier,
    formModifier: Modifier = Modifier,
    userName: String,
    onUserNameChanged: (String) -> Unit,
    password: String,
    onPasswordChanged: (String) -> Unit,
) {
    Column(
        modifier = formModifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        _AuthTextField(
            modifier = fieldModifier,
            label = "User Name",
            value = userName,
            onValueChanged = onUserNameChanged,
            keyboardType = KeyboardType.Text,
            leadingIcon = Icons.Default.Person4,
        )

        _AuthPasswordField(
            modifier = fieldModifier,
            label = "Password",
            value = password,
            onValueChanged = onPasswordChanged,
        )


    }


}


//TODO: Auth Filed Section -- Auth Filed Section -- Auth Filed Section
//TODO: Auth Filed Section -- Auth Filed Section -- Auth Filed Section
@Composable
private fun _AuthPasswordField(
    modifier: Modifier,
    label: String? = null,
    value: String,
    onValueChanged: (String) -> Unit,
    errorMessage: String? = null,
    shape: Shape = TextFieldDefaults.shape,
) {
    var showPassword by remember { mutableStateOf(true) }
    val trailingIcon = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff
    val leadingIcon = remember { Icons.Default.Lock }
    val keyboardType = remember { KeyboardType.Password }
    val visualTransformation =
        if (showPassword) VisualTransformation.None else PasswordVisualTransformation()
    val onTogglePassword: () -> Unit = remember {
        { showPassword = !showPassword }
    }
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    )
    val content: @Composable ColumnScope.() -> Unit =
        if (errorMessage == null) {
            @Composable {
                if (label != null) {
                    Text(text = label)
                }
                TextField(
                    label = null,
                    shape = shape,
                    modifier = modifier,
                    singleLine = true,
                    value = value,
                    onValueChange = onValueChanged,
                    leadingIcon = {
                        Icon(imageVector = leadingIcon, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                onTogglePassword()
                            })
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                    visualTransformation = visualTransformation,
                    colors = colors,
                )
            }
        } else {
            @Composable {
                if (label != null) {
                    Text(text = label)
                }
                TextField(
                    label = null,
                    shape = shape,
                    modifier = modifier,
                    singleLine = true,
                    value = value,
                    onValueChange = onValueChanged,
                    leadingIcon = {
                        Icon(imageVector = leadingIcon, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                onTogglePassword()
                            })
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                    visualTransformation = visualTransformation,
                    colors = colors,
                    isError = true,
                    supportingText = {
                        Text(
                            text = errorMessage
                        )
                    },
                )
            }


        }
    Column(modifier = modifier) {
        content()
    }
}

@Composable
private fun _AuthTextField(
    modifier: Modifier,
    label: String? = null,
    value: String,
    leadingIcon: ImageVector?,
    keyboardType: KeyboardType,
    shape: Shape = TextFieldDefaults.shape,
    errorMessage: String? = null,
    onValueChanged: (String) -> Unit,
) {
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    )
    val content: @Composable ColumnScope.() -> Unit = if (errorMessage == null) @Composable {
        {
            if (label != null) {
                Text(text = label)
            }
            TextField(
                label = null,
                shape = shape,
                modifier = modifier,
                value = value,
                onValueChange = onValueChanged,
                leadingIcon = {
                    if (leadingIcon != null) {
                        Icon(imageVector = leadingIcon, contentDescription = null)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                colors = colors,
            )

        }
    } else @Composable {
        {
            if (label != null) {
                Text(text = label)
            }
            TextField(
                label = null,
                shape = shape,
                modifier = modifier,
                value = value,
                onValueChange = onValueChanged,
                leadingIcon = {
                    if (leadingIcon != null) {
                        Icon(imageVector = leadingIcon, contentDescription = null)
                    }
                },

                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                colors = colors,
                isError = true,
                supportingText = {
                    Text(
                        text = errorMessage
                    )
                },

                )
        }

    }
    Column(modifier = modifier) {
        content()
    }


}