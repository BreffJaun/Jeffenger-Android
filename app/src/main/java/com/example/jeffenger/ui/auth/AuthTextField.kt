package com.example.jeffenger.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.theme.UrbanistText


/**
 * Reusable text input field for the authentication flow.
 *
 * Supports:
 * - normal text input
 * - email input
 * - password input with visibility toggle
 * - optional validation indicator
 * - IME actions such as Next and Done
 * - optional focus control via [FocusRequester]
 *
 * The field adapts its keyboard type automatically based on the given
 * configuration and forwards IME actions through [onImeAction].
 *
 * @param value Current text value.
 * @param onValueChange Called when the input text changes.
 * @param label Label shown above the text field.
 * @param placeholder Placeholder shown when the field is empty.
 * @param isPassword Whether the field should behave like a password field.
 * Enables password masking and visibility toggle.
 * @param isValid Optional validation state. If `true`, a check icon is shown.
 * If `false` or `null`, no validation icon is displayed.
 * @param focusRequester Optional [FocusRequester] to request focus programmatically.
 * @param imeAction IME action shown on the keyboard, e.g. Next or Done.
 * @param onImeAction Optional callback triggered when the selected IME action is pressed.
 * @param modifier Optional [Modifier] for styling and layout customization.
 */
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false,
    isValid: Boolean? = null,
    focusRequester: FocusRequester? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {

        // LABEL + Stern
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(bottom = 6.dp)
        ) {
            Text(
                text = label,
                style = UrbanistText.Label,
                color = scheme.onSurfaceVariant
            )
            Text(
                text = "*",
                style = UrbanistText.Label,
                color = scheme.primary
            )
        }

        // INPUT CONTAINER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(
                    color = scheme.tertiaryContainer,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp,
                    color = when {
                        isFocused -> scheme.primary
                        else -> Color.Transparent
                    },
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when {
                            isPassword -> KeyboardType.Password
                            label.contains("E-Mail", ignoreCase = true) -> KeyboardType.Email
                            else -> KeyboardType.Text
                        },
                        imeAction = imeAction,
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { onImeAction?.invoke() },
                        onDone = { onImeAction?.invoke() }
                    ),
                    visualTransformation = when {
                        isPassword && !passwordVisible -> PasswordVisualTransformation()
                        else -> VisualTransformation.None
                    },
                    textStyle = UrbanistText.BodyRegular.copy(
                        color = scheme.onSurface
                    ),
                    cursorBrush = SolidColor(scheme.onSurface),
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (focusRequester != null)
                                Modifier.focusRequester(focusRequester)
                            else Modifier
                        )
                        .background(
                            color = scheme.tertiaryContainer
                        ),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = UrbanistText.Placeholder,
                                color = scheme.outline
                            )
                        }
                        innerTextField()
                    }
                )

                if (isPassword) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Outlined.Visibility
                        else
                            Icons.Outlined.VisibilityOff,
                        contentDescription = "Toggle Password Visibility",
                        tint = if (passwordVisible) scheme.onSurface else scheme.outline,
                        modifier = Modifier
                            .size(25.dp)
                            .padding(end = 8.dp)
                            .clickable {
                                passwordVisible = !passwordVisible
                            }
                    )
                }

                if (isValid == true) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Valid",
                        tint = scheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

