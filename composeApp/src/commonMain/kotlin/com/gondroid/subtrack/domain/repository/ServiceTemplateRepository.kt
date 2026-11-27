package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.ServiceTemplate

interface ServiceTemplateRepository {
    fun getPopularServices(): List<ServiceTemplate>
}
