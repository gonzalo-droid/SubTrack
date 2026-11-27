package com.gondroid.subtrack.domain.usecase.servicetemplate

import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.repository.ServiceTemplateRepository

class GetPopularServicesUseCase(
    private val repository: ServiceTemplateRepository
) {
    operator fun invoke(): List<ServiceTemplate> = repository.getPopularServices()

    /** Starting point for a custom service ("Other"). */
    fun other(): ServiceTemplate = repository.getOtherService()
}
