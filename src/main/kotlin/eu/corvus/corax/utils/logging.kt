/**
 * Copyright (c) 2013-2019 Corvus Corax Entertainment
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - Neither the name of Corvus Corax Entertainment nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.corvus.corax.utils

/**
 * @author Vlad Ravenholm on 12/1/2019
 */
object Logger: ILogger {
    private lateinit var usingLogger: ILogger

    var level = LogLevel.Debug

    fun init(iLogger: ILogger, minLevel: LogLevel = LogLevel.Info) {
        usingLogger = iLogger

        level = minLevel
    }

    override fun debug(message: String, vararg objs: Any) {
        if (level <= LogLevel.Debug) {
            usingLogger.info(message, *objs)
        }
    }

    override fun info(message: String, vararg objs: Any) {
        if (level <= LogLevel.Info) {
            usingLogger.info(message, *objs)
        }
    }

    override fun warn(message: String, vararg objs: Any) {
        if (level <= LogLevel.Warn) {
            usingLogger.warn(null, message, *objs)
        }
    }

    override fun warn(throwable: Throwable?, message: String?, vararg objs: Any) {
        if (level <= LogLevel.Warn) {
            usingLogger.warn(throwable, message, *objs)
        }
    }

    override fun error(message: String, vararg objs: Any) {
        if (level <= LogLevel.Error) {
            usingLogger.error(null, message, *objs)
        }
    }

    override fun error(throwable: Throwable?, message: String?, vararg objs: Any) {
        if (level <= LogLevel.Error) {
            usingLogger.error(throwable, message, *objs)
        }
    }

    override fun log(level: LogLevel, message: String?, throwable: Throwable?, objs: Array<out Any>) =
        usingLogger.log(level, message, throwable, objs)
}

enum class LogLevel { Debug, Info, Warn, Error, Silent }

/**
 * It's not meant to be pretty =]
 */
class SystemConsoleLogger: ILogger {
    private val classNameRegex = "[^\$]*".toRegex()

    override fun debug(message: String, vararg objs: Any) =
        log(LogLevel.Debug, message, null, objs)

    override fun info(message: String, vararg objs: Any) =
        log(LogLevel.Info, message, null, objs)

    override fun warn(throwable: Throwable?, message: String?, vararg objs: Any) =
        log(LogLevel.Warn, message, throwable, objs)

    override fun error(throwable: Throwable?, message: String?, vararg objs: Any) =
        log(LogLevel.Error, message, throwable, objs)

    override fun log(
        level: LogLevel,
        message: String?,
        throwable: Throwable?,
        vararg objs: Any
    ) {
        val stackTrace = Thread.currentThread().stackTrace
        val element = stackTrace.getOrElse(5) { stackTrace.last() } // ouch-er

        val mess = if (message.isNullOrBlank()) throwable?.message ?: "no message" else message
        val stream = if (level > LogLevel.Info) System.err else System.out

        stream.printf("[${level.name.toUpperCase()}] ${formatClassName(element)}: $mess\n", *objs)
        throwable?.printStackTrace(stream)
        println()
    }

    private fun formatClassName(element: StackTraceElement) = classNameRegex.find(element.className)?.value
}

interface ILogger {
    fun debug(message: String, vararg objs: Any)
    fun info(message: String, vararg objs: Any)

    fun warn(message: String, vararg objs: Any) {}
    fun warn(throwable: Throwable?, message: String? = null, vararg objs: Any)

    fun error(message: String, vararg objs: Any) {}
    fun error(throwable: Throwable?, message: String? = null, vararg objs: Any)
    fun log(level: LogLevel, message: String?, throwable: Throwable?, vararg objs: Any)
}
