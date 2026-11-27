package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.SplitType
import com.gondroid.subtrack.domain.repository.SubscriptionRepository

/** Edits amount, currency, cycle and cutoff day; member shares follow the new total. */
class UpdateSubscriptionDetailsUseCase(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(
        id: String,
        totalAmount: Double,
        currency: String,
        cycle: BillingCycle,
        cutoffDay: Int
    ): Result<Unit> {
        val current = repository.getSubscriptionById(id)
            ?: return Result.failure(NoSuchElementException("Subscription $id not found"))
        val updated = current.copy(
            totalAmount = totalAmount,
            currency = currency,
            cycle = cycle,
            cutoffDay = cutoffDay,
            members = rescaleShares(current, totalAmount),
            updatedAt = System.currentTimeMillis()
        )
        return repository.updateSubscription(updated)
    }

    private fun rescaleShares(current: Subscription, newTotal: Double) = current.members.map { member ->
        val share = when (current.splitType) {
            SplitType.EQUAL -> newTotal / (current.members.size + 1) // +1: the admin pays a share too
            SplitType.PERCENTAGE ->
                if (current.totalAmount > 0) member.shareAmount * newTotal / current.totalAmount
                else member.shareAmount
            SplitType.FIXED -> member.shareAmount
        }
        member.copy(shareAmount = share)
    }
}
