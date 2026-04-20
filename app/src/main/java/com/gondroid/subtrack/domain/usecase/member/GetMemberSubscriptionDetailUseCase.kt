package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.AdminPublicInfo
import com.gondroid.subtrack.domain.model.MemberSubscriptionView
import com.gondroid.subtrack.domain.repository.MemberRepository
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import com.gondroid.subtrack.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMemberSubscriptionDetailUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val memberRepository: MemberRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(subscriptionId: String, memberId: String): Flow<MemberSubscriptionView?> {
        val subFlow = subscriptionRepository.observeSubscription(subscriptionId)
        val exitFlow = memberRepository.getExitRequest(memberId, subscriptionId)
        return combine(subFlow, exitFlow) { sub, exitRequest ->
            if (sub == null) return@combine null
            val member = (sub.members + sub.archivedMembers).find { it.id == memberId }
                ?: return@combine null
            val adminUser = userRepository.getUserById(sub.ownerId)
            MemberSubscriptionView(
                subscriptionId = sub.id,
                serviceName = sub.name,
                brandColor = sub.brandColor,
                cycle = sub.cycle,
                cutoffDay = sub.cutoffDay,
                currency = sub.currency,
                myMemberId = member.id,
                myShareAmount = member.shareAmount,
                myCurrentStatus = member.currentStatus,
                myJoinedAt = member.joinedAt,
                adminInfo = AdminPublicInfo(
                    name = adminUser?.name ?: "Admin",
                    phone = adminUser?.phone ?: ""
                ),
                pendingExitRequest = exitRequest
            )
        }
    }
}
