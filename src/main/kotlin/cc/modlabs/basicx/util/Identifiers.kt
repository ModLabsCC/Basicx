package cc.modlabs.basicx.util

private val identifierPattern = Regex("^[A-Za-z0-9_-]{1,32}$")

fun isSafeIdentifier(value: String): Boolean = identifierPattern.matches(value)
