package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.NewMemberData
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.repository.MemberRepository
import javax.inject.Inject

class AddMemberUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(subscriptionId: String, data: NewMemberData): Result<Member> {
        val member = Member(
            id = "mem_${System.currentTimeMillis()}",
            userId = null,
            name = data.name,
            phone = data.phone,
            profileLabel = data.profileLabel,
            shareAmount = data.shareAmount,
            currentStatus = PaymentStatus.PENDING,
            joinedAt = System.currentTimeMillis()
        )
        return repository.addMember(subscriptionId, member).map { member }
    }
}
