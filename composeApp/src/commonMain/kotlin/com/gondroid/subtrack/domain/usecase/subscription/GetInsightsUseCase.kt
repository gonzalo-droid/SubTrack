package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.Insight
import com.gondroid.subtrack.domain.model.InsightType
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.gondroid.subtrack.domain.util.currentTimeMillis

private const val UNUSED_THRESHOLD_DAYS = 30

class GetInsightsUseCase(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<Insight?> =
        repository.getAllSubscriptions().map { subscriptions ->
            val now = currentTimeMillis()
            val thresholdMs = UNUSED_THRESHOLD_DAYS.toLong() * 24 * 60 * 60 * 1000
            subscriptions.firstNotNullOfOrNull { sub ->
                sub.lastActivityAt?.takeIf { now - it > thresholdMs }?.let { sub to it }
            }?.let { (sub, lastActivityAt) ->
                val daysSince = ((now - lastActivityAt) / (24 * 60 * 60 * 1000)).toInt()
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
