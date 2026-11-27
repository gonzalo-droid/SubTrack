package com.gondroid.subtrack.data.repository

import com.gondroid.subtrack.domain.model.ReferralInfo
import com.gondroid.subtrack.domain.model.ReferralRecord
import com.gondroid.subtrack.domain.repository.ReferralRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReferralRepositoryImpl : ReferralRepository {

    override fun getReferralInfo(): Flow<ReferralInfo> = AppStore.referralInfo

    override fun getRecentReferrals(limit: Int): Flow<List<ReferralRecord>> =
        AppStore.referralRecords.map { it.take(limit) }
}
