package spatter.tilemap

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import rain.api.scene.TileGfx
import rain.api.scene.TileGfxNone
import rain.api.scene.Tilemap
import spatter.project.SceneMetadata

data class TileGroup @JsonCreator constructor(
    @JsonProperty("image_x")
    val imageX: Int,
    @JsonProperty("image_y")
    val imageY: Int,
    @JsonProperty("tile_indices_into_map")
    val tileIndicesIntoMap: MutableSet<Int>)

data class TilemapLayer @JsonCreator constructor(
    @JsonProperty("tile_group")
    var tileGroup: MutableList<TileGroup>,
    @JsonProperty("metadata")
    var metadata: MutableList<SceneMetadata>
) {
    @JsonIgnore
    var tileGfx: Array<TileGfx> = Array(0){ TileGfxNone }
        @JsonIgnore
        get() {
            return field
        }
        @JsonIgnore
        set(value) {
            field = value
        }
    @JsonIgnore
    var tilemapRef: Tilemap = Tilemap()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TilemapLayer

        if (tileGroup != other.tileGroup) return false
        if (!tileGfx.contentEquals(other.tileGfx)) return false
        if (tilemapRef != other.tilemapRef) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tileGroup.hashCode()
        result = 31 * result + tileGfx.contentHashCode()
        result = 31 * result + tilemapRef.hashCode()
        return result
    }
}

data class TilemapData @JsonCreator constructor(
    @JsonProperty("tile_num_x")
    var tileNumX: Int,
    @JsonProperty("tile_num_y")
    var tileNumY: Int,
    @JsonProperty("tile_width")
    var tileWidth: Float,
    @JsonProperty("tile_height")
    var tileHeight: Float,
    @JsonProperty("layers")
    var layers: MutableList<TilemapLayer>
) {
    @JsonIgnore
    lateinit var activeLayer: TilemapLayer
}
