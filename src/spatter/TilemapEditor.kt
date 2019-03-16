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

class TilemapEditor(private val resourceFactory: ResourceFactory, private val scene: Scene) {
    // TODO: Replace these with dynamic materials
    private var tilemapTexture: Texture2d
    private var tilemapMaterial: Material
    private val guiMaterial: Material

    val tilemaps = ArrayList<Pair<Array<TileGfx>, Tilemap>>()
    var editMode: EditMode = EditMode.MOVE
    private var selectedTilemap: Tilemap? = null
    private var selectedTilemapData: Array<TileGfx>? = null
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
            TileGfx(0, 1)
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
        tilemaps.add(Pair(defaultTileGfx, tilemap))
    }

    fun update(input: Input) {
        tileSelector.update()

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
                if (selectedTilemap != null) {
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

                moveDiffStart.x = input.mousePosition.x.toFloat() - selectedTilemap!!.transform.x
                moveDiffStart.y = input.mousePosition.y.toFloat() - selectedTilemap!!.transform.y
            }
        }
        else if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.RELEASED) {
            beginMovePosition = false
            movePosition = false
        }

        if (movePosition && selectedTilemap != null) {
            selectedTilemap!!.transform.x = input.mousePosition.x.toFloat() - moveDiffStart.x
            selectedTilemap!!.transform.y = input.mousePosition.y.toFloat() - moveDiffStart.y
        }

        if (editMode == EditMode.EDIT) {
            if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.PRESSED ||
                input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.DOWN) {
                editTilemap(input.mousePosition.x, input.mousePosition.y)
            }
        }
    }

    private fun editTilemap(x: Int, y: Int) {
        if (selectedTilemap != null) {
            val tx = ((x - selectedTilemap!!.transform.x) / selectedTilemap!!.tileWidth).toInt()
            val ty = ((y - selectedTilemap!!.transform.y) / selectedTilemap!!.tileHeight).toInt()

            if (tx >= 0 && tx < selectedTilemap!!.tileNumX &&
                ty >= 0 && ty < selectedTilemap!!.tileNumY) {
                selectedTilemapData!![tx + ty * selectedTilemap!!.tileNumX] =
                    TileGfx(
                        tileSelector.selectedTileIndex.x,
                        tileSelector.selectedTileIndex.y
                    )
                selectedTilemap!!.update(selectedTilemapData!!)
            }
        }
    }

    private fun selectTilemap(x: Int, y: Int) {
        for (tilemapData in tilemaps) {
            val tilemap = tilemapData.second
            if (x >= tilemap.transform.x && x <= tilemap.transform.x + tilemap.tileNumX * tilemap.tileWidth &&
                y >= tilemap.transform.y && y <= tilemap.transform.y + tilemap.tileNumY * tilemap.tileHeight) {
                selectedTilemap = tilemap
                selectedTilemapData = tilemapData.first
                break
            }
        }
    }
}