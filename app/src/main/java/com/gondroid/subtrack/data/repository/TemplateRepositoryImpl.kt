package com.gondroid.subtrack.data.repository

import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.domain.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow

class TemplateRepositoryImpl : TemplateRepository {

    private val store = AppStore.templates

    override fun getAllTemplates(): Flow<List<Template>> = store

    override suspend fun getTemplateById(id: String): Template? {
        return store.value.find { it.id == id }
    }

    override suspend fun createTemplate(template: Template): Result<String> {
        store.value = store.value + template
        return Result.success(template.id)
    }

    override suspend fun updateTemplate(template: Template): Result<Unit> {
        store.value = store.value.map { if (it.id == template.id) template else it }
        return Result.success(Unit)
    }

    override suspend fun deleteTemplate(id: String): Result<Unit> {
        val template = store.value.find { it.id == id }
            ?: return Result.failure(Exception("Template not found"))
        if (template.isDefault) {
            return Result.failure(Exception("Cannot delete default templates"))
        }
        store.value = store.value.filter { it.id != id }
        return Result.success(Unit)
    }

    override suspend fun duplicateTemplate(id: String): Result<String> {
        val original = store.value.find { it.id == id }
            ?: return Result.failure(Exception("Template not found"))
        val copy = original.copy(
            id = "tpl_copy_${System.currentTimeMillis()}",
            name = "Copia de ${original.name}",
            isDefault = false,
            isUserCreated = true
        )
        store.value = store.value + copy
        return Result.success(copy.id)
    }
}
