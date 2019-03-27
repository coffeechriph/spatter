package spatter.entity

import org.joml.Vector2i
import rain.api.Window
import rain.api.gfx.Texture2d
import rain.api.gui.v2.*
import rain.api.scene.parse.SceneMetadata
import rain.assertion
import spatter.EditorDialog
import spatter.TOOLS_PANEL_HEIGHT
import spatter.editorSkin
import spatter.project.ProjectEntity
import spatter.project.ProjectScene
import spatter.project.currentProjectScene

class EntityEditorDialog(private val window: Window): EditorDialog {
    private var layout: GridLayout = GridLayout()
    private var entityTypeLayout = FillRowLayout()
    private var imageSelectorPanel: Panel
    private var entityTypePanel: Panel
    private var entityPropertiesPanel: Panel
    private var entityWidthLabel: Label
    private var entityHeightLabel: Label
    private val entityWidthField: TextField
    private val entityHeightField: TextField
    private var metadataPanel: Panel
    private var metadataLayout = FillRowLayout()
    private var newMetadataEntryButton: Button

    var selectedEntity: ProjectEntity? = null
    private var currentProjectEntities = ArrayList<ToggleButton>()
    private var currentActiveMetadata = ArrayList<TextField>()
    private val currentActiveMetadataRemoveButtons = ArrayList<Button>()

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

        val panelHeight = (window.size.y - TOOLS_PANEL_HEIGHT) / 4.0f

        imageSelectorPanel = guiManagerCreatePanel(layout)
        imageSelectorPanel.w = 300.0f
        imageSelectorPanel.h = panelHeight
        imageSelectorPanel.skin = editorSkin
        imageSelectorPanel.visible = false
        imageSelectorPanel.moveable = false
        imageSelectorPanel.resizable = false

        entityTypeLayout.componentsPerRow = 2
        entityTypeLayout.componentHeight = 20.0f
        entityTypePanel = guiManagerCreatePanel(entityTypeLayout)
        entityTypePanel.w = 300.0f
        entityTypePanel.h = panelHeight
        entityTypePanel.skin = editorSkin
        entityTypePanel.visible = false
        entityTypePanel.moveable = false
        entityTypePanel.resizable = false

        entityPropertiesPanel = guiManagerCreatePanel(entityTypeLayout)
        entityPropertiesPanel.w = 300.0f
        entityPropertiesPanel.h = panelHeight
        entityPropertiesPanel.skin = editorSkin
        entityPropertiesPanel.visible = false
        entityPropertiesPanel.moveable = false
        entityPropertiesPanel.resizable = false

        entityWidthLabel = entityPropertiesPanel.createLabel("Width")
        entityWidthField = entityPropertiesPanel.createTextField("32")

        entityHeightLabel = entityPropertiesPanel.createLabel("Height")
        entityHeightField = entityPropertiesPanel.createTextField("32")

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

    fun entityWidth(): Float {
        return entityWidthField.string.toFloat()
    }

    fun entityHeight(): Float {
        return entityHeightField.string.toFloat()
    }

    fun update(projectScene: ProjectScene, selectedTilemapTexture: Texture2d) {
        entityTypePanel.x = window.size.x - entityTypePanel.w
        entityTypePanel.y = TOOLS_PANEL_HEIGHT

        entityPropertiesPanel.x = window.size.x - entityPropertiesPanel.w
        entityPropertiesPanel.y = entityTypePanel.y + entityTypePanel.h

        imageSelectorPanel.x = window.size.x - imageSelectorPanel.w
        imageSelectorPanel.y = entityPropertiesPanel.y + entityPropertiesPanel.h

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

        if (entityWidthField.textEdited) {
            if (entityWidthField.string.contains(Regex("[^0-9]"))) {
                entityWidthField.string = entityWidthField.string.replace(Regex("[^0-9]"), "")
            }
        }

        if (entityHeightField.textEdited) {
            if (entityHeightField.string.contains(Regex("[^0-9]"))) {
                entityHeightField.string = entityHeightField.string.replace(Regex("[^0-9]"), "")
            }
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
            metadataPanel.removeComponent(newMetadataEntryButton)
            val metadata = SceneMetadata("name", "value")
            selectedEntity!!.metadata.add(metadata)

            val nameField = metadataPanel.createTextField(metadata.name)
            val valueField = metadataPanel.createTextField(metadata.value)
            val removeButton = metadataPanel.createButton("-")
            currentActiveMetadata.add(nameField)
            currentActiveMetadata.add(valueField)
            currentActiveMetadataRemoveButtons.add(removeButton)
            newMetadataEntryButton = metadataPanel.createButton("+")
        }

        var buttonIndex = -1
        for ((index, button) in currentActiveMetadataRemoveButtons.withIndex()) {
            if (button.clicked) {
                selectedEntity!!.metadata.removeAt(index)
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

            if (selectedEntity == entity.value) {
                button.checked = true
            }
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
        entityPropertiesPanel.visible = false
    }

    override fun show() {
        if (selectedEntity == null && currentProjectScene.entities.isNotEmpty()) {
            selectedEntity = currentProjectScene.entities.values.first()
        }

        updateImageSelector = true
        imageSelectorPanel.visible = true
        entityTypePanel.visible = true
        metadataPanel.visible = true
        entityPropertiesPanel.visible = true
    }
}
