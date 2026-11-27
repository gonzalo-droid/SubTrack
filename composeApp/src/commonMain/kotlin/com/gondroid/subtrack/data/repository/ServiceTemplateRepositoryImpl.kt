package com.gondroid.subtrack.data.repository

import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.repository.ServiceTemplateRepository

class ServiceTemplateRepositoryImpl : ServiceTemplateRepository {
    override fun getPopularServices(): List<ServiceTemplate> = ServiceTemplates.popular

    override fun getOtherService(): ServiceTemplate = ServiceTemplates.other
}
