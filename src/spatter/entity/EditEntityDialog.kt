package spatter.entity

import rain.api.Window
import rain.api.gui.v2.*
import rain.assertion
import spatter.Metadata
import spatter.ProjectScene
import spatter.ResourcePanel
import spatter.editorSkin

class EditEntityDialog(private val window: Window) {
    private val panelLayout = FillRowLayout()
    private val panel: Panel
    private val entityNameLabel: Label
    private val entityNameValueLabel: Label
    private val newMetadataEntryButton: Button
    private val metadataList = ArrayList<Metadata>()
    private val metadataTextFields = ArrayList<TextField>()
    private val doneButton: Button

    private var selectedItem: ListItem? = null

    init {
        panelLayout.componentsPerRow = 2
        panelLayout.componentHeight = 30.0f

        panel = guiManagerCreatePanel(panelLayout)
        panel.w = 200.0f
        panel.x = window.size.x.toFloat() - panel.w
        panel.h = window.size.y.toFloat()
        panel.skin = editorSkin
        panel.visible = false
        panel.moveable = false
        panel.resizable = false

        entityNameLabel = panel.createLabel("Name:")
        entityNameValueLabel = panel.createLabel("entity")
        doneButton = panel.createButton("Done")

        newMetadataEntryButton = panel.createButton("New Metadata")
    }

    fun update(projectScene: ProjectScene, resourcePanel: ResourcePanel) {
        if (resourcePanel.getSelectedEntityItem() != null && selectedItem != resourcePanel.getSelectedEntityItem()) {
            entityNameValueLabel.string = resourcePanel.getSelectedEntityItem()!!.string
            selectedItem = resourcePanel.getSelectedEntityItem()

            for (field in metadataTextFields) {
                panel.removeComponent(field)
            }
            metadataTextFields.clear()
            metadataList.clear()

            val entity = projectScene.entities[selectedItem!!.string]
            if (entity == null) {
                assertion("Error entity ${selectedItem!!.string} does not exist in project!")
            }

            for (data in entity.metadata) {
                val nameField = panel.createTextField(data.name)
                val valueField = panel.createTextField(data.value)
                metadataTextFields.add(nameField)
                metadataTextFields.add(valueField)
                metadataList.add(data)
            }
        }

        if (newMetadataEntryButton.clicked) {
            val entity = projectScene.entities[selectedItem!!.string]
            if (entity == null) {
                assertion("Error entity ${selectedItem!!.string} does not exist in project!")
            }

            val nameField = panel.createTextField("name")
            val valueField = panel.createTextField("value")
            metadataTextFields.add(nameField)
            metadataTextFields.add(valueField)

            val metadata = Metadata("name", "value")
            metadataList.add(metadata)
            entity.metadata.add(metadata)
        }

        var index = 0
        for (field in metadataTextFields) {
            if (field.textEdited) {
                if (index % 2 == 0) {
                    metadataList[index/2].name = field.string
                }
                else {
                    metadataList[index/2].value = field.string
                }
            }

            index += 1
        }

        if (doneButton.clicked) {
            panel.visible = false
        }
    }

    fun show() {
        if (!panel.visible) {
            panel.visible = true
            panel.x = window.size.x.toFloat() - panel.w
            panel.h = window.size.y.toFloat()
            panel.y = 34.0f
        }
    }
}
