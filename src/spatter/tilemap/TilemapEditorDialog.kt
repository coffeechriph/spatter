package spatter.tilemap

import org.joml.Vector2i
import rain.api.Window
import rain.api.gfx.Material
import rain.api.gfx.ResourceFactory
import rain.api.gfx.Texture2d
import rain.api.gui.v2.*
import rain.api.scene.Scene
import rain.api.scene.TileGfxNone
import rain.api.scene.Tilemap
import spatter.*

class TilemapEditorDialog(private val window: Window): EditorDialog {
    private var layout: GridLayout = GridLayout()
    private var tileLayerLayout = FillRowLayout()
    private var tileSelectorPanel: Panel
    private var tileLayerPanel: Panel
    private var metadataPanel: Panel

    private var tileEditorToolPanel: Panel
    private var tileEditorMoveButton: ToggleButton
    private val tileEditorEditButton: ToggleButton

    private var createTileLayerLabel: Label
    private var createTileLayerButton: Button

    private var metadataLayout = FillRowLayout()
    private var newMetadataEntryLabel: Label
    private var newMetadataEntryButton: Button

    private var selectedTilemapData: TilemapData? = null
    private var currentActiveLayers = ArrayList<ToggleButton>()

    private var currentActiveMetadata = ArrayList<TextField>()

    var selectedTileIndex: Vector2i = Vector2i(0,0)
        private set
    var selectedEditorMode: EditMode = EditMode.MOVE
        private set

    private val images = ArrayList<Image>()

    private var updateTileSelector = false

    init {
        val tileToolPanelLayout = FillRowLayout()
        tileToolPanelLayout.componentsPerRow = 2
        tileEditorToolPanel = guiManagerCreatePanel(tileToolPanelLayout)
        tileEditorToolPanel.h = 30.0f
        tileEditorToolPanel.w = 300.0f
        tileEditorToolPanel.y = TOOLS_PANEL_HEIGHT
        tileEditorToolPanel.skin = editorSkin
        tileEditorToolPanel.visible = false

        tileEditorMoveButton = tileEditorToolPanel.createToggleButton("Move")
        tileEditorMoveButton.checked = true
        tileEditorEditButton = tileEditorToolPanel.createToggleButton("Edit")

        layout.gridW = 50.0f
        layout.gridH = 50.0f

        val panelHeight = (window.size.y - TOOLS_PANEL_HEIGHT) / 3.0f - 10.0f
        tileSelectorPanel = guiManagerCreatePanel(layout)
        tileSelectorPanel.w = 300.0f
        tileSelectorPanel.h = panelHeight
        tileSelectorPanel.skin = editorSkin
        tileSelectorPanel.visible = false
        tileSelectorPanel.moveable = false
        tileSelectorPanel.resizable = false

        tileLayerLayout.componentsPerRow = 2
        tileLayerLayout.componentHeight = 20.0f
        tileLayerPanel = guiManagerCreatePanel(tileLayerLayout)
        tileLayerPanel.w = 300.0f
        tileLayerPanel.h = panelHeight
        tileLayerPanel.skin = editorSkin
        tileLayerPanel.visible = false
        tileLayerPanel.moveable = false
        tileLayerPanel.resizable = false

        createTileLayerLabel = tileLayerPanel.createLabel("Layers")
        createTileLayerButton = tileLayerPanel.createButton("+")

        metadataLayout.componentsPerRow = 2
        metadataLayout.componentHeight = 24.0f
        metadataPanel = guiManagerCreatePanel(metadataLayout)
        metadataPanel.w = 300.0f
        metadataPanel.h = panelHeight
        metadataPanel.skin = editorSkin
        metadataPanel.resizable = false
        metadataPanel.moveable = false
        metadataPanel.visible = false
        newMetadataEntryLabel = metadataPanel.createLabel("New Metadata")
        newMetadataEntryButton = metadataPanel.createButton("+")
    }

    fun update(selectedTilemapData: TilemapData?, tileMaterial: Material, scene: Scene, resourceFactory: ResourceFactory) {
        tileEditorToolPanel.x = window.size.x - tileEditorToolPanel.w
        tileEditorToolPanel.y = TOOLS_PANEL_HEIGHT

        tileLayerPanel.x = window.size.x - tileLayerPanel.w
        tileLayerPanel.y = TOOLS_PANEL_HEIGHT + tileEditorToolPanel.y

        tileSelectorPanel.x = window.size.x - tileSelectorPanel.w
        tileSelectorPanel.y = tileLayerPanel.y + tileLayerPanel.h

        metadataPanel.x = window.size.x - metadataPanel.w
        metadataPanel.y = tileSelectorPanel.y + tileSelectorPanel.h

        if (tileEditorMoveButton.clicked) {
            tileEditorEditButton.checked = false
            selectedEditorMode = EditMode.MOVE
        }
        else if (tileEditorEditButton.clicked){
            tileEditorMoveButton.checked = false
            selectedEditorMode = EditMode.EDIT
        }

        if (updateTileSelector) {
            updateTileSelector = false
            for (image in images) {
                tileSelectorPanel.removeComponent(image)
            }

            val numTilesX = tileMaterial.getTexture2d()[0].getWidth() / tileMaterial.getTexture2d()[0].getTileWidthPixels()
            val numTilesY = tileMaterial.getTexture2d()[0].getHeight() / tileMaterial.getTexture2d()[0].getTileHeightPixels()

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
            metadataPanel.x = tileLayerPanel.x - metadataPanel.w
            metadataPanel.y = tileLayerPanel.y
        }

        if (selectedTilemapData != null) {
            populateActiveLayerButtons(selectedTilemapData)
            this.selectedTilemapData = selectedTilemapData

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
                    populateMetadataButtons(selectedTilemapData)

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
                val tileGfx = Array(selectedTilemapData.tileNumX*selectedTilemapData.tileNumY){ TileGfxNone }
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

                val newLayer =
                    TilemapLayer(mutableListOf(), mutableListOf(), tileGfx, tilemap)
                selectedTilemapData.layers.add(newLayer)

                val button = tileLayerPanel.createToggleButton("Layer:${currentActiveLayers.size}")
                currentActiveLayers.add(button)
            }

            if (newMetadataEntryButton.clicked) {
                val metadata = SceneMetadata("name", "value")
                selectedTilemapData.activeLayer.metadata.add(metadata)

                val nameField = metadataPanel.createTextField(metadata.name)
                val valueField = metadataPanel.createTextField(metadata.value)
                currentActiveMetadata.add(nameField)
                currentActiveMetadata.add(valueField)
            }

            var index = 0
            for (metadataField in currentActiveMetadata) {
                if (metadataField.textEdited) {

                    // Name fields end up at even indices
                    if (index % 2 == 0) {
                        selectedTilemapData.activeLayer.metadata[index/2].name = metadataField.string
                    }
                    else {
                        selectedTilemapData.activeLayer.metadata[index/2].value = metadataField.string
                    }

                    break
                }

                index += 1
            }
        }

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

                if (selectedTilemapData.layers.indexOf(selectedTilemapData.activeLayer) == i) {
                    button.checked = true
                }

                currentActiveLayers.add(button)
            }
            populateMetadataButtons(selectedTilemapData)
        }
    }

    private fun populateMetadataButtons(selectedTilemapData: TilemapData) {
        for (button in currentActiveMetadata) {
            metadataPanel.removeComponent(button)
        }
        currentActiveMetadata.clear()

        for (data in selectedTilemapData.activeLayer.metadata) {
            val nameField = metadataPanel.createTextField(data.name)
            val valueField = metadataPanel.createTextField(data.value)
            currentActiveMetadata.add(nameField)
            currentActiveMetadata.add(valueField)
        }
    }

    override fun shown(): Boolean {
        return tileSelectorPanel.visible
    }

    override fun hide() {
        tileSelectorPanel.visible = false
        tileLayerPanel.visible = false
        metadataPanel.visible = false
        tileEditorToolPanel.visible = false
    }

    override fun show() {
        updateTileSelector = true
        tileSelectorPanel.visible = true
        tileLayerPanel.visible = true
        metadataPanel.visible = true
        tileEditorToolPanel.visible = true
    }
}