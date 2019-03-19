package spatter

import org.joml.Vector2i
import rain.api.Window
import rain.api.gfx.Material
import rain.api.gfx.ResourceFactory
import rain.api.gfx.Texture2d
import rain.api.gui.v2.*
import rain.api.scene.Scene
import rain.api.scene.TileGfx
import rain.api.scene.Tilemap

class TilemapEditorProperties(private val window: Window) {
    private var layout: GridLayout = GridLayout()
    private var tileLayerLayout = FillRowLayout()
    private var tileSelectorPanel: Panel
    private var tileLayerPanel: Panel
    private var createTileLayerLabel: Label
    private var createTileLayerButton: Button
    private var selectedTilemapData: TilemapData? = null
    private var currentActiveLayers = ArrayList<ToggleButton>()

    var visible = false
        set(value) {
            field = value
            tileSelectorPanel.visible = value
            tileLayerPanel.visible = value
        }
        get() {
            return tileSelectorPanel.visible
        }
    var created = false
        private set
    var selectedTileIndex: Vector2i = Vector2i(0,0)

    val images = ArrayList<Image>()
    init {
        layout.gridW = 50.0f
        layout.gridH = 50.0f

        tileSelectorPanel = guiManagerCreatePanel(layout)
        tileSelectorPanel.w = 400.0f
        tileSelectorPanel.h = 400.0f
        tileSelectorPanel.skin = editorSkin
        tileSelectorPanel.visible = false

        tileLayerLayout.componentsPerRow = 2
        tileLayerLayout.componentHeight = 20.0f
        tileLayerPanel = guiManagerCreatePanel(tileLayerLayout)
        tileLayerPanel.w = 200.0f
        tileLayerPanel.h = 400.0f
        tileLayerPanel.skin = editorSkin
        tileLayerPanel.visible = false

        createTileLayerLabel = tileLayerPanel.createLabel("Layers")
        createTileLayerButton = tileLayerPanel.createButton("+")
    }

    fun update(selectedTilemapData: TilemapData?, tileMaterial: Material, scene: Scene, resourceFactory: ResourceFactory) {
        tileSelectorPanel.x = tileLayerPanel.x + tileLayerPanel.w
        tileSelectorPanel.y = tileLayerPanel.y

        if (selectedTilemapData != null) {
            populateActiveLayerButtons(selectedTilemapData)

            // Change active layer
            for (i in 0 until currentActiveLayers.size) {
                if (currentActiveLayers[i].clicked) {
                    for (j in 0 until currentActiveLayers.size) {
                        if (j != i) {
                            currentActiveLayers[j].checked = false
                        }
                    }

                    // Tint the inactive layer
                    for (tile in selectedTilemapData.activeLayer.tileGfx) {
                        tile.red = 0.75f
                        tile.green = 0.75f
                        tile.blue = 0.75f
                    }
                    selectedTilemapData.activeLayer.tilemapRef.update(selectedTilemapData.activeLayer.tileGfx)

                    selectedTilemapData.activeLayer = selectedTilemapData.layers[i]

                    // Remove potential tint from active
                    for (tile in selectedTilemapData.activeLayer.tileGfx) {
                        tile.red = 1.0f
                        tile.green = 1.0f
                        tile.blue = 1.0f
                    }
                    selectedTilemapData.activeLayer.tilemapRef.update(selectedTilemapData.activeLayer.tileGfx)
                    break
                }
            }

            if (createTileLayerButton.clicked) {
                val tileGfx = Array(selectedTilemapData.tileNumX*selectedTilemapData.tileNumY){ TileGfx(0, 0) }
                val tilemap = Tilemap()
                tilemap.create(
                    resourceFactory,
                    tileMaterial,
                    selectedTilemapData.tileNumX,
                    selectedTilemapData.tileNumY,
                    selectedTilemapData.tileWidth,
                    selectedTilemapData.tileHeight,
                    tileGfx
                )
                scene.addTilemap(tilemap)
                tilemap.transform.x = selectedTilemapData.activeLayer.tilemapRef.transform.x
                tilemap.transform.y = selectedTilemapData.activeLayer.tilemapRef.transform.y
                tilemap.transform.z = selectedTilemapData.activeLayer.tilemapRef.transform.z + 1.0f
                val newLayer = TilemapLayer(mutableListOf(), tileGfx, tilemap)
                selectedTilemapData.layers.add(newLayer)

                val button = tileLayerPanel.createToggleButton("Layer:${currentActiveLayers.size}")
                currentActiveLayers.add(button)
            }
        }

        created = false

        for(image in images) {
            if (image.clicked) {
                selectedTileIndex = Vector2i(image.imageTileIndexX, image.imageTileIndexY)
            }
        }
    }

    private fun populateActiveLayerButtons(selectedTilemapData: TilemapData) {
        if (this.selectedTilemapData != selectedTilemapData) {
            for (button in currentActiveLayers) {
                tileLayerPanel.removeComponent(button)
            }
            this.selectedTilemapData = selectedTilemapData
            currentActiveLayers.clear()

            for (i in 0 until selectedTilemapData.layers.size) {
                val button = tileLayerPanel.createToggleButton("Layer:$i")
                currentActiveLayers.add(button)
            }
        }
    }

    fun show(selectedTilemapTexture: Texture2d) {
        if (!tileSelectorPanel.visible) {
            for (image in images) {
                tileSelectorPanel.removeComponent(image)
            }

            val numTilesX = selectedTilemapTexture.getWidth() / selectedTilemapTexture.getTileWidthPixels()
            val numTilesY = selectedTilemapTexture.getHeight() / selectedTilemapTexture.getTileHeightPixels()

            var tx = 0
            var ty = 0
            for (i in 0 until numTilesX*numTilesY) {
                val image = tileSelectorPanel.createImage(tx, ty, "")
                images.add(image)

                tx += 1
                if (tx >= numTilesX) {
                    tx = 0
                    ty += 1
                }
            }

            tileLayerPanel.x = window.size.x - tileLayerPanel.w - tileSelectorPanel.w
            tileLayerPanel.y = window.size.y - tileLayerPanel.h - tileSelectorPanel.h
            tileSelectorPanel.x = tileLayerPanel.x + tileLayerPanel.w
            tileSelectorPanel.y = tileLayerPanel.y
        }
        tileSelectorPanel.visible = true
        tileLayerPanel.visible = true
    }
}
