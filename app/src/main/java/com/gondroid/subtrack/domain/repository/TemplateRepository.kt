package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.Template
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    fun getAllTemplates(): Flow<List<Template>>
    suspend fun getTemplateById(id: String): Template?
    suspend fun createTemplate(template: Template): Result<String>
    suspend fun updateTemplate(template: Template): Result<Unit>
    suspend fun deleteTemplate(id: String): Result<Unit>
    suspend fun duplicateTemplate(id: String): Result<String>
}
