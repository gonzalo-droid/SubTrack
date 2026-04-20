package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.ReferralInfo
import com.gondroid.subtrack.domain.model.ReferralRecord
import kotlinx.coroutines.flow.Flow

interface ReferralRepository {
    fun getReferralInfo(): Flow<ReferralInfo>
    fun getRecentReferrals(limit: Int = 4): Flow<List<ReferralRecord>>
}
