package com.gondroid.subtrack.data.repository

import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.repository.ServiceTemplateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceTemplateRepositoryImpl @Inject constructor() : ServiceTemplateRepository {
    override fun getPopularServices(): List<ServiceTemplate> = ServiceTemplates.popular
}
