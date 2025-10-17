package com.thecodefather.untigrito.presentation.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Reusable Application Button Component
 *
 * Custom button component following app design standards.
 *
 * @param text The text to display on the button
 * @param onClick Callback when button is clicked
 * @param modifier Modifier to apply to the button
 * @param enabled Whether the button is enabled
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(text)
    }
}
