package spatter.tilemap

import org.joml.Vector2i
import rain.api.Window
import rain.api.gfx.Material
import rain.api.gfx.ResourceFactory
import rain.api.gui.v2.*
import rain.api.scene.Scene
import rain.api.scene.parse.SceneMetadata
import spatter.EditMode
import spatter.EditorDialog
import spatter.TOOLS_PANEL_HEIGHT
import spatter.editorSkin
import spatter.project.TilemapData
import spatter.project.TilemapLayer

class TilemapEditorDialog(private val window: Window): EditorDialog {
    private var layout: GridLayout = GridLayout()
    private var tileLayerLayout = FillRowLayout()
    private var tileSelectorPanel: Panel
    private var tileLayerPanel: Panel
    private var metadataPanel: Panel

    private var tileEditorToolPanel: rain.api.gui.v2.Window
    private val tileEditorEditButton: ToggleButton
    private val tileEditorRemoveButton: ToggleButton

    private var createTileLayerLabel: Label
    private var createTileLayerButton: Button

    private var metadataLayout = FillRowLayout()
    private var newMetadataEntryButton: Button

    private var selectedTilemapData: TilemapData? = null
    private var currentActiveLayers = ArrayList<ToggleButton>()

    private var currentActiveMetadata = ArrayList<TextField>()
    private var currentActiveMetadataRemoveButtons = ArrayList<Button>()

    var selectedTileIndex: Vector2i = Vector2i(0,0)
        private set
    var selectedEditorMode: EditMode = EditMode.ADD
        private set

    private val images = ArrayList<Image>()

    private var updateTileSelector = false

    init {
        val tileToolPanelLayout = FillRowLayout()
        tileToolPanelLayout.componentsPerRow = 3
        tileEditorToolPanel = guiManagerCreateWindow(tileToolPanelLayout, "Tile Edit")
        tileEditorToolPanel.h = 65.0f
        tileEditorToolPanel.w = 300.0f
        tileEditorToolPanel.y = TOOLS_PANEL_HEIGHT
        tileEditorToolPanel.skin = editorSkin
        tileEditorToolPanel.visible = false

        tileEditorEditButton = tileEditorToolPanel.createToggleButton("Edit")
        tileEditorEditButton.checked = true
        tileEditorRemoveButton = tileEditorToolPanel.createToggleButton("Remove")

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

        metadataLayout.componentsPerRow = 3
        metadataLayout.componentHeight = 24.0f
        metadataPanel = guiManagerCreatePanel(metadataLayout)
        metadataPanel.w = 300.0f
        metadataPanel.h = panelHeight
        metadataPanel.skin = editorSkin
        metadataPanel.resizable = false
        metadataPanel.moveable = false
        metadataPanel.visible = false
        newMetadataEntryButton = metadataPanel.createButton("+")
    }

    fun update(selectedTilemapData: TilemapData?, tileMaterial: Material, scene: Scene) {
        tileSelectorPanel.visible = tileEditorToolPanel.visible
        metadataPanel.visible = tileEditorToolPanel.visible
        tileLayerPanel.visible = tileEditorToolPanel.visible

        tileEditorToolPanel.x = window.size.x - tileEditorToolPanel.w
        tileEditorToolPanel.y = TOOLS_PANEL_HEIGHT

        tileLayerPanel.x = window.size.x - tileLayerPanel.w
        tileLayerPanel.y = tileEditorToolPanel.y + tileEditorToolPanel.h

        tileSelectorPanel.x = window.size.x - tileSelectorPanel.w
        tileSelectorPanel.y = tileLayerPanel.y + tileLayerPanel.h

        metadataPanel.x = window.size.x - metadataPanel.w
        metadataPanel.y = tileSelectorPanel.y + tileSelectorPanel.h

        if (tileEditorEditButton.clicked){
            tileEditorRemoveButton.checked = false
            selectedEditorMode = EditMode.ADD
        }
        else if (tileEditorRemoveButton.clicked){
            tileEditorEditButton.checked = false
            selectedEditorMode = EditMode.REMOVE
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
                    for (k in 0 until selectedTilemapData.tileNumY*selectedTilemapData.tileNumX) {
                        val tileX = k % selectedTilemapData.tileNumX
                        val tileY = k / selectedTilemapData.tileNumX
                        val imageIndices = selectedTilemapData.activeLayer.tilemapRef.getTileImageIndex(tileX, tileY)
                        selectedTilemapData.activeLayer.tilemapRef.setTile(
                            tileX,
                            tileY,
                            imageIndices.first,
                            imageIndices.second,
                            0.75f,
                            0.75f,
                            0.75f,
                            1.0f
                        )
                    }

                    selectedTilemapData.activeLayer = selectedTilemapData.layers[i]
                    populateMetadataButtons(selectedTilemapData)

                    // Remove potential tint from active
                    for (k in 0 until selectedTilemapData.tileNumY*selectedTilemapData.tileNumX) {
                        val tileX = k % selectedTilemapData.tileNumX
                        val tileY = k / selectedTilemapData.tileNumX
                        val imageIndices = selectedTilemapData.activeLayer.tilemapRef.getTileImageIndex(tileX, tileY)
                        selectedTilemapData.activeLayer.tilemapRef.setTile(
                            tileX,
                            tileY,
                            imageIndices.first,
                            imageIndices.second,
                            1.0f,
                            1.0f,
                            1.0f,
                            1.0f
                        )
                    }
                    break
                }
            }

            if (createTileLayerButton.clicked) {
                val tilemap = scene.createTilemap(
                    tileMaterial,
                    selectedTilemapData.tileNumX,
                    selectedTilemapData.tileNumY,
                    selectedTilemapData.tileWidth,
                    selectedTilemapData.tileHeight)

                tilemap.transform.z = selectedTilemapData.activeLayer.tilemapRef.transform.z + 1.0f

                val newLayer = TilemapLayer(mutableListOf(), mutableListOf(), tilemap)
                selectedTilemapData.layers.add(newLayer)

                val button = tileLayerPanel.createToggleButton("Layer:${currentActiveLayers.size}")
                currentActiveLayers.add(button)
            }

            if (newMetadataEntryButton.clicked) {
                metadataPanel.removeComponent(newMetadataEntryButton)
                val metadata = SceneMetadata("name", "value")
                selectedTilemapData.activeLayer.metadata.add(metadata)

                val nameField = metadataPanel.createTextField(metadata.name)
                val valueField = metadataPanel.createTextField(metadata.value)
                val removeButton = metadataPanel.createButton("-")
                currentActiveMetadata.add(nameField)
                currentActiveMetadata.add(valueField)
                currentActiveMetadataRemoveButtons.add(removeButton)
                newMetadataEntryButton = metadataPanel.createButton("+")
            }

            var buttonIndex = -1
            for ((index,button) in currentActiveMetadataRemoveButtons.withIndex()) {
                if (button.clicked) {
                    selectedTilemapData.activeLayer.metadata.removeAt(index)
                    metadataPanel.removeComponent(currentActiveMetadata[index*2])
                    metadataPanel.removeComponent(currentActiveMetadata[index*2+1])
                    metadataPanel.removeComponent(button)
                    currentActiveMetadata.removeAt(index*2)
                    currentActiveMetadata.removeAt(index*2)
                    buttonIndex = index
                    break
                }
            }

            if (buttonIndex >= 0) {
                currentActiveMetadataRemoveButtons.removeAt(buttonIndex)
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
        for (field in currentActiveMetadata) {
            metadataPanel.removeComponent(field)
        }
        currentActiveMetadata.clear()

        for (button in currentActiveMetadataRemoveButtons) {
            metadataPanel.removeComponent(button)
        }
        currentActiveMetadataRemoveButtons.clear()

        metadataPanel.removeComponent(newMetadataEntryButton)
        for (data in selectedTilemapData.activeLayer.metadata) {
            val nameField = metadataPanel.createTextField(data.name)
            val valueField = metadataPanel.createTextField(data.value)
            val removeButton = metadataPanel.createButton("-")
            currentActiveMetadata.add(nameField)
            currentActiveMetadata.add(valueField)
            currentActiveMetadataRemoveButtons.add(removeButton)
        }
        newMetadataEntryButton = metadataPanel.createButton("+")
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
