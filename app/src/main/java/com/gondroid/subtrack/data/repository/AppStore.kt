package com.gondroid.subtrack.data.mock

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

object MockDataStore {
    // Demo data only in debug builds; a real account starts empty (message templates are app defaults)
    private val demo = BuildConfig.DEBUG

    val subscriptions = MutableStateFlow<List<Subscription>>(if (demo) MockData.subscriptions else emptyList())
    val payments = MutableStateFlow<List<Payment>>(if (demo) MockData.payments else emptyList())
    val templates = MutableStateFlow<List<Template>>(MockData.templates)
    val currentUser = MutableStateFlow<User?>(if (demo) MockData.currentUser else null)
    val exitRequests = MutableStateFlow<List<ExitRequest>>(emptyList())

    // Toggle Pro state for debug
    val isProUser = MutableStateFlow(true)

    val userAlias = MutableStateFlow(if (demo) MockData.userAlias else UserAlias())
    val referralInfo = MutableStateFlow<ReferralInfo>(MockData.referralInfo)
    val referralRecords = MutableStateFlow<List<ReferralRecord>>(MockData.referralRecords)
}
