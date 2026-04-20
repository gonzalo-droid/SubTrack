package com.gondroid.subtrack.domain.usecase.member

import javax.inject.Inject

class SendReminderUseCase @Inject constructor() {
    // TODO: [fase-2] registrar recordatorio enviado en Firestore
    operator fun invoke(memberId: String, subscriptionId: String) = Unit
}
