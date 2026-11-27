package com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.input.STTextField
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.enums.PaymentStatus

@Composable
fun EditMemberDataSheet(
    member: Member,
    onConfirm: (Member) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(member.name) }
    var phone by remember { mutableStateOf(member.phone) }
    var amount by remember { mutableStateOf("%.2f".format(member.shareAmount)) }

    SheetContainer {
        Column(modifier = Modifier.padding(horizontal = Spacing.base)) {
            SheetHandle()
            Spacer(Modifier.height(Spacing.m))

            Text(
                text = stringResource(R.string.member_sheet_data_title, member.name),
                style = SubTrackType.headlineL,
                color = TextPrimary
            )
            Spacer(Modifier.height(Spacing.l))

            STTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.member_sheet_data_name_label),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.m))
            STTextField(
                value = phone,
                onValueChange = { phone = it },
                label = stringResource(R.string.member_sheet_data_phone_label),
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Phone
            )
            Spacer(Modifier.height(Spacing.m))
            STTextField(
                value = amount,
                onValueChange = { amount = it },
                label = stringResource(R.string.member_sheet_data_amount_label),
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Decimal
            )

            Spacer(Modifier.height(Spacing.l))
            PrimaryButton(
                text = stringResource(R.string.member_sheet_data_save),
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: member.shareAmount
                    onConfirm(member.copy(name = name, phone = phone, shareAmount = parsedAmount))
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && phone.isNotBlank()
            )
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF18181C)
@Composable
private fun EditMemberDataPreview() {
    SubTrackTheme {
        EditMemberDataSheet(
            member = Member(
                id = "mem_maria", userId = null, name = "María Rodríguez",
                phone = "+51 912 345 678", profileLabel = "Perfil 2",
                shareAmount = 10.98, currentStatus = PaymentStatus.OVERDUE, joinedAt = 0L
            ),
            onConfirm = {}, onDismiss = {}
        )
    }
}
