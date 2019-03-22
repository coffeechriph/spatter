package spatter.tilemap

import rain.api.Input
import rain.api.gfx.Material
import rain.api.gfx.ResourceFactory
import rain.api.gfx.Texture2d
import rain.api.gfx.TextureFilter
import rain.api.gui.v2.guiManagerSetMaterial
import rain.api.scene.Camera
import rain.api.scene.Scene
import rain.api.scene.TileGfx
import rain.api.scene.Tilemap
import spatter.EditMode
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
        val defaultTileGfx = Array(numTileX * numTileY) {
            TileGfx(0, 0)
        }

        val tilemap = Tilemap()
        tilemap.create (
            resourceFactory,
            tilemapMaterial,
            numTileX,
            numTileY,
            tileW,
            tileH,
            defaultTileGfx
        )
        tilemap.update(defaultTileGfx)
        scene.addTilemap(tilemap)

        val defaultIndexSet = HashSet<Int>()
        for (i in 0 until numTileX * numTileY) {
            defaultIndexSet.add(i)
        }
        val defaultGroup = TileGroup(0, 0, defaultIndexSet)
        val defaultLayer =
            TilemapLayer(mutableListOf(defaultGroup), mutableListOf())
        defaultLayer.tileGfx = defaultTileGfx
        defaultLayer.tilemapRef = tilemap

        val tilemapData = TilemapData(numTileX, numTileY, tileW, tileH, ArrayList())
        tilemapData.activeLayer = defaultLayer
        tilemapData.layers.add(defaultLayer)
        currentProjectScene.mapData.add(tilemapData)
    }

    fun update(input: Input, camera: Camera) {
        // TODO: Allow to specify which tilemap to edit
        if (currentProjectScene.mapData.size > 0) {
            selectedTilemapData = currentProjectScene.mapData[0]
        }

        tilemapEditorDialog.update(selectedTilemapData, tilemapMaterial, scene, resourceFactory)
        if (tilemapEditorDialog.shown()) {
            if (tilemapEditorDialog.selectedEditorMode == EditMode.ADD) {
                if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.PRESSED ||
                    input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.DOWN
                ) {
                    val mx = input.mousePosition.x - camera.x
                    val my = input.mousePosition.y - camera.y
                    editTilemap(mx.toInt(), my.toInt(),
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
            val tx = ((x - activeTilemapLayer.transform.x) / activeTilemapLayer.tileWidth).toInt()
            val ty = ((y - activeTilemapLayer.transform.y) / activeTilemapLayer.tileHeight).toInt()

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

                selectedTilemapData!!.activeLayer.tileGfx[tileIndex] = TileGfx(imageX, imageY)
                activeTilemapLayer.update(selectedTilemapData!!.activeLayer.tileGfx)
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
