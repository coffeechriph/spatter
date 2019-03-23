package spatter.project

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import rain.api.scene.Tilemap

@Serializable
data class TileGroup constructor(
    @SerialName("image_x")
    val imageX: Int,
    @SerialName("image_y")
    val imageY: Int,
    @SerialName("tile_indices_into_map")
    val tileIndicesIntoMap: MutableSet<Int>)

@Serializable
data class TilemapLayer constructor(
    @SerialName("tile_group")
    var tileGroup: MutableList<TileGroup>,
    @SerialName("metadata")
    var metadata: MutableList<SceneMetadata>
) {
    @Transient
    lateinit var tilemapRef: Tilemap

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TilemapLayer

        if (tileGroup != other.tileGroup) return false
        if (tilemapRef != other.tilemapRef) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tileGroup.hashCode()
        result = 31 * result + tilemapRef.hashCode()
        return result
    }
}

@Serializable
data class TilemapData constructor(
    @SerialName("tile_num_x")
    var tileNumX: Int,
    @SerialName("tile_num_y")
    var tileNumY: Int,
    @SerialName("tile_width")
    var tileWidth: Float,
    @SerialName("tile_height")
    var tileHeight: Float,
    @SerialName("layers")
    var layers: MutableList<TilemapLayer>
) {
    @Transient
    lateinit var activeLayer: TilemapLayer
}
