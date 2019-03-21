package spatter.entity

import org.joml.Vector2i
import rain.api.Window
import rain.api.gfx.Texture2d
import rain.api.gui.v2.*
import rain.assertion
import spatter.*

class EntityEditorDialog(private val window: Window): EditorDialog {
    private var layout: GridLayout = GridLayout()
    private var entityTypeLayout = FillRowLayout()
    private var imageSelectorPanel: Panel
    private var entityTypePanel: Panel
    private var metadataPanel: Panel

    private var metadataLayout = FillRowLayout()
    private var newMetadataEntryLabel: Label
    private var newMetadataEntryButton: Button

    var selectedEntity: ProjectEntity? = null
    private var currentProjectEntities = ArrayList<ToggleButton>()
    private var currentActiveMetadata = ArrayList<TextField>()

    var selectedImageIndex: Vector2i = Vector2i(0,0)
    var visible = false
        get() {
            return imageSelectorPanel.visible
        }
        private set

    private val images = ArrayList<Image>()
    private var updateImageSelector = false

    init {
        layout.gridW = 50.0f
        layout.gridH = 50.0f

        imageSelectorPanel = guiManagerCreatePanel(layout)
        imageSelectorPanel.w = 300.0f
        imageSelectorPanel.h = (window.size.y - TOOLS_PANEL_HEIGHT) / 3.0f
        imageSelectorPanel.skin = editorSkin
        imageSelectorPanel.visible = false
        imageSelectorPanel.moveable = false
        imageSelectorPanel.resizable = false

        entityTypeLayout.componentsPerRow = 2
        entityTypeLayout.componentHeight = 20.0f
        entityTypePanel = guiManagerCreatePanel(entityTypeLayout)
        entityTypePanel.w = 300.0f
        entityTypePanel.h = (window.size.y - TOOLS_PANEL_HEIGHT) / 3.0f
        entityTypePanel.skin = editorSkin
        entityTypePanel.visible = false
        entityTypePanel.moveable = false
        entityTypePanel.resizable = false

        metadataLayout.componentsPerRow = 2
        metadataLayout.componentHeight = 24.0f
        metadataPanel = guiManagerCreatePanel(metadataLayout)
        metadataPanel.w = 300.0f
        metadataPanel.h = (window.size.y - TOOLS_PANEL_HEIGHT) / 3.0f
        metadataPanel.skin = editorSkin
        metadataPanel.resizable = false
        metadataPanel.moveable = false
        metadataPanel.visible = false
        newMetadataEntryLabel = metadataPanel.createLabel("New Metadata")
        newMetadataEntryButton = metadataPanel.createButton("+")
    }

    fun update(projectScene: ProjectScene, selectedTilemapTexture: Texture2d) {
        entityTypePanel.x = window.size.x - entityTypePanel.w
        entityTypePanel.y = TOOLS_PANEL_HEIGHT

        imageSelectorPanel.x = window.size.x - imageSelectorPanel.w
        imageSelectorPanel.y = entityTypePanel.y + entityTypePanel.h

        metadataPanel.x = window.size.x - metadataPanel.w
        metadataPanel.y = imageSelectorPanel.y + imageSelectorPanel.h

        if (updateImageSelector) {
            updateImageSelector = false
            populateCurrentEntitiesButton(projectScene)
            for (image in images) {
                imageSelectorPanel.removeComponent(image)
            }

            val numTilesX = selectedTilemapTexture.getWidth() / selectedTilemapTexture.getTileWidthPixels()
            val numTilesY = selectedTilemapTexture.getHeight() / selectedTilemapTexture.getTileHeightPixels()

            var tx = 0
            var ty = 0
            for (i in 0 until numTilesX*numTilesY) {
                val image = imageSelectorPanel.createImage(tx, ty, "")
                images.add(image)

                tx += 1
                if (tx >= numTilesX) {
                    tx = 0
                    ty += 1
                }
            }

            entityTypePanel.x = window.size.x - entityTypePanel.w - imageSelectorPanel.w
            entityTypePanel.y = window.size.y - entityTypePanel.h - imageSelectorPanel.h
            imageSelectorPanel.x = entityTypePanel.x + entityTypePanel.w
            imageSelectorPanel.y = entityTypePanel.y
            metadataPanel.x = entityTypePanel.x - metadataPanel.w
            metadataPanel.y = entityTypePanel.y
        }

        // Select a new entity
        for (i in 0 until currentProjectEntities.size) {
            if (currentProjectEntities[i].clicked) {
                for (j in 0 until currentProjectEntities.size) {
                    if (j != i) {
                        currentProjectEntities[j].checked = false
                    }
                }

                this.selectedEntity = projectScene.entities[currentProjectEntities[i].string]
                if (this.selectedEntity == null) {
                    assertion("Selected entity is not found in project!")
                }
                populateMetadataButtons(selectedEntity!!)
                break
            }
        }

        if (selectedEntity != null && newMetadataEntryButton.clicked) {
            val metadata = SceneMetadata("name", "value")
            selectedEntity!!.metadata.add(metadata)

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
                    selectedEntity!!.metadata[index/2].name = metadataField.string
                }
                else {
                    selectedEntity!!.metadata[index/2].value = metadataField.string
                }

                break
            }

            index += 1
        }

        for(image in images) {
            if (image.clicked) {
                selectedImageIndex = Vector2i(image.imageTileIndexX, image.imageTileIndexY)
            }
        }
    }

    private fun populateCurrentEntitiesButton(projectScene: ProjectScene) {
        for (button in currentProjectEntities) {
            entityTypePanel.removeComponent(button)
        }
        currentProjectEntities.clear()

        for (entity in projectScene.entities) {
            val button = entityTypePanel.createToggleButton(entity.key)
            currentProjectEntities.add(button)
        }
    }

    private fun populateMetadataButtons(selectedEntity: ProjectEntity) {
        for (button in currentActiveMetadata) {
            metadataPanel.removeComponent(button)
        }
        currentActiveMetadata.clear()

        for (data in selectedEntity.metadata) {
            val nameField = metadataPanel.createTextField(data.name)
            val valueField = metadataPanel.createTextField(data.value)
            currentActiveMetadata.add(nameField)
            currentActiveMetadata.add(valueField)
        }
    }

    override fun shown(): Boolean {
        return imageSelectorPanel.visible
    }

    override fun hide() {
        imageSelectorPanel.visible = false
        entityTypePanel.visible = false
        metadataPanel.visible = false
    }

    override fun show() {
        updateImageSelector = true
        imageSelectorPanel.visible = true
        entityTypePanel.visible = true
        metadataPanel.visible = true
    }
}
