package spatter.entity

import rain.api.Window
import rain.api.gui.v2.*
import spatter.EditorDialog
import spatter.project.ProjectScene
import spatter.editorSkin
import spatter.project.ProjectEntity

class NewEntityDialog(private val window: Window): EditorDialog {
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

    fun update(projectScene: ProjectScene) {
        if (doneButton.clicked) {
            if (projectScene.entities.containsKey(entityNameField.string)) {
                // TODO: Prompt error - every entity in each scene needs to be unique
            }
            else {
                panel.visible = false
                val entity = ProjectEntity("materialName", mutableListOf(), mutableListOf())
                projectScene.entities[entityNameField.string] = entity
            }
        }
    }

    override fun shown(): Boolean {
        return panel.visible
    }

    override fun hide() {
        panel.visible = false
    }

    override fun show() {
        if (!panel.visible) {
            panel.visible = true
            panel.x = window.size.x.toFloat() - panel.w
            panel.h = window.size.y.toFloat()
            panel.y = 34.0f
        }
    }
}
