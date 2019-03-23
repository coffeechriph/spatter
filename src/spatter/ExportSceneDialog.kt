package spatter

import kotlinx.serialization.json.Json
import rain.api.Window
import rain.api.gui.v2.*
import spatter.project.ProjectScene
import spatter.project.currentProjectScene
import java.io.File

class ExportSceneDialog(window: Window): EditorDialog {
    private val panel: Panel
    private val fileNameField: TextField
    private val exportButton: Button

    init {
        panel = guiManagerCreatePanel(FillRowLayout())
        panel.w = 200.0f
        panel.h = 100.0f
        panel.x = window.size.x / 2.0f - 100.0f
        panel.y = window.size.y - 100.0f
        panel.moveable = false
        panel.resizable = false
        panel.visible = false

        fileNameField = panel.createTextField("EnterFileName")
        exportButton = panel.createButton("Export")
    }

    fun update() {
        if (exportButton.clicked) {
            val json = Json.stringify(ProjectScene.serializer(), currentProjectScene)

            // TODO: We want to specify actual project directories
            if (!File("projects").exists()) {
                File("projects").mkdir()
                File("projects/project1").mkdir()
                File("projects/project1/scenes").mkdir()
            }
            File("projects/project1/scenes/${fileNameField.string}.json").writeText(json)
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