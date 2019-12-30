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
package eu.corvus.corax.app.kotlinx

import eu.corvus.corax.app.GleipnirApplication
import kotlinx.coroutines.*
import kotlinx.coroutines.internal.MainDispatcherFactory
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

/**
 * @author Vlad Ravenholm on 12/30/2019
 */
@InternalCoroutinesApi
sealed class GameDispatcher : MainCoroutineDispatcher(), Delay, KoinComponent {
    private val app: GleipnirApplication by inject()
    private val scheduledExecutorService = Executors.newScheduledThreadPool(4)

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        app.dispatch(block)
    }

    @ExperimentalCoroutinesApi
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val future = scheduledExecutorService.schedule({
            with(continuation) { resumeUndispatched(Unit)}
        }, timeMillis, TimeUnit.MILLISECONDS)
        continuation.invokeOnCancellation { runCatching { future.cancel(true) } }
    }

    override fun invokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle {
        val future = scheduledExecutorService.schedule({
            app.dispatch(block)
        }, timeMillis, TimeUnit.MILLISECONDS)

        return object : DisposableHandle {
            override fun dispose() {
                runCatching { future.cancel(true) }
            }
        }
    }

}

@InternalCoroutinesApi
internal class GameDispatcherFactory : MainDispatcherFactory {
    override val loadPriority: Int
        get() = 0

    override fun createDispatcher(allFactories: List<MainDispatcherFactory>): MainCoroutineDispatcher = GameDispatch
}

@InternalCoroutinesApi
private object ImmediateGameDispatcher : GameDispatcher() {
    override val immediate: MainCoroutineDispatcher
        get() = this

    override fun isDispatchNeeded(context: CoroutineContext): Boolean = true

    override fun toString() = "Game [immediate]"
}

@InternalCoroutinesApi
internal object GameDispatch: GameDispatcher() {
    override val immediate: MainCoroutineDispatcher
        get() = ImmediateGameDispatcher

    override fun toString(): String = "Game"
}