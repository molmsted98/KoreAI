package link.socket.kore.ui.widget

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import kotlinx.coroutines.delay

/**
 * A composable function that displays a small Snackbar.
 *
 * @param modifier The modifier to be applied to the Snackbar.
 * @param snackbarHostState The state of the Snackbar host, which controls the display of Snackbars.
 */
@Composable
fun SmallSnackbarHost(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
) {
    val currentSnackbarData = snackbarHostState.currentSnackbarData
    val accessibilityManager = LocalAccessibilityManager.current

    // Launch a coroutine to handle the Snackbar's display duration and dismissal
    LaunchedEffect(currentSnackbarData) {
        if (currentSnackbarData != null) {
            val duration = currentSnackbarData.duration.toMillis(
                currentSnackbarData.actionLabel != null,
                accessibilityManager
            )
            delay(duration)
            currentSnackbarData.dismiss()
        }
    }

    // Display the Snackbar if there is current Snackbar data
    currentSnackbarData?.let { data ->
        Snackbar(
            modifier = modifier.wrapContentWidth(),
            snackbarData = data,
        )
    }
}

/**
 * Extension function to convert SnackbarDuration to milliseconds, considering accessibility settings.
 *
 * @param hasAction Boolean indicating if the Snackbar has an action button.
 * @param accessibilityManager The AccessibilityManager to use for calculating recommended timeout.
 * @return The duration in milliseconds.
 */
internal fun SnackbarDuration.toMillis(
    hasAction: Boolean,
    accessibilityManager: AccessibilityManager?
): Long {
    val original = when (this) {
        SnackbarDuration.Indefinite -> Long.MAX_VALUE
        SnackbarDuration.Long -> 10000L
        SnackbarDuration.Short -> 4000L
    }
    if (accessibilityManager == null) {
        return original
    }
    return accessibilityManager.calculateRecommendedTimeoutMillis(
        original,
        containsIcons = true,
        containsText = true,
        containsControls = hasAction
    )
}
