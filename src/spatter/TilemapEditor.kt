package spatter

import org.joml.Vector2f
import rain.api.Input
import rain.api.gfx.Material
import rain.api.gfx.ResourceFactory
import rain.api.gfx.Texture2d
import rain.api.gfx.TextureFilter
import rain.api.gui.v2.guiManagerSetMaterial
import rain.api.scene.Scene
import rain.api.scene.TileGfx
import rain.api.scene.Tilemap
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.collections.HashSet

class TilemapEditor(private val resourceFactory: ResourceFactory, private val scene: Scene) {
    // TODO: Replace these with dynamic materials
    private var tilemapTexture: Texture2d
    private var tilemapMaterial: Material
    private val guiMaterial: Material

    var editMode: EditMode = EditMode.MOVE
    private var selectedTilemapData: TilemapData? = null
    private var beginMovePosition = false
    private var movePosition = false
    private var moveDiffStart = Vector2f(0.0f, 0.0f)

    private var tileSelector: TileSelector

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
            .withName("tilemapMaterial")
            .withVertexShader("./data/shaders/guiv2.vert.spv")
            .withFragmentShader("./data/shaders/gui.frag.spv")
            .withTexture(tilemapTexture)
            .withBlendEnabled(true)
            .build()
        guiManagerSetMaterial(guiMaterial)

        tileSelector = TileSelector(scene.window)
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
        tilemap.transform.setPosition(100.0f, 24.0f, 1.0f)
        scene.addTilemap(tilemap)

        val defaultIndexSet = HashSet<Int>()
        for (i in 0 until numTileX * numTileY) {
            defaultIndexSet.add(i)
        }
        val defaultGroup = TileGroup(0, 0, defaultIndexSet)
        val defaultLayer = TilemapLayer(mutableListOf(defaultGroup), defaultTileGfx, tilemap)
        val tilemapData = TilemapData(numTileX, numTileY, tileW, tileH, ArrayList(), defaultLayer)
        tilemapData.layers.add(defaultLayer)
        currentProjectScene.tilemapData.add(tilemapData)
    }

    fun update(input: Input) {
        tileSelector.update(selectedTilemapData, tilemapMaterial, scene, resourceFactory)

        if (input.keyState(Input.Key.KEY_1) == Input.InputState.PRESSED) {
            editMode = EditMode.MOVE
            tileSelector.visible = false
        }
        else if (input.keyState(Input.Key.KEY_2) == Input.InputState.PRESSED) {
            editMode = EditMode.EDIT

            if (!tileSelector.visible) {
                tileSelector.show(tilemapTexture)
            }
        }

        if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.PRESSED) {
            if (editMode == EditMode.MOVE) {
                selectTilemap(input.mousePosition.x, input.mousePosition.y)
                if (selectedTilemapData != null) {
                    if (!movePosition && !beginMovePosition) {
                        beginMovePosition = true
                    }
                }
            }
        }
        else if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.DOWN) {
            if (beginMovePosition) {
                beginMovePosition = false
                movePosition = true

                for (layer in selectedTilemapData!!.layers) {
                    moveDiffStart.x = input.mousePosition.x.toFloat() - layer.tilemapRef.transform.x
                    moveDiffStart.y = input.mousePosition.y.toFloat() - layer.tilemapRef.transform.y
                }
            }
        }
        else if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.RELEASED) {
            beginMovePosition = false
            movePosition = false
        }

        if (movePosition && selectedTilemapData != null) {
            for (layer in selectedTilemapData!!.layers) {
                layer.tilemapRef.transform.x = input.mousePosition.x.toFloat() - moveDiffStart.x
                layer.tilemapRef.transform.y = input.mousePosition.y.toFloat() - moveDiffStart.y
            }
        }

        if (editMode == EditMode.EDIT) {
            if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.PRESSED ||
                input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.DOWN) {
                editTilemap(input.mousePosition.x, input.mousePosition.y)
            }
        }
    }

    private fun editTilemap(x: Int, y: Int) {
        if (selectedTilemapData != null) {
            val activeTilemapLayer = selectedTilemapData!!.activeLayer.tilemapRef
            val tx = ((x - activeTilemapLayer.transform.x) / activeTilemapLayer.tileWidth).toInt()
            val ty = ((y - activeTilemapLayer.transform.y) / activeTilemapLayer.tileHeight).toInt()

            if (tx >= 0 && tx < activeTilemapLayer.tileNumX &&
                ty >= 0 && ty < activeTilemapLayer.tileNumY) {
                val imageX = tileSelector.selectedTileIndex.x
                val imageY = tileSelector.selectedTileIndex.y
                val tileIndex = tx + ty * activeTilemapLayer.tileNumX
                val activeLayer = selectedTilemapData!!.activeLayer

                val oldGroup = findGroupMatchingIndex(activeLayer, tileIndex)
                oldGroup.ifPresent { group -> group.tileIndicesIntoMap.remove(tileIndex) }

                val tileGroup = findGroupMatchingTile(activeLayer, imageX, imageY)
                tileGroup.ifPresentOrElse({group -> group.tileIndicesIntoMap.add(tileIndex)}, {createNewTileGroupWithTile(activeLayer, imageX, imageY, tileIndex)})

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

    private fun selectTilemap(x: Int, y: Int) {
        for (tilemapData in currentProjectScene.tilemapData) {
            val tilemap = tilemapData.activeLayer.tilemapRef
            if (x >= tilemap.transform.x && x <= tilemap.transform.x + tilemap.tileNumX * tilemap.tileWidth &&
                y >= tilemap.transform.y && y <= tilemap.transform.y + tilemap.tileNumY * tilemap.tileHeight) {
                selectedTilemapData = tilemapData
                break
            }
        }
    }
}
