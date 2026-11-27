package com.gondroid.subtrack.feature.update

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.theme.BgPage
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.platform.ExternalActions
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.update_required_body
import com.gondroid.subtrack.resources.update_required_cta
import com.gondroid.subtrack.resources.update_required_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** Shown instead of the app when the installed build is below the remote minimum (UPD-02). */
@Composable
fun UpdateRequiredScreen() {
    val externalActions = koinInject<ExternalActions>()
    UpdateRequiredContent(onUpdate = externalActions::openStorePage)
}

@Composable
private fun UpdateRequiredContent(onUpdate: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgPage)
            .safeDrawingPadding()
            .padding(Spacing.l),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(Res.string.update_required_title),
            style = SubTrackType.displayS,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            stringResource(Res.string.update_required_body),
            style = SubTrackType.bodyM,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.xl))
        PrimaryButton(
            text = stringResource(Res.string.update_required_cta),
            onClick = onUpdate,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun UpdateRequiredContentPreview() {
    SubTrackTheme { UpdateRequiredContent(onUpdate = {}) }
}
