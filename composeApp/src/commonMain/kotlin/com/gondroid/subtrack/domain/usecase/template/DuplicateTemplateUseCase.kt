package com.gondroid.subtrack.domain.usecase.template

import com.gondroid.subtrack.domain.repository.TemplateRepository

class DuplicateTemplateUseCase(
    private val repository: TemplateRepository
) {
    suspend operator fun invoke(id: String): Result<String> = repository.duplicateTemplate(id)
}
