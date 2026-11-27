package com.gondroid.subtrack.domain.usecase.template

import com.gondroid.subtrack.domain.repository.TemplateRepository

class DeleteTemplateUseCase(
    private val repository: TemplateRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteTemplate(id)
}
