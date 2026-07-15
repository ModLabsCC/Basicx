package cc.modlabs.basicx.util

private val variablePattern = Regex("\\$\\{\\{([a-zA-Z0-9_.-]+)}}")

fun resolveMessageVariables(values: Map<String, String>): Map<String, String> =
    values.mapValues { (_, initialValue) ->
        var value = initialValue
        repeat(values.size.coerceAtMost(20)) {
            val resolved = variablePattern.replace(value) { match ->
                values[match.groupValues[1]] ?: match.value
            }
            if (resolved == value) return@mapValues value
            value = resolved
        }
        value
    }
