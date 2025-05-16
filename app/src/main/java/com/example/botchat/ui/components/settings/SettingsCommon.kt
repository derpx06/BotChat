package com.example.botchat.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (MaterialTheme.colorScheme.background == MidnightBlack) MidnightBlack else CloudWhite
            )
            .border(
                1.5.dp,
                if (MaterialTheme.colorScheme.background == MidnightBlack) ElectricCyan else Purple40,
                RoundedCornerShape(24.dp)
            )
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
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (MaterialTheme.colorScheme.background == MidnightBlack) MidnightBlack else CloudWhite
            )
            .border(
                1.5.dp,
                if (MaterialTheme.colorScheme.background == MidnightBlack) ElectricCyan else Purple40,
                RoundedCornerShape(24.dp)
            )
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
            shape = RoundedCornerShape(20.dp),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSelectionInput(
    selectedModel: String,
    onModelChange: (String) -> Unit,
    models: List<String>,
    onAddNewModel: @Composable () -> Unit={}, // Lambda to handle adding a new model
    onDeleteModel: (String) -> Unit={}, // Lambda to handle deleting a model
    modifier: Modifier = Modifier,
    navController: NavController= NavController(LocalContext.current)
) {
    var expanded by remember { mutableStateOf(false) }
    var isTextFieldFocused by remember { mutableStateOf(false) } // Track text field focus

    // Using ExposedDropdownMenuBox for standard Material 3 dropdown
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
                clip = true
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (MaterialTheme.colorScheme.background == MidnightBlack) MidnightBlack else CloudWhite // Keep custom background if desired
            )
            .border(
                1.5.dp,
                if (MaterialTheme.colorScheme.background == MidnightBlack) ElectricCyan else Purple40, // Keep custom border if desired
                RoundedCornerShape(24.dp)
            )
            .padding(16.dp) // Padding inside the box, not the text field
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
            readOnly = true,
            trailingIcon = {
                // Use the appropriate icon based on expanded state
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                // Use MaterialTheme colors where possible for consistency
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
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
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                // Remove the default indicator color provided by ExposedDropdownMenuBox
                // if you want to fully control the border with your custom border modifier
                // indicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .menuAnchor() // Required for ExposedDropdownMenuBox
                .fillMaxWidth()
            // Add focus indication if needed, though ExposedDropdownMenuBox handles some
            //.onFocusEvent { focusState -> isTextFieldFocused = focusState.isFocused }
            ,
            shape = RoundedCornerShape(20.dp), // Match the outer shape
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

        // The dropdown menu itself
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .shadow(
                    elevation = 8.dp, // Increased elevation for better visual separation
                    shape = RoundedCornerShape(12.dp), // Slightly less rounded for the dropdown
                    clip = true
                )
                .background(MaterialTheme.colorScheme.surface) // Use surface color
                .clip(RoundedCornerShape(12.dp))
                .border( // Optional: add a subtle border to the dropdown
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    RoundedCornerShape(12.dp)
                )
                .width(IntrinsicSize.Max)
                .heightIn(max = 300.dp) // Limit height and make it scrollable
        ) {
            Column {
                // "Add New Model" Button
                DropdownMenuItem(
                    text = {
                        Text(
                            "Add New Model",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        onAddNewModel
                       expanded = false // Close dropdown after clicking
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add New Model",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)) // Divider

                // Model list with delete button
                models.forEach { model ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                model,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (model == selectedModel) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (model == selectedModel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onModelChange(model)
                            expanded = false // Close dropdown after selection
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (model == selectedModel) MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.1f
                                )
                                else Color.Transparent
                            )
                        ,
                        // Adding trailing icon for delete
                        trailingIcon = {
                            // Only show delete icon if there are multiple models,
                            // preventing deletion of the last model if that's a requirement.
                            // Adjust this logic based on your needs.
                            if (models.size > 1) {
                                IconButton(
                                    onClick = {
                                        onDeleteModel(model)
                                        // No need to close the dropdown here, as the list will update
                                    },
                                    modifier = Modifier.size(24.dp) // Adjust size as needed
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Model: $model",
                                        tint = MaterialTheme.colorScheme.error // Use error color for delete
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}