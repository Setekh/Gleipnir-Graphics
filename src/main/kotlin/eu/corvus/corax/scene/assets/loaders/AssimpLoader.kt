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
package eu.corvus.corax.scene.assets.loaders

import eu.corvus.corax.app.storage.StorageAccess
import eu.corvus.corax.scene.Object
import eu.corvus.corax.scene.Spatial
import eu.corvus.corax.scene.assets.AssetManager
import eu.corvus.corax.scene.geometry.Mesh
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.Assimp.*
import org.lwjgl.system.MemoryUtil

/**
 * @author Vlad Ravenholm on 12/28/2019
 */
class AssimpLoader : AssetManager.AssetLoader {
    override suspend fun load(assetManager: AssetManager, storageAccess: StorageAccess, path: String): Object {
        val spatial = Spatial(path)
        storageAccess.readFrom(path) {
            val alloc = MemoryUtil.memAlloc(it.available())
            alloc.put(it.readBytes()).flip()

            val suffix = path.substringAfterLast('.')
            val aiScene = aiImportFileFromMemory(alloc,
                aiProcess_JoinIdenticalVertices or aiProcess_Triangulate or aiProcess_FixInfacingNormals or aiProcess_CalcTangentSpace,
                suffix
            ) ?: error("Failed loading asset $path!")


            val numMeshes = aiScene.mNumMeshes()
            val aiMeshes = aiScene.mMeshes()!!
            repeat(numMeshes) { index ->
                val aiMesh = AIMesh.create(aiMeshes.get(index))
                aiMesh.mBones()
                val vertexes = aiMesh.getVertices()
                val texCoords = aiMesh.getTexCoords()
                val normals = aiMesh.getNormals()
                val tangents = aiMesh.getTangents()
                val bitangents = aiMesh.getBitangents()
                val indices = aiMesh.getIndices()

                val geometry = Mesh(aiMesh.mName().dataString())
                geometry.createMesh(vertexes, indices, texCoords, normals, tangents, bitangents)
                geometry.forceUpdate()
                spatial.appendChild(geometry)
            }
        }

        return spatial
    }
}

fun AIMesh.getVertices(): FloatArray = mVertices().map { listOf(it.x(), it.y(), it.z()) }.flatten().toFloatArray()
fun AIMesh.getTexCoords(): FloatArray? = mTextureCoords(0)?.map { listOf(it.x(), it.y()) }?.flatten()?.toFloatArray()

fun AIMesh.getNormals(): FloatArray? = mNormals()?.map { listOf(it.x(), it.y(), it.z()) }?.flatten()?.toFloatArray()
fun AIMesh.getTangents(): FloatArray? = mTangents()?.map { listOf(it.x(), it.y(), it.z()) }?.flatten()?.toFloatArray()
fun AIMesh.getBitangents(): FloatArray? = mBitangents()?.map { listOf(it.x(), it.y(), it.z()) }?.flatten()?.toFloatArray()

fun AIMesh.getIndices(): IntArray = mFaces().map {
    val collect = mutableListOf<Int>()
    val mIndices = it.mIndices()
    while (mIndices.remaining() > 0)
        collect.add(mIndices.get())

    collect
}.flatten().toIntArray()