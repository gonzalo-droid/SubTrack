package com.gondroid.subtrack.domain.usecase.template

import com.gondroid.subtrack.domain.repository.TemplateRepository
import javax.inject.Inject

class DeleteTemplateUseCase @Inject constructor(
    private val repository: TemplateRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteTemplate(id)
}
