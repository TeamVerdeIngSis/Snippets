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
                value = null,
            ),
            Rule(
                id = "2",
                name = "camel-case-variables",
                isActive = true,
                value = null,
            ),
            Rule(
                id = "3",
                name = "mandatory-variable-or-literal-in-println",
                isActive = true,
                value = null,
            ),
            Rule(
                id = "4",
                name = "read-input-with-simple-argument",
                isActive = true,
                value = null,
            ),
        )
    }

    fun getDefaultFormattingRules(): List<Rule> {
        return listOf(
            Rule(
                id = "1",
                name = "space-before-colon",
                isActive = true,
            ),
            Rule(
                id = "2",
                name = "space-after-colon",
                isActive = false,
            ),
            Rule(
                id = "3",
                name = "space-around-equals",
                isActive = true,
            ),
            Rule(
                id = "4",
                name = "newline-before-println",
                isActive = false,
                value = 0,
            ),
            Rule(
                id = "5",
                name = "indentation",
                isActive = false,
                value = 4,
            ),
        )
    }
}
