package spatter

import rain.api.WindowContext
import rain.api.gui.v2.*
import spatter.project.currentProjectScene

class ExportSceneDialog(window: WindowContext): EditorDialog {
    private val panel: Window = guiManagerCreateWindow(FillRowLayout(), "Export Scene")
    private val fileNameField: TextField
    private val exportButton: Button

    init {
        panel.w = 200.0f
        panel.h = 100.0f
        panel.x = window.framebufferSize.x / 2.0f - 100.0f
        panel.y = window.framebufferSize.y - 100.0f
        panel.moveable = false
        panel.resizable = false
        panel.visible = false
        panel.skin = editorSkin

        fileNameField = panel.createTextField("EnterFileName")
        exportButton = panel.createButton("Export")
    }

    fun update() {
        if (exportButton.clicked) {
            currentProjectScene.export(fileNameField.string)
            hide()
        }
    }

    override fun show() {
        panel.visible = true
    }

    override fun hide() {
        panel.visible = false
    }

    override fun shown(): Boolean {
        return panel.visible
    }

}
