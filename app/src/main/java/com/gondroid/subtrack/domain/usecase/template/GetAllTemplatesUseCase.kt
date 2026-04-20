package com.gondroid.subtrack.domain.usecase.template

import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.domain.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTemplatesUseCase @Inject constructor(
    private val repository: TemplateRepository
) {
    operator fun invoke(): Flow<List<Template>> = repository.getAllTemplates()
}
