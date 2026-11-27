package com.gondroid.subtrack.domain.usecase.member


class SendReminderUseCase() {
    // TODO: [fase-2] registrar recordatorio enviado en Firestore
    operator fun invoke(memberId: String, subscriptionId: String) = Unit
}
