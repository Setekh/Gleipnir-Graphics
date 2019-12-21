package eu.corvus.corax.graphics.buffers.types

enum class BufferType(val size: Int) {
        Vertex(3),
        Indices(1),
        TextCoord(2),
        Normals(3);
        // Tangents
}