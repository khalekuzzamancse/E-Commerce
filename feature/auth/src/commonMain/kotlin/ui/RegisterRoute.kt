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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max


/**
 * Putting all code in single file so that can direcly copy past to other project
 * and used as library
 */

@Composable
fun RegisterRoute(
    controller: RegistrationController,
    onEvent: (RegisterEvent) -> Unit,
) {
    val data = controller.data.collectAsState().value
    _RegisterDestination(
        data = data,
        formEvent = controller.event,
        onRegisterRequest = {
            onEvent(
                RegisterEvent.RegisterRequest(
                    name = data.name,
                    email = data.email,
                    password = data.password
                )
            )
        },
        onLoginRequest = {
            onEvent(RegisterEvent.LoginRequest)
        }
    )
}

object RegisterFactory {
    fun createController() = RegistrationController()
}

class RegistrationController internal constructor() {
    private val _data = MutableStateFlow(
        RegistrationFormData(
            name = "khalek",
            email = "khale@just.edu.bd",
            password = "test@123",
            confirmPassword = "test@123"
        )
    )
    val data = _data.asStateFlow()
    val event = RegisterFormEvent(
        onNameChanged = ::onNameChanged,
        onEmailChanged = ::onEmailChanged,
        onPasswordChanged = ::onPasswordChanged,
        onConfirmedPassword = ::onConfirmedPasswordChanged
    )

    private fun onNameChanged(fullName: String) {
        _data.update { it.copy(name = fullName) }
    }


    private fun onEmailChanged(email: String) {
        _data.update { it.copy(email = email) }
    }


    private fun onPasswordChanged(password: String) {
        _data.update { it.copy(password = password) }
    }

    private fun onConfirmedPasswordChanged(confirmedPassword: String) {
        _data.update { it.copy(confirmPassword = confirmedPassword) }
    }


}

@Composable
private fun _RegisterDestination(
    modifier: Modifier = Modifier,
    data: RegistrationFormData,
    formEvent: RegisterFormEvent,
    onRegisterRequest: () -> Unit,
    onLoginRequest: () -> Unit,
) {
    _RegisterFormNControls(
        modifier = modifier,
        onLoginRequest = onLoginRequest,
        data = data,
        onRegisterRequest = onRegisterRequest,
        event = formEvent
    )

}

interface RegisterEvent {
    data class RegisterRequest(
        val name: String,
        val email: String,
        val password: String
    ) : RegisterEvent

    data object LoginRequest : RegisterEvent
}


@Composable
private fun _RegisterFormNControls(
    modifier: Modifier,
    data: RegistrationFormData,
    event: RegisterFormEvent,
    onRegisterRequest: () -> Unit,
    onLoginRequest: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        //wrapping the form with box to make it centered
        //liming the max width of the form to avoid bug because there is a bug on measurement form
        Box(Modifier.widthIn(max = 1000.dp).align(Alignment.CenterHorizontally)) {
            RegistrationForm(
                modifier = Modifier.fillMaxWidth(),
                fieldModifier = Modifier.fillMaxWidth(),
                data = data,
                event = event
            )
        }
        Spacer(Modifier.height(24.dp))
        Column(Modifier.padding(start = 16.dp).align(Alignment.CenterHorizontally)) {
            LoginSection(
                modifier = Modifier,
                onLoginRequest = onLoginRequest
            )
        }
        RegistrationControls(
            modifier = Modifier.widthIn(min = 200.dp, max = 300.dp)
                .align(Alignment.CenterHorizontally),
            onRegistrationRequest = onRegisterRequest
        )

    }
}
/*
TODO :Control section

 */


@Composable
private fun RegistrationControls(
    modifier: Modifier,
    onRegistrationRequest: () -> Unit,
) {
    Button(
        modifier = modifier,
        elevation = ButtonDefaults
            .buttonElevation(defaultElevation = 8.dp, focusedElevation = 8.dp),
        onClick = onRegistrationRequest
    ) {
        Text(text = "Register".uppercase())
    }
}

@Composable
private fun LoginSection(
    modifier: Modifier,
    onLoginRequest: () -> Unit,
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max), // add this modifier
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already Have an account ?")
            Spacer(Modifier.width(4.dp))
            TextButton(onClick = onLoginRequest) {
                Text(
                    text = "Login"
                )
            }
        }

    }


}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun RegistrationForm(
    modifier: Modifier = Modifier,
    fieldModifier: Modifier,
    data: RegistrationFormData,
    event: RegisterFormEvent,
) {
    val formPadding = 8.dp
    val windowSize = calculateWindowSizeClass()
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            Surface(
                modifier = modifier,
                shadowElevation = 6.dp,
            ) {
                CompactModeRegistrationForm(
                    modifier = Modifier.padding(formPadding).fillMaxWidth(),
                    data = data,
                    fieldModifier = fieldModifier,
                    event = event

                )
            }
        }

        WindowWidthSizeClass.Expanded, WindowWidthSizeClass.Medium -> {
            Surface(
                modifier = modifier,
                shadowElevation = 6.dp,
            ) {
                NonCompactModeRegistrationForm(
                    modifier = Modifier.padding(formPadding).fillMaxWidth(),
                    data = data,
                    fieldModifier = Modifier.fillMaxWidth(),
                    event = event
                )
            }
        }
    }


}


/*

TODO: NON Compact Mode Form -- NON Compact Mode Form -- TNON Compact Mode Form --
TODO: NON Compact Mode Form -- NON Compact Mode Form -- TNON Compact Mode Form --

 */

@Composable
private fun NonCompactModeRegistrationForm(
    modifier: Modifier = Modifier,
    fieldModifier: Modifier,
    data: RegistrationFormData,
    event: RegisterFormEvent
) {

    NonCompactModeRegistrationForm(
        modifier = modifier,
        fieldModifier = fieldModifier,
        name = data.name,
        onNameChanged = event.onNameChanged,
        email = data.email,
        onEmailChanged = event.onEmailChanged,
        password = data.password,
        onPasswordChanged = event.onPasswordChanged,
        confirmedPassword = data.confirmPassword,
        onConfirmedPassword = event.onConfirmedPassword
    )

}

/*
As per the custom layout measurements it will takes the full width,
so when use it define a max width for it
 */


@Composable
private fun NonCompactModeRegistrationForm(
    modifier: Modifier = Modifier,
    fieldModifier: Modifier,
    name: String,
    onNameChanged: (String) -> Unit,
    email: String,
    onEmailChanged: (String) -> Unit,
    password: String,
    onPasswordChanged: (String) -> Unit,
    confirmedPassword: String,
    onConfirmedPassword: (String) -> Unit,
) {
    _FormLayout(
        eachRow1stChildMaxWidth = 200.dp,
        verticalGap = 8.dp,
        horizontalGap = 4.dp,
        modifier = modifier,
    ) {
        Text(text = RegistrationFormLabels.FULL_NAME)
        _AuthTextField(
            modifier = fieldModifier,
            value = name,
            onValueChanged = onNameChanged,
            keyboardType = KeyboardType.Text,
            leadingIcon = Icons.Default.Person,
        )
        Text(text = RegistrationFormLabels.EMAIL)
        _AuthTextField(
            modifier = fieldModifier,
            value = email,
            onValueChanged = onEmailChanged,
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Default.Email,
        )

        Text(text = RegistrationFormLabels.PASSWORD)
        _AuthPasswordField(
            modifier = fieldModifier,
            value = password,
            onValueChanged = onPasswordChanged,
        )
        Text(text = RegistrationFormLabels.CONFIRMED_PASSWORD)
        _AuthPasswordField(
            modifier = fieldModifier,
            value = confirmedPassword,
            onValueChanged = onConfirmedPassword,
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


/*
TODO: Compact Mode Form -- TODO: Compact Mode Form -- TODO: Compact Mode Form --
TODO: Compact Mode Form -- TODO: Compact Mode Form -- TODO: Compact Mode Form --

 */





@Composable
private fun CompactModeRegistrationForm(
    modifier: Modifier = Modifier,
    fieldModifier: Modifier,
    data: RegistrationFormData,
    event: RegisterFormEvent,
) {

    RegistrationForm(
        modifier = modifier,
        fieldModifier = fieldModifier,
        name = data.name,
        onNameChanged = event.onNameChanged,
        email = data.email,
        onEmailChanged = event.onEmailChanged,
        password = data.password,
        onPasswordChanged = event.onPasswordChanged,
        confirmedPassword = data.confirmPassword,
        onConfirmedPassword = event.onConfirmedPassword
    )

}

data class RegisterFormEvent(
    val onNameChanged: (String) -> Unit,
    val onEmailChanged: (String) -> Unit,
    val onPasswordChanged: (String) -> Unit,
    val onConfirmedPassword: (String) -> Unit,
)

data class RegistrationFormData(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)

@Composable
private fun RegistrationForm(
    modifier: Modifier = Modifier,
    fieldModifier: Modifier,
    name: String,
    onNameChanged: (String) -> Unit,
    email: String,
    onEmailChanged: (String) -> Unit,
    password: String,
    onPasswordChanged: (String) -> Unit,
    confirmedPassword: String,
    onConfirmedPassword: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        _AuthTextField(
            modifier = fieldModifier,
            label = RegistrationFormLabels.FULL_NAME,
            value = name,
            onValueChanged = onNameChanged,
            keyboardType = KeyboardType.Text,
            leadingIcon = Icons.Default.Person,
        )


        _AuthTextField(
            modifier = fieldModifier,
            label = RegistrationFormLabels.EMAIL,
            value = email,
            onValueChanged = onEmailChanged,
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Default.Email,
        )




        _AuthPasswordField(
            modifier = fieldModifier,
            label = RegistrationFormLabels.PASSWORD,
            value = password,
            onValueChanged = onPasswordChanged,
        )

        _AuthPasswordField(
            modifier = fieldModifier,
            label = RegistrationFormLabels.CONFIRMED_PASSWORD,
            value = confirmedPassword,
            onValueChanged = onConfirmedPassword,
        )


    }

}


private object RegistrationFormLabels {
    const val FULL_NAME = "Name"
    const val EMAIL = "Email"
    const val PASSWORD = "Password"
    const val CONFIRMED_PASSWORD = "Confirm Password"
}


/*
TODO: Reusable component section -- Reusable content section --
TODO: Reusable component section -- Reusable content section --
TODO: Reusable component section -- Reusable content section --
TODO: Reusable component section -- Reusable content section --
TODO: Reusable component section -- Reusable content section --
 */


@Composable
private fun _AuthDropDownMenu(
    modifier: Modifier,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    label: String? = null,
    selected: String,
) {

    Column {
        if (label != null) {
            Text(
                text = label,
            )
            Spacer(Modifier.height(8.dp))
        }

        _MyDrop2(
            modifier = modifier,
            options = options,
            selected = selected, onOptionSelected = onOptionSelected,
            leadingIcon = Icons.Default.School
        )
    }


}

@Composable
private fun _MyDrop2(
    modifier: Modifier,
    options: List<String>,
    leadingIcon: ImageVector? = null,
    selected: String,
    onOptionSelected: (String) -> Unit
) {
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    )
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Box {
        TextField(
            modifier = modifier.onGloballyPositioned { coordinates ->
                textFieldSize = coordinates.size.toSize()
            },
            readOnly = true,
            value = selected,
            onValueChange = onOptionSelected,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        isExpanded = true
                    }
                )
            },
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                    )
                }
            },
            colors = colors,
            shape = RoundedCornerShape(8.dp)
        )
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
            offset = DpOffset.Zero.copy(
                y = -((with(LocalDensity.current) { textFieldSize.height.toDp() }))
            )
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = {
                        Text(text = it)
                    },
                    onClick = {
                        onOptionSelected(it)
                        isExpanded = false
                    }
                )
            }
        }
    }


}

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