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
package eu.corvus.corax.app.input

import eu.corvus.corax.app.Device
import eu.corvus.corax.app.Input
import eu.corvus.corax.app.InputAction
import eu.corvus.corax.app.InputEvent
import eu.corvus.corax.utils.Logger
import org.joml.Vector2f


/**
 * @author Vlad Ravenholm on 11/30/2019
 */
class AppInput: Input {
    private val actions: MutableMap<String, InputAction> = mutableMapOf()
    private val keyMap: MutableMap<Int, String> = mutableMapOf()
    private val mouseMap: MutableMap<Int, String> = mutableMapOf()

    val lastMousePosition = Vector2f()
    val mousePositionDelta = Vector2f()

    override fun keyPress(key: Int, event: InputEvent) {
        if (event == InputEvent.Repeat) return // not finding an use right now
        val mapping = keyMap[key] ?: return
        actions[mapping]?.invoke(mapping, event) ?: let { Logger.error("How did we get here? null mapping! $mapping") }
    }

    override fun mousePress(button: Int, event: InputEvent) {
        if (event == InputEvent.Repeat) return // not finding an use right now
        val mapping = mouseMap[button] ?: return
        actions[mapping]?.invoke(mapping, event) ?: let { Logger.error("How did we get here? null mapping! $mapping") }
    }

    override fun mouseMotion(width: Int, height: Int, xpos: Float, ypos: Float) {
        val newPos = Vector2f(xpos, ypos)

        mousePositionDelta.set(lastMousePosition.sub(newPos)).normalize()

        lastMousePosition.set(xpos, ypos)

        //println("xpos = [${mousePositionDelta.x}], ypos = [${mousePositionDelta.y}]")
    }

    override fun map(device: Device, target: Int, mapping: String, action: InputAction) {
        when (device) {
            Device.Keyboard -> keyMap[target] = mapping
            Device.Mouse -> mouseMap[target] = mapping
            Device.Controller -> TODO()
        }

        actions[mapping] = action
    }

    override fun remove(mapping: String) {
        val key = keyMap.entries.find { it.value == mapping }?.key
        keyMap.remove(key)
        actions.remove(mapping)
    }
}