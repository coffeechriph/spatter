package spatter.project

import rain.api.scene.Tilemap
import rain.api.scene.parse.SceneMetadata

data class TileGroup constructor(
    val imageX: Int,
    val imageY: Int,
    val tileIndicesIntoMap: MutableSet<Int>)

data class TilemapLayer constructor(
    var tileGroup: MutableList<TileGroup>,
    var metadata: MutableList<SceneMetadata>,
    var tilemapRef: Tilemap
)

data class TilemapData constructor(
    var tileNumX: Int,
    var tileNumY: Int,
    var tileWidth: Float,
    var tileHeight: Float,
    var layers: MutableList<TilemapLayer>,
    var activeLayer: TilemapLayer
)
