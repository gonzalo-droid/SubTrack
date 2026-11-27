package com.gondroid.subtrack.core.designsystem.components.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.AccentRedBg
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.domain.model.enums.PaymentStatus

private data class StatusColors(val container: Color, val content: Color)

private fun PaymentStatus.colors() = when (this) {
    PaymentStatus.PAID -> StatusColors(AccentGreenBg, AccentGreen)
    PaymentStatus.PENDING -> StatusColors(AccentAmberBg, AccentAmber)
    PaymentStatus.OVERDUE -> StatusColors(AccentRedBg, AccentRed)
    PaymentStatus.LATE -> StatusColors(AccentAmberBg, AccentAmber)
}

private val PaymentStatus.labelRes get() = when (this) {
    PaymentStatus.PAID -> R.string.status_paid
    PaymentStatus.PENDING -> R.string.status_pending
    PaymentStatus.OVERDUE -> R.string.status_overdue
    PaymentStatus.LATE -> R.string.status_late
}

@Composable
fun StatusPill(
    status: PaymentStatus,
    modifier: Modifier = Modifier
) {
    val colors = status.colors()
    Text(
        text = stringResource(status.labelRes).uppercase(),
        style = SubTrackType.monoXS,
        color = colors.content,
        modifier = modifier
            .clip(SubTrackShapes.circle)
            .background(colors.container)
            .padding(horizontal = Spacing.s, vertical = Spacing.xs)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun StatusPillPreview() {
    SubTrackTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Spacing.base)
        ) {
            StatusPill(status = PaymentStatus.PAID)
            Spacer(Modifier.width(Spacing.s))
            StatusPill(status = PaymentStatus.PENDING)
            Spacer(Modifier.width(Spacing.s))
            StatusPill(status = PaymentStatus.OVERDUE)
            Spacer(Modifier.width(Spacing.s))
            StatusPill(status = PaymentStatus.LATE)
        }
    }
}
