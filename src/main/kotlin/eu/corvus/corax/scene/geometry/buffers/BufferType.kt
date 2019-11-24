package eu.corvus.corax.scene.geometry.buffers

enum class BufferType(val size: Int) {
        Vertex(3),
        Indices(1),
        TextCoord(2),
        Normals(3);
        // Tangents
}