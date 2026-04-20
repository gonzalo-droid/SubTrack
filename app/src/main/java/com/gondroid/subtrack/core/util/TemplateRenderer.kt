package com.gondroid.subtrack.core.util

import com.gondroid.subtrack.domain.model.TemplateVariable

private val TOKEN_REGEX = Regex("""\{[a-z_]+\}""")

object TemplateRenderer {

    fun renderWithExamples(messageBody: String): String {
        var result = messageBody
        TemplateVariable.entries.forEach { variable ->
            result = result.replace(variable.token, variable.exampleValue)
        }
        return result
    }

    fun renderWithValues(messageBody: String, values: Map<TemplateVariable, String>): String {
        var result = messageBody
        values.forEach { (variable, value) ->
            result = result.replace(variable.token, value)
        }
        return result
    }

    fun extractVariables(messageBody: String): List<TemplateVariable> {
        val tokens = TOKEN_REGEX.findAll(messageBody).map { it.value }.toSet()
        return TemplateVariable.entries.filter { it.token in tokens }
    }

    fun isValidVariableToken(token: String): Boolean = TOKEN_REGEX.matches(token)
}
