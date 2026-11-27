package com.gondroid.subtrack.feature.profile.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.settings_licenses
import org.jetbrains.compose.resources.stringResource

/** Open source pieces the app ships, with their license (AJU-01). Names are proper nouns, not translated. */
private val OPEN_SOURCE = listOf(
    "Kotlin, kotlinx.coroutines, kotlinx.serialization, kotlinx-datetime" to "Apache License 2.0",
    "Compose Multiplatform, Jetpack Compose, Material 3" to "Apache License 2.0",
    "AndroidX (Room, DataStore, Lifecycle, Navigation, Activity, Browser, Credentials)" to "Apache License 2.0",
    "Koin" to "Apache License 2.0",
    "Coil, Ktor, OkHttp" to "Apache License 2.0",
    "Firebase SDK, Google Sign-In" to "Apache License 2.0",
    "ZXing" to "Apache License 2.0",
    "Space Grotesk, Geist, JetBrains Mono" to "SIL Open Font License 1.1",
    "Simple Icons (service logos)" to "CC0 1.0"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BgSheet,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LicensesContent(modifier = Modifier.padding(horizontal = Spacing.base))
    }
}

@Composable
private fun LicensesContent(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        item {
            Text(stringResource(Res.string.settings_licenses), style = SubTrackType.headlineS, color = TextPrimary)
            Spacer(Modifier.height(Spacing.m))
        }
        items(OPEN_SOURCE) { (name, license) ->
            Column(modifier = Modifier.padding(vertical = Spacing.s)) {
                Text(name, style = SubTrackType.bodyM, color = TextPrimary)
                Text(license, style = SubTrackType.monoXS, color = TextTertiary)
            }
        }
        item { Spacer(Modifier.height(Spacing.xl)) }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121214)
@Composable
private fun LicensesContentPreview() {
    SubTrackTheme { LicensesContent(modifier = Modifier.padding(Spacing.base)) }
}
