package com.gondroid.subtrack.data.mock

import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.domain.repository.TemplateRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockTemplateRepository @Inject constructor() : TemplateRepository {

    private val store = MockDataStore.templates

    override fun getAllTemplates(): Flow<List<Template>> = store

    override suspend fun getTemplateById(id: String): Template? {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        return store.value.find { it.id == id }
    }

    override suspend fun createTemplate(template: Template): Result<String> {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        store.value = store.value + template
        return Result.success(template.id)
    }

    override suspend fun updateTemplate(template: Template): Result<Unit> {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        store.value = store.value.map { if (it.id == template.id) template else it }
        return Result.success(Unit)
    }

    override suspend fun deleteTemplate(id: String): Result<Unit> {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        val template = store.value.find { it.id == id }
            ?: return Result.failure(Exception("Template not found"))
        if (template.isDefault) {
            return Result.failure(Exception("Cannot delete default templates"))
        }
        store.value = store.value.filter { it.id != id }
        return Result.success(Unit)
    }

    override suspend fun duplicateTemplate(id: String): Result<String> {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
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
