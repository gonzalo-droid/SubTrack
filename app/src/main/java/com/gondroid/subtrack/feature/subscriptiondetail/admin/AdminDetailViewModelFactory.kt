package com.gondroid.subtrack.feature.subscriptiondetail.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gondroid.subtrack.data.mock.MockMemberRepository
import com.gondroid.subtrack.data.mock.MockPaymentRepository
import com.gondroid.subtrack.data.mock.MockSubscriptionRepository
import com.gondroid.subtrack.domain.usecase.member.AddMemberUseCase
import com.gondroid.subtrack.domain.usecase.member.ArchiveMemberUseCase
import com.gondroid.subtrack.domain.usecase.member.GetArchivedMembersUseCase
import com.gondroid.subtrack.domain.usecase.member.GetMembersForSubscriptionUseCase
import com.gondroid.subtrack.domain.usecase.member.MarkMemberAsPaidUseCase
import com.gondroid.subtrack.domain.usecase.member.RemoveMemberUseCase
import com.gondroid.subtrack.domain.usecase.member.UpdateMemberUseCase
import com.gondroid.subtrack.domain.usecase.payment.GetPaymentHistoryUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionDetailUseCase

// TODO: [hilt] Remove when @HiltViewModel injection is active (AGP / Hilt plugin fixed)
class AdminDetailViewModelFactory(
    private val subscriptionId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val subRepo = MockSubscriptionRepository()
        val memberRepo = MockMemberRepository()
        val paymentRepo = MockPaymentRepository()
        return AdminDetailViewModel(
            subscriptionId = subscriptionId,
            getSubscriptionDetail = GetSubscriptionDetailUseCase(subRepo),
            getMembersForSubscription = GetMembersForSubscriptionUseCase(memberRepo),
            getArchivedMembers = GetArchivedMembersUseCase(memberRepo),
            getPaymentHistory = GetPaymentHistoryUseCase(paymentRepo, memberRepo),
            addMemberUseCase = AddMemberUseCase(memberRepo),
            updateMemberUseCase = UpdateMemberUseCase(memberRepo),
            archiveMemberUseCase = ArchiveMemberUseCase(memberRepo),
            removeMemberUseCase = RemoveMemberUseCase(memberRepo),
            markMemberAsPaidUseCase = MarkMemberAsPaidUseCase(paymentRepo)
        ) as T
    }
}
