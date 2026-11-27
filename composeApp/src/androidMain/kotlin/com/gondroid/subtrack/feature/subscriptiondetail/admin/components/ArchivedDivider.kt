package com.gondroid.subtrack.feature.subscriptiondetail.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType

@Composable
fun ArchivedDivider(
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BorderDefault,
            thickness = 0.5.dp
        )
        Text(
            text = stringResource(R.string.subscription_detail_archived_divider, count),
            style = SubTrackType.monoXS,
            color = AccentAmber,
            modifier = Modifier
                .clip(SubTrackShapes.circle)
                .background(AccentAmberBg)
                .padding(horizontal = Spacing.m, vertical = Spacing.xs)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BorderDefault,
            thickness = 0.5.dp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ArchivedDividerPreview() {
    SubTrackTheme {
        ArchivedDivider(
            count = 1,
            modifier = Modifier.padding(Spacing.base)
        )
    }
}
