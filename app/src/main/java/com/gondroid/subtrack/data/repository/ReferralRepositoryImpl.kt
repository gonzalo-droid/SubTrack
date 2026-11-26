package com.gondroid.subtrack.data.mock

import com.gondroid.subtrack.domain.model.ReferralInfo
import com.gondroid.subtrack.domain.model.ReferralRecord
import com.gondroid.subtrack.domain.repository.ReferralRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MockReferralRepository @Inject constructor() : ReferralRepository {

    override fun getReferralInfo(): Flow<ReferralInfo> = MockDataStore.referralInfo

    override fun getRecentReferrals(limit: Int): Flow<List<ReferralRecord>> =
        MockDataStore.referralRecords.map { it.take(limit) }
}
