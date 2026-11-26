package com.gondroid.subtrack.core.designsystem.components.feedback

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import kotlinx.coroutines.launch

/**
 * App-wide messenger provided by MainApp. It lives in MainApp's scope so a message
 * survives the screen that posted it popping right after (e.g. "Guardado" + onBack).
 */
val LocalSnackbarMessenger = staticCompositionLocalOf { SnackbarMessenger(resources = null) {} }

@Composable
fun STSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        STSnackbar(message = data.visuals.message)
    }
}

@Composable
private fun STSnackbar(message: String) {
    Snackbar(
        modifier = Modifier
            .padding(horizontal = Spacing.base, vertical = Spacing.s)
            .border(1.dp, BorderDefault, SubTrackShapes.l),
        shape = SubTrackShapes.l,
        containerColor = BgSurfaceEl,
        contentColor = TextPrimary
    ) {
        Text(message, style = SubTrackType.bodyS, color = TextPrimary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun STSnackbarPreview() {
    SubTrackTheme { STSnackbar(message = "Plantilla duplicada") }
}

class SnackbarMessenger(
    private val resources: Resources?,
    private val post: (String) -> Unit
) {
    fun show(message: String) = post(message)

    fun show(@StringRes messageRes: Int) {
        resources?.getString(messageRes)?.let(post)
    }
}

/** Creates the messenger MainApp provides through [LocalSnackbarMessenger]. */
@Composable
fun rememberSnackbarMessenger(hostState: SnackbarHostState): SnackbarMessenger {
    val scope = rememberCoroutineScope()
    val resources = LocalResources.current
    return remember(hostState, scope, resources) {
        SnackbarMessenger(resources) { message -> scope.launch { hostState.showSnackbar(message) } }
    }
}
