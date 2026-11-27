package com.gondroid.subtrack.domain.usecase.template

import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.domain.repository.TemplateRepository

class GetTemplateByIdUseCase(
    private val repository: TemplateRepository
) {
    suspend operator fun invoke(id: String): Template? = repository.getTemplateById(id)
}
