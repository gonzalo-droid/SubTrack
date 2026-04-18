package com.gondroid.subtrack.core.designsystem.components.text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary

enum class AmountSize { SMALL, MEDIUM, LARGE }

private data class AmountStyles(
    val amountStyle: TextStyle,
    val symbolStyle: TextStyle,
    val symbolTopPadding: Dp
)

private fun AmountSize.styles() = when (this) {
    AmountSize.LARGE -> AmountStyles(
        amountStyle = SubTrackType.displayL.copy(fontFeatureSettings = "tnum"),
        symbolStyle = SubTrackType.headlineS,
        symbolTopPadding = 6.dp
    )
    AmountSize.MEDIUM -> AmountStyles(
        amountStyle = SubTrackType.displayM.copy(fontFeatureSettings = "tnum"),
        symbolStyle = SubTrackType.headlineS,
        symbolTopPadding = 4.dp
    )
    AmountSize.SMALL -> AmountStyles(
        amountStyle = SubTrackType.headlineM.copy(fontFeatureSettings = "tnum"),
        symbolStyle = SubTrackType.monoS,
        symbolTopPadding = 3.dp
    )
}

@Composable
fun AmountDisplay(
    amount: Double,
    modifier: Modifier = Modifier,
    currencySymbol: String = "S/",
    size: AmountSize = AmountSize.MEDIUM,
    color: Color = TextPrimary
) {
    val styles = size.styles()
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
    ) {
        Text(
            text = currencySymbol,
            style = styles.symbolStyle,
            color = color.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = styles.symbolTopPadding, end = 2.dp)
        )
        Text(
            text = "%.2f".format(amount),
            style = styles.amountStyle,
            color = color
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun AmountDisplayPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base)) {
            AmountDisplay(amount = 1234.90, size = AmountSize.LARGE)
            Spacer(Modifier.width(Spacing.s))
            AmountDisplay(amount = 49.90, size = AmountSize.MEDIUM)
            Spacer(Modifier.width(Spacing.s))
            AmountDisplay(amount = 12.50, size = AmountSize.SMALL)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun AmountDisplayUsdPreview() {
    SubTrackTheme {
        AmountDisplay(
            amount = 9.99,
            currencySymbol = "$",
            size = AmountSize.MEDIUM
        )
    }
}
