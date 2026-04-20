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

object MockDataStore {
    val subscriptions = MutableStateFlow<List<Subscription>>(MockData.subscriptions)
    val payments = MutableStateFlow<List<Payment>>(MockData.payments)
    val templates = MutableStateFlow<List<Template>>(MockData.templates)
    val currentUser = MutableStateFlow<User?>(MockData.currentUser)
    val exitRequests = MutableStateFlow<List<ExitRequest>>(emptyList())

    // Toggle Pro state for debug
    val isProUser = MutableStateFlow(true)

    val userAlias = MutableStateFlow(MockData.userAlias)
    val referralInfo = MutableStateFlow<ReferralInfo>(MockData.referralInfo)
    val referralRecords = MutableStateFlow<List<ReferralRecord>>(MockData.referralRecords)
}
