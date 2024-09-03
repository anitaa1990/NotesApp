package com.an.notesapp.ui.component

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * An interface that provides methods to display snack bars.
 */
@Immutable
interface SnackbarController {
    /**
     * Displays a text message.
     *
     * @param message text to be shown in the SnackBar.
     * @param duration duration of the SnackBar.
     */
    fun showMessage(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short
    )
}

/**
 * Returns a [SnackbarController] that uses the given [SnackbarHostState] and [coroutineScope] to display SnackBars.
 *
 * @param snackbarHostState The [SnackbarHostState] to use.
 * @param coroutineScope The [CoroutineScope] to use.
 * @return A [SnackbarController] that uses the given [SnackbarHostState] and [coroutineScope] to display SnackBars.
 */
@Stable
fun SnackbarController(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
): SnackbarController = SnackbarControllerImpl(
    snackbarHostState = snackbarHostState,
    coroutineScope = coroutineScope
)

/**
 * Provides a [SnackbarController] to its content.
 *
 * @param snackbarHostState The [SnackbarHostState] to use.
 * @param coroutineScope The [CoroutineScope] to use.
 * @param content The content that will have access to the [SnackbarController].
 */
@Composable
fun ProvideSnackbarController(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSnackbarController provides SnackbarController(
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope
        ),
        content = content
    )
}

/**
 * Handles a [SnackbarMessage] by showing a Snackbar message
 */
@Composable
fun SnackbarMessageHandler(
    snackbarMessage: SnackbarMessage?,
    snackbarController: SnackbarController = LocalSnackbarController.current
) {
    if (snackbarMessage == null) return

    val message = stringResource(id = snackbarMessage.resourceId)
    LaunchedEffect(snackbarMessage) {
        snackbarController.showMessage(
            message = message,
            duration = snackbarMessage.duration
        )
    }
}

/**
 * Implementation of the [SnackbarController] interface that uses a [SnackbarHostState] to show
 * Snackbar messages. The [coroutineScope] is used to launch coroutines for showing Snackbar messages.
 *
 * @param snackbarHostState The [SnackbarHostState] used to show Snackbar messages.
 * @param coroutineScope The [CoroutineScope] used to launch coroutines for showing Snackbar messages.
 */
@Immutable
private class SnackbarControllerImpl(
    private val snackbarHostState: SnackbarHostState,
    private val coroutineScope: CoroutineScope
) : SnackbarController {
    /**
     * Shows a Snackbar message with the given parameters
     */
    override fun showMessage(
        message: String,
        duration: SnackbarDuration
    ) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = duration
            )
        }
    }
}

/**
 * Static CompositionLocal that provides access to a [SnackbarController]. The value of the
 * [LocalSnackbarController] is set using the [CompositionLocalProvider] composable. If no
 * [SnackbarController] is provided, an error is thrown.
 */
val LocalSnackbarController = staticCompositionLocalOf<SnackbarController> {
    error("No SnackBarController provided.")
}

data class SnackbarMessage(
    val resourceId: Int,
    val duration: SnackbarDuration = SnackbarDuration.Short
) {
    companion object {
        /**
         * Returns a new [SnackbarMessage] instance.
         *
         * @param resourceId text to be shown in the Snackbar.
         * @param duration duration of the Snackbar.
         * @return an instance of [SnackbarMessage].
         */
        fun from(
            resourceId: Int,
            duration: SnackbarDuration = SnackbarDuration.Short
        ) = SnackbarMessage(
            resourceId = resourceId,
            duration = duration
        )
    }
}
