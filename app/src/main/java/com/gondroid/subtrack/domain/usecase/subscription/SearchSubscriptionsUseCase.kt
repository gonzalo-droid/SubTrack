package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.Subscription
import javax.inject.Inject

enum class SubscriptionFilter { ALL, PERSONAL, SHARED }

class SearchSubscriptionsUseCase @Inject constructor() {
    operator fun invoke(
        subscriptions: List<Subscription>,
        query: String,
        filter: SubscriptionFilter
    ): List<Subscription> {
        val filtered = when (filter) {
            SubscriptionFilter.ALL -> subscriptions
            SubscriptionFilter.PERSONAL -> subscriptions.filter { !it.isShared }
            SubscriptionFilter.SHARED -> subscriptions.filter { it.isShared }
        }
        return if (query.isBlank()) filtered
        else filtered.filter { it.name.contains(query.trim(), ignoreCase = true) }
    }
}
