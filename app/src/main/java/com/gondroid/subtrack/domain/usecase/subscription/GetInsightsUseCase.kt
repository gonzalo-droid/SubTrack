package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.Insight
import com.gondroid.subtrack.domain.model.InsightType
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val UNUSED_THRESHOLD_DAYS = 30

class GetInsightsUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<Insight?> =
        repository.getAllSubscriptions().map { subscriptions ->
            val now = System.currentTimeMillis()
            val thresholdMs = UNUSED_THRESHOLD_DAYS.toLong() * 24 * 60 * 60 * 1000
            subscriptions.firstOrNull { sub ->
                sub.lastActivityAt != null && (now - sub.lastActivityAt) > thresholdMs
            }?.let { sub ->
                val daysSince = ((now - sub.lastActivityAt!!) / (24 * 60 * 60 * 1000)).toInt()
                Insight(
                    type = InsightType.UNUSED_SUBSCRIPTION,
                    subscriptionId = sub.id,
                    subscriptionName = sub.name,
                    amount = sub.totalAmount,
                    daysSinceActivity = daysSince
                )
            }
        }
}
