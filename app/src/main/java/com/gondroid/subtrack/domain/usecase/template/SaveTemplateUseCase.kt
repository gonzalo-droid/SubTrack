package com.gondroid.subtrack.domain.usecase.template

import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.domain.repository.TemplateRepository
import javax.inject.Inject

class SaveTemplateUseCase @Inject constructor(
    private val repository: TemplateRepository
) {
    suspend operator fun invoke(template: Template): Result<String> {
        return if (template.isUserCreated && repository.getTemplateById(template.id) == null) {
            repository.createTemplate(template)
        } else {
            repository.updateTemplate(template).map { template.id }
        }
    }
}
