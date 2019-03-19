package spatter

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import rain.api.scene.TileGfx
import rain.api.scene.Tilemap

class TileGroup @JsonCreator constructor(
    @JsonProperty("image_x")
    val imageX: Int,
    @JsonProperty("image_y")
    val imageY: Int,
    @JsonProperty("tile_indices_into_map")
    val tileIndicesIntoMap: MutableSet<Int>)

class TilemapData @JsonCreator constructor(
    @JsonProperty("tile_num_x")
    var tileNumX: Int,
    @JsonProperty("tile_num_y")
    var tileNumY: Int,
    @JsonProperty("tile_width")
    var tileWidth: Float,
    @JsonProperty("tile_height")
    var tileHeight: Float,
    @JsonProperty("tile_group")
    var tileGroup: MutableList<TileGroup>,
    @JsonIgnore
    var tileGfx: Array<TileGfx>,
    @JsonIgnore
    var tilemapRef: Tilemap
)
