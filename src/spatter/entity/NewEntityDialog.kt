package spatter.entity

import rain.api.Window
import rain.api.gui.v2.*
import spatter.ProjectScene
import spatter.ResourcePanel
import spatter.editorSkin

class NewEntityDialog(private val window: Window) {
    private val panelLayout = FillRowLayout()
    private val panel: Panel
    private val entityNameLabel: Label
    private val entityNameField: TextField
    private val doneButton: Button

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

        entityNameLabel = panel.createLabel("Name")
        entityNameField = panel.createTextField("entity")
        doneButton = panel.createButton("Done")
    }

    fun update(projectScene: ProjectScene, resourcePanel: ResourcePanel) {
        if (doneButton.clicked) {
            if (projectScene.entities.containsKey(entityNameField.string)) {
                // TODO: Prompt error - every entity in each scene needs to be unique
            }
            else {
                panel.visible = false
                val entity = ProjectEntity("materialName", mutableListOf(), mutableListOf())
                projectScene.entities[entityNameField.string] = entity
                resourcePanel.recreateEntitiesList(projectScene)
            }
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
