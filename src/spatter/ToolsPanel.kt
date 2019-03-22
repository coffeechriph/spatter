package spatter

import rain.api.Window
import rain.api.gui.v2.*
import spatter.entity.EntityEditorDialog
import spatter.entity.NewEntityDialog
import spatter.project.currentProjectScene
import spatter.tilemap.TilemapEditorDialog
import spatter.tilemap.NewTilemapDialog
import java.io.File

const val TOOLS_PANEL_HEIGHT = 34.0f

class ToolsPanel(private val window: Window,
                 private val materialProperties: MaterialPropertiesDialog,
                 private val newTilemapDialog: NewTilemapDialog,
                 private val tilemapEditorDialog: TilemapEditorDialog,
                 private val newEntityDialog: NewEntityDialog,
                 private val editEntityDialog: EntityEditorDialog,
                 private val loadSceneDialog: FileChooseDialog
) {
    private val panelLayout: GridLayout
    private val panel: Panel
    private val createMaterialButton: Button
    private val createTilemapButton: Button
    private val createEntityButton: Button
    private val editTilemapButton: Button
    private val editEntityButton: Button
    private val saveSceneButton: Button
    private val loadSceneButton: Button

    init {
        panelLayout = GridLayout()
        panelLayout.gridW = 100.0f
        panelLayout.gridH = 24.0f

        panel = guiManagerCreatePanel(panelLayout)
        panel.skin = editorSkin
        panel.x = 0.0f
        panel.y = 0.0f
        panel.w = window.size.x.toFloat()
        panel.h = TOOLS_PANEL_HEIGHT
        panel.moveable = false
        panel.resizable = false

        createMaterialButton = panel.createButton("New Material")
        createTilemapButton = panel.createButton("New Tilemap")
        createEntityButton = panel.createButton("New Entity")
        editTilemapButton = panel.createButton("Edit Tilemap")
        editEntityButton = panel.createButton("Edit Entity")
        saveSceneButton = panel.createButton("Save Scene")
        loadSceneButton = panel.createButton("Load Scene")
    }

    fun update() {
        panel.w = window.size.x.toFloat()

        if (createTilemapButton.clicked) {
            newTilemapDialog.show()
            materialProperties.hide()
            newEntityDialog.hide()
            tilemapEditorDialog.hide()
            editEntityDialog.hide()
        }

        if (createMaterialButton.clicked) {
            newTilemapDialog.hide()
            materialProperties.show()
            newEntityDialog.hide()
            tilemapEditorDialog.hide()
            editEntityDialog.hide()
        }

        if (createEntityButton.clicked) {
            newTilemapDialog.hide()
            materialProperties.hide()
            newEntityDialog.show()
            tilemapEditorDialog.hide()
            editEntityDialog.hide()
        }

        if (editTilemapButton.clicked) {
            newTilemapDialog.hide()
            materialProperties.hide()
            newEntityDialog.hide()
            tilemapEditorDialog.show()
            editEntityDialog.hide()
        }

        if (editEntityButton.clicked) {
            newTilemapDialog.hide()
            materialProperties.hide()
            newEntityDialog.hide()
            tilemapEditorDialog.hide()
            editEntityDialog.show()
        }

        if (loadSceneButton.clicked) {
            newTilemapDialog.hide()
            materialProperties.hide()
            newEntityDialog.hide()
            tilemapEditorDialog.hide()
            editEntityDialog.hide()
            loadSceneDialog.show("./projects/project1/scenes")
        }

        if (saveSceneButton.clicked) {
            val json = jsonSceneWriter.writeValueAsString(currentProjectScene)

            // TODO: We want to specify actual project directories
            if (!File("projects").exists()) {
                File("projects").mkdir()
                File("projects/project1").mkdir()
                File("projects/project1/scenes").mkdir()
            }
            File("projects/project1/scenes/scene.json").writeText(json)
        }
    }
}
