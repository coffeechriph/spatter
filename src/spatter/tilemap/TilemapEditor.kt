package spatter.tilemap

import rain.api.Input
import rain.api.gfx.Material
import rain.api.gfx.ResourceFactory
import rain.api.gfx.Texture2d
import rain.api.gfx.TextureFilter
import rain.api.gui.v2.guiManagerSetMaterial
import rain.api.scene.Camera
import rain.api.scene.Scene
import spatter.EditMode
import spatter.project.TileGroup
import spatter.project.TilemapData
import spatter.project.TilemapLayer
import spatter.project.currentProjectScene
import java.util.*
import kotlin.collections.HashSet

class TilemapEditor(private val resourceFactory: ResourceFactory, private val scene: Scene, private val tilemapEditorDialog: TilemapEditorDialog) {
    // TODO: Replace these with dynamic materials
    private var tilemapTexture: Texture2d
    internal var tilemapMaterial: Material
    private val guiMaterial: Material

    private var selectedTilemapData: TilemapData? = null

    init {
        tilemapTexture = resourceFactory.buildTexture2d()
            .withName("tilemapTexture")
            .fromImageFile("./data/textures/tiles.png")
            .withFilter(TextureFilter.NEAREST)
            .build()

        tilemapTexture.setTiledTexture(16,16)
        tilemapMaterial = resourceFactory.buildMaterial()
            .withName("tilemapMaterial")
            .withVertexShader("./data/shaders/tilemap.vert.spv")
            .withFragmentShader("./data/shaders/tilemap.frag.spv")
            .withTexture(tilemapTexture)
            .withBlendEnabled(false)
            .build()

        guiMaterial = resourceFactory.buildMaterial()
            .withName("tilemapGuiMaterial")
            .withVertexShader("./data/shaders/guiv2.vert.spv")
            .withFragmentShader("./data/shaders/gui.frag.spv")
            .withTexture(tilemapTexture)
            .withBlendEnabled(true)
            .build()
        guiManagerSetMaterial(guiMaterial)
    }

    fun createTilemap(numTileX: Int, numTileY: Int, tileW: Float, tileH: Float) {
        val tilemap = scene.createTilemap(tilemapMaterial, numTileX, numTileY, tileW, tileH)
        var x = 0
        var y = 0
        for (i in 0 until numTileX*numTileY) {
            tilemap.setTile(x, y, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f)
            x += 1
            if (x >= numTileX) {
                x = 0
                y += 1
            }
        }

        val defaultIndexSet = HashSet<Int>()
        for (i in 0 until numTileX * numTileY) {
            defaultIndexSet.add(i)
        }
        val defaultGroup = TileGroup(0, 0, defaultIndexSet)
        val defaultLayer = TilemapLayer(mutableListOf(defaultGroup), mutableListOf(), tilemap)

        val tilemapData = TilemapData(numTileX, numTileY, tileW, tileH, mutableListOf(defaultLayer), defaultLayer)
        currentProjectScene.mapData.add(tilemapData)
    }

    fun update(input: Input, camera: Camera) {
        // TODO: Allow to specify which tilemap to edit
        if (currentProjectScene.mapData.size > 0) {
            selectedTilemapData = currentProjectScene.mapData[0]
        }
        else {
            selectedTilemapData = null
        }

        tilemapEditorDialog.update(selectedTilemapData, tilemapMaterial, scene)
        if (tilemapEditorDialog.shown()) {
            if (tilemapEditorDialog.selectedEditorMode == EditMode.ADD) {
                if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.PRESSED ||
                    input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.DOWN
                ) {
                    editTilemap(input.mousePosition.x, input.mousePosition.y,
                        tilemapEditorDialog.selectedTileIndex.x,
                        tilemapEditorDialog.selectedTileIndex.y)
                }
            }
            else if (tilemapEditorDialog.selectedEditorMode == EditMode.REMOVE) {
                if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.PRESSED ||
                    input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.DOWN
                ) {
                    editTilemap(input.mousePosition.x, input.mousePosition.y, -1, -1)
                }
            }
        }
    }

    private fun editTilemap(x: Int, y: Int, imageX: Int, imageY: Int) {
        if (selectedTilemapData != null) {
            val activeTilemapLayer = selectedTilemapData!!.activeLayer.tilemapRef
            val tx = ((x - activeTilemapLayer.transform.x - scene.activeCamera.x) / activeTilemapLayer.tileWidth).toInt()
            val ty = ((y - activeTilemapLayer.transform.y - scene.activeCamera.y) / activeTilemapLayer.tileHeight).toInt()

            if (tx >= 0 && tx < activeTilemapLayer.tileNumX &&
                ty >= 0 && ty < activeTilemapLayer.tileNumY) {
                val tileIndex = tx + ty * activeTilemapLayer.tileNumX
                val activeLayer = selectedTilemapData!!.activeLayer

                val oldGroup = findGroupMatchingIndex(activeLayer, tileIndex)
                oldGroup.ifPresent { group -> group.tileIndicesIntoMap.remove(tileIndex) }

                // Image index of -1 means that no tile should be present
                // Don't add it to a tile group
                if (imageX > -1 && imageY > -1) {
                    val tileGroup = findGroupMatchingTile(activeLayer, imageX, imageY)
                    tileGroup.ifPresentOrElse(
                        { group -> group.tileIndicesIntoMap.add(tileIndex) },
                        { createNewTileGroupWithTile(activeLayer, imageX, imageY, tileIndex) })
                }

                if(tilemapEditorDialog.selectedEditorMode == EditMode.ADD) {
                    activeTilemapLayer.setTile(tx, ty, imageX, imageY)
                }
                else {
                    activeTilemapLayer.removeTile(tx, ty)
                }
            }
        }
    }

    private fun findGroupMatchingIndex(tilemapLayer: TilemapLayer, tileIndex: Int): Optional<TileGroup> {
        return tilemapLayer.tileGroup.stream()
            .filter{a -> a.tileIndicesIntoMap.contains(tileIndex)}
            .findFirst()
    }

    private fun findGroupMatchingTile(tilemapLayer: TilemapLayer, imageX: Int, imageY: Int): Optional<TileGroup> {
        return tilemapLayer.tileGroup.stream()
            .filter { a -> a.imageX == imageX && a.imageY == imageY }
            .findFirst()
    }

    private fun createNewTileGroupWithTile(tilemapLayer: TilemapLayer, imageX: Int, imageY: Int, tileIndex: Int) {
        val group = TileGroup(imageX, imageY, mutableSetOf(tileIndex))
        tilemapLayer.tileGroup.add(group)
    }
}
