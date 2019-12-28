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
package eu.corvus.corax.tests

import eu.corvus.corax.app.storage.DesktopStorageAccess
import eu.corvus.corax.app.storage.StorageAccess
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

/**
 * @author Vlad Ravenholm on 12/28/2019
 */
class TestStorageAccess {
    companion object {
        const val TestFilePath = "test-data/test-file"
        const val TestFileContent = "This is the content."
        const val ResourceTestFileContent = "This is the resource file content"
    }

    private val storageAccess: StorageAccess by lazy { DesktopStorageAccess() }

    @BeforeEach
    fun setUp(): Unit {
        runBlocking {
            createTestFileToDisk()
        }
    }

    @AfterEach
    fun cleanup(): Unit {
        runBlocking {
            deleteTestFile()
        }
    }

    @Test
    fun `Test write & read with storage access`(): Unit = runBlocking {
        storageAccess.readFrom(TestFilePath) {
            val content = it.readBytes().toString(Charsets.UTF_8)
            assertEquals(TestFileContent, content)
        }
    }

    @Test
    fun `Test read with storage access from resource`(): Unit = runBlocking {
        deleteTestFile()

        storageAccess.readFrom(TestFilePath) {
            val content = it.readBytes().toString(Charsets.UTF_8)
            assertEquals(ResourceTestFileContent, content)
        }
    }

    @Test
    fun `Test delete with storage access`(): Unit = runBlocking {
        val result = deleteTestFile()
        assertTrue(result)
    }

    private suspend fun createTestFileToDisk() {
        storageAccess.writeTo(TestFilePath) {
            val writer = it.writer(Charsets.UTF_8)
            writer.write(TestFileContent)
            writer.flush()
        }
    }

    private suspend fun deleteTestFile(): Boolean {
        return storageAccess.delete(TestFilePath)
    }
}