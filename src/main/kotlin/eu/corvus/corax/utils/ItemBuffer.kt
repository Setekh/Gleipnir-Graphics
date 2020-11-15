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
 * @author Vlad Ravenholm on 1/3/2020
 */
class ItemBuffer<E> {
    companion object {
        const val MIN_ARRAY_SIZE = 4
        const val MAX_ARRAY_SIZE = Int.MAX_VALUE
    }

    private var elements: Array<Any?> = Array(MIN_ARRAY_SIZE) { null }

    var index = 0
        private set

    var limit = 0
        private set

    var capacity = MIN_ARRAY_SIZE
        private set

    fun put(element: E): ItemBuffer<E> {
        ensureCapacity(index + 1)

        elements[index++] = element

        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun get(): E = elements[index++] as E

    fun flip(): ItemBuffer<E> {
        limit = index
        index = 0

        for(i in limit until capacity) {
            elements[i] = null
        }

        return this
    }

    private fun ensureCapacity(size: Int) {
        if (size > elements.size) {
            val oldCapacity = capacity
            val newCapacity = oldCapacity + (oldCapacity shr 1)

            val newElementsArray = Array<Any?>(newCapacity) { null }
            System.arraycopy(elements, 0, newElementsArray, 0, capacity)
            elements = newElementsArray

            capacity = newCapacity
        }
    }

    fun clear() {
        index = 0
        limit = 0
    }
}

