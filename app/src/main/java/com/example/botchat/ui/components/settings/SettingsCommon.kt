package com.example.botchat.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.R
import com.example.botchat.ui.theme.*

@Composable
fun SettingsSwitchItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            modifier = Modifier.scale(0.85f)
        )
    }
}

@Composable
fun ApiKeyInput(
    apiKey: String,
    showApiKey: Boolean,
    onApiKeyChange: (String) -> Unit,
    onVisibilityToggle: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = apiKey,
            onValueChange = onApiKeyChange,
            label = {
                Text(
                    label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        painter = painterResource(id = if (showApiKey) R.drawable.visibility else R.drawable.visibility_off),
                        contentDescription = "Toggle API Key Visibility",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Transparent,
                unfocusedContainerColor = Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                errorCursorColor = MaterialTheme.colorScheme.error,
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                errorTrailingIconColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                errorLabelColor = MaterialTheme.colorScheme.error,
                focusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                disabledSupportingTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                errorSupportingTextColor = MaterialTheme.colorScheme.error,
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(16.dp),
            supportingText = if (apiKey.isBlank()) {
                {
                    Text(
                        "API Key is required",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            } else null
        )
    }
}

@Composable
fun ModelSelectionInput(
    selectedModel: String,
    onModelChange: (String) -> Unit,
    models: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = selectedModel,
            onValueChange = { /* Read-only */ },
            label = {
                Text(
                    "Model",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Select Model",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Transparent,
                unfocusedContainerColor = Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                errorCursorColor = MaterialTheme.colorScheme.error,
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                errorTrailingIconColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                errorLabelColor = MaterialTheme.colorScheme.error,
                focusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                disabledSupportingTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                errorSupportingTextColor = MaterialTheme.colorScheme.error,
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(16.dp),
            supportingText = if (selectedModel.isBlank()) {
                {
                    Text(
                        "Model is required",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            } else null
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .width(IntrinsicSize.Max)
                .heightIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                models.forEach { model ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                model,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (model == selectedModel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        },
                        onClick = {
                            onModelChange(model)
                            expanded = false
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (model == selectedModel) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else Transparent
                            )
                    )
                }
            }
        }
    }
}