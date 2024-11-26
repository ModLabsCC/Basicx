package cc.modlabs.basicx.extensions

import cc.modlabs.basicx.BasicX
import org.slf4j.LoggerFactory

fun <T : Any> T.getLogger(): org.slf4j.Logger {
    return LoggerFactory.getLogger(BasicX::class.java)
}

fun <T : Any> T.nullIf(condition: (T) -> Boolean): T? {
    return if (condition(this)) null else this
}