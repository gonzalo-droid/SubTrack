package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.ServiceTemplate

interface ServiceTemplateRepository {
    fun getPopularServices(): List<ServiceTemplate>

    /** The "Other" tile: a custom service the user names and colors. */
    fun getOtherService(): ServiceTemplate
}
