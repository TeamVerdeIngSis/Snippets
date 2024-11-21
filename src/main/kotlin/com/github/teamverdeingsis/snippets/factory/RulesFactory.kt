package com.github.teamverdeingsis.snippets.factory

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.teamverdeingsis.snippets.models.Rule

data class Config(
    @JsonProperty("identifier_format")
    val identifierFormat: String = "none", // Default to none
    @JsonProperty("mandatory-variable-or-literal-in-println")
    val mandatoryVariableOrLiteral: String = "none",
    @JsonProperty("read-input-with-simple-argument")
    val readInputWithSimpleArgument: String = "none",
)


class RulesFactory {
    fun getDefaultLintingRules(): List<Rule> {
        return listOf(
            Rule(
                id = "1",
                name = "snake-case-variables",
                isActive = true,
                value = null
            ),
            Rule(
                id = "2",
                name = "camel-case-variables",
                isActive = true,
                value = null
        ),
            Rule(
                id = "3",
                name = "mandatory-variable-or-literal-in-println",
                isActive = true,
                value = null
            ),
            Rule(
                id = "4",
                name = "read-input-with-simple-argument",
                isActive = true,
                value = null
            )
        )
    }

    fun getDefaultFormattingRules(): List<Rule> {
        return listOf(
            Rule(
                id = "1",
                name = "indentation",
                isActive = true,
                value = 3 // Representa la cantidad de espacios de indentación
            ),
            Rule(
                id = "2",
                name = "open-if-block-on-same-line",
                isActive = false
            ),
            Rule(
                id = "3",
                name = "max-line-length",
                isActive = true,
                value = 100
            ),
            Rule(
                id = "4",
                name = "no-trailing-spaces",
                isActive = false
            ),
            Rule(
                id = "5",
                name = "no-multiple-empty-lines",
                isActive = false
            )
        )
    }
}
