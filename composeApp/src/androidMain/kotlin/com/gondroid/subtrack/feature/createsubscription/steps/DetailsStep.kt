package com.gondroid.subtrack.feature.createsubscription.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.ServiceLogo
import com.gondroid.subtrack.core.designsystem.components.input.SegmentedSelector
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.BorderStrong
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.core.util.toComposeColor
import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.semantics.Role
import com.gondroid.subtrack.domain.util.nextCutoff

private val CURRENCIES = listOf("PEN", "USD")
private val CYCLES = listOf(BillingCycle.MONTHLY, BillingCycle.YEARLY)

@Composable
fun DetailsStep(
    isEditing: Boolean,
    service: ServiceTemplate?,
    totalAmount: String,
    currency: String,
    cycle: BillingCycle,
    cutoffDay: Int,
    onAmountChange: (String) -> Unit,
    onApplySuggested: () -> Unit,
    onCurrencyChange: (String) -> Unit,
    onCycleChange: (BillingCycle) -> Unit,
    onCutoffDayChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.base)
    ) {
        Spacer(Modifier.height(Spacing.m))
        Eyebrow(
            text = stringResource(
                if (isEditing) R.string.edit_subscription_eyebrow else R.string.create_subscription_step2_eyebrow
            )
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.create_subscription_step2_title),
            style = SubTrackType.displayS,
            color = TextPrimary
        )
        if (service != null) {
            Spacer(Modifier.height(Spacing.xs))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                ServiceLogo(
                    serviceName = service.name,
                    brandColor = service.brandColor.toComposeColor(),
                    size = 16.dp,
                    serviceId = service.id.takeIf { it != "tpl_other" }
                )
                Text(service.name, style = SubTrackType.monoXS, color = TextTertiary)
            }
        }
        Spacer(Modifier.height(Spacing.l))

        // Amount card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.l)
                .background(BgSurface)
                .border(1.dp, BorderDefault, SubTrackShapes.l)
                .padding(Spacing.m)
        ) {
            Eyebrow(text = stringResource(R.string.create_subscription_amount_label))
            Spacer(Modifier.height(Spacing.s))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                Text("S/", style = SubTrackType.displayM, color = TextTertiary)
                BasicTextField(
                    value = totalAmount,
                    onValueChange = { v ->
                        if (v.count { it == '.' } <= 1 && v.all { it.isDigit() || it == '.' }) {
                            onAmountChange(v)
                        }
                    },
                    textStyle = SubTrackType.displayM.copy(color = TextPrimary),
                    cursorBrush = SolidColor(AccentGreen),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    decorationBox = { inner ->
                        Box {
                            if (totalAmount.isEmpty()) {
                                Text("0.00", style = SubTrackType.displayM.copy(color = TextTertiary))
                            }
                            inner()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            // Suggested amount chip
            val suggested = service?.suggestedMonthly
            if (suggested != null) {
                Spacer(Modifier.height(Spacing.s))
                Row(
                    modifier = Modifier
                        .clip(SubTrackShapes.circle)
                        .background(AccentAmberBg)
                        .border(1.dp, AccentAmber.copy(alpha = 0.3f), SubTrackShapes.circle)
                        .clickable(onClick = onApplySuggested)
                        .padding(horizontal = Spacing.m, vertical = Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        stringResource(R.string.create_subscription_suggested_chip, "%.2f".format(suggested)),
                        style = SubTrackType.monoXS,
                        color = AccentAmber
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.m))

        // Currency selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.l)
                .background(BgSurface)
                .border(1.dp, BorderDefault, SubTrackShapes.l)
                .padding(Spacing.m)
        ) {
            Eyebrow(text = stringResource(R.string.create_subscription_currency_label))
            Spacer(Modifier.height(Spacing.s))
            SegmentedSelector(
                options = CURRENCIES,
                selectedIndex = CURRENCIES.indexOf(currency).coerceAtLeast(0),
                onSelectionChange = { onCurrencyChange(CURRENCIES[it]) },
                labelProvider = { it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(Spacing.m))

        // Cycle selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.l)
                .background(BgSurface)
                .border(1.dp, BorderDefault, SubTrackShapes.l)
                .padding(Spacing.m)
        ) {
            Eyebrow(text = stringResource(R.string.create_subscription_cycle_label))
            Spacer(Modifier.height(Spacing.s))
            val cycleLabels = mapOf(
                BillingCycle.MONTHLY to stringResource(R.string.create_subscription_cycle_monthly),
                BillingCycle.YEARLY  to stringResource(R.string.create_subscription_cycle_yearly),
                BillingCycle.CUSTOM  to stringResource(R.string.create_subscription_cycle_custom)
            )
            SegmentedSelector(
                options = CYCLES,
                selectedIndex = CYCLES.indexOf(cycle).coerceAtLeast(0),
                onSelectionChange = { onCycleChange(CYCLES[it]) },
                labelProvider = { c -> cycleLabels[c] ?: c.name },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(Spacing.m))

        // Cutoff day card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.l)
                .background(BgSurface)
                .border(1.dp, BorderDefault, SubTrackShapes.l)
                .padding(Spacing.m)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Eyebrow(text = stringResource(R.string.create_subscription_cutoff_label))
                Text(
                    stringResource(R.string.create_subscription_cutoff_selected, cutoffDay),
                    style = SubTrackType.monoXS,
                    color = AccentGreen
                )
            }
            Spacer(Modifier.height(Spacing.s))
            DayGrid(selectedDay = cutoffDay, onSelect = onCutoffDayChange)
            Spacer(Modifier.height(Spacing.s))
            val next = nextCutoff(cutoffDay)
            Text(
                stringResource(
                    R.string.create_subscription_cutoff_next,
                    stringResource(
                        R.string.date_day_of_month,
                        next.day,
                        stringArrayResource(R.array.month_names)[next.month.ordinal]
                    )
                ),
                style = SubTrackType.bodyXS,
                color = TextSecondary
            )
        }

        Spacer(Modifier.height(Spacing.xl))
    }
}

/** Month-calendar style day picker: 7 columns, 48dp tall cells. */
@Composable
private fun DayGrid(selectedDay: Int, onSelect: (Int) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        modifier = Modifier.selectableGroup()
    ) {
        (1..31).chunked(7).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                week.forEach { day ->
                    val isSelected = day == selectedDay
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(SubTrackShapes.s)
                            .background(if (isSelected) AccentGreen.copy(alpha = 0.15f) else BgSurfaceEl)
                            .border(1.dp, if (isSelected) AccentGreen.copy(alpha = 0.5f) else BorderDefault, SubTrackShapes.s)
                            .selectable(selected = isSelected, role = Role.RadioButton) { onSelect(day) }
                    ) {
                        Text(
                            "$day",
                            style = SubTrackType.monoS,
                            color = if (isSelected) AccentGreen else TextSecondary
                        )
                    }
                }
                // Keep the last row's cells the same width as the others
                repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun DetailsStepPreview() {
    SubTrackTheme {
        DetailsStep(
            isEditing = false,
            service = ServiceTemplate("tpl_netflix", "Netflix", "#E50914", "N", 54.90, SubscriptionCategory.STREAMING),
            totalAmount = "54.90",
            currency = "PEN",
            cycle = BillingCycle.MONTHLY,
            cutoffDay = 15,
            onAmountChange = {},
            onApplySuggested = {},
            onCurrencyChange = {},
            onCycleChange = {},
            onCutoffDayChange = {}
        )
    }
}
