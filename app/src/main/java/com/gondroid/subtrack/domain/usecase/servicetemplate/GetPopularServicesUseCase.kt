package com.gondroid.subtrack.domain.usecase.servicetemplate

import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.repository.ServiceTemplateRepository
import javax.inject.Inject

class GetPopularServicesUseCase @Inject constructor(
    private val repository: ServiceTemplateRepository
) {
    operator fun invoke(): List<ServiceTemplate> = repository.getPopularServices()
}
