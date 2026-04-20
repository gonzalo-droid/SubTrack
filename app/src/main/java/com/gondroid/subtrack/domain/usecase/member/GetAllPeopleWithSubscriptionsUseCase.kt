package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.PersonSummary
import com.gondroid.subtrack.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPeopleWithSubscriptionsUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    operator fun invoke(): Flow<List<PersonSummary>> = repository.getAllPeopleWithSubscriptions()
}
