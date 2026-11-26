package com.gondroid.subtrack.data.mock

import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.repository.ServiceTemplateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockServiceTemplateRepository @Inject constructor() : ServiceTemplateRepository {
    override fun getPopularServices(): List<ServiceTemplate> = ServiceTemplates.popular
}
