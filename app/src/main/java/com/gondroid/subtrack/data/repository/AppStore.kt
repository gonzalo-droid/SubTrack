package com.gondroid.subtrack.data.repository

import com.gondroid.subtrack.data.demo.DemoData

import com.gondroid.subtrack.domain.model.ExitRequest
import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.model.ReferralInfo
import com.gondroid.subtrack.domain.model.ReferralRecord
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.model.UserAlias
import kotlinx.coroutines.flow.MutableStateFlow
import com.gondroid.subtrack.BuildConfig

object AppStore {
    // Demo data only in debug builds; a real account starts empty (message templates are app defaults)
    private val demo = BuildConfig.DEBUG

    val subscriptions = MutableStateFlow<List<Subscription>>(if (demo) DemoData.subscriptions else emptyList())
    val payments = MutableStateFlow<List<Payment>>(if (demo) DemoData.payments else emptyList())
    val templates = MutableStateFlow<List<Template>>(DemoData.templates)
    val currentUser = MutableStateFlow<User?>(if (demo) DemoData.currentUser else null)
    val exitRequests = MutableStateFlow<List<ExitRequest>>(emptyList())

    val userAlias = MutableStateFlow(if (demo) DemoData.userAlias else UserAlias())
    val referralInfo = MutableStateFlow<ReferralInfo>(DemoData.referralInfo)
    val referralRecords = MutableStateFlow<List<ReferralRecord>>(DemoData.referralRecords)

    /** Sign-out / account deletion: back to an empty install (the default templates stay). */
    fun clear() {
        subscriptions.value = emptyList()
        payments.value = emptyList()
        templates.value = DemoData.templates
        currentUser.value = null
        exitRequests.value = emptyList()
        userAlias.value = UserAlias()
    }
}
