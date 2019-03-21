package spatter

import rain.api.Window
import rain.api.gui.v2.*
import spatter.entity.EntityEditorProperties
import spatter.entity.NewEntityDialog
import spatter.tilemap.TilemapEditorProperties
import spatter.tilemap.TilemapPropertiesPanel
import java.io.File

const val TOOLS_PANEL_HEIGHT = 34.0f

class ToolsPanel(private val window: Window,
                 private val materialProperties: MaterialPropertiesPanel,
                 private val tilemapProperties: TilemapPropertiesPanel,
                 private val tilemapEditorProperties: TilemapEditorProperties,
                 private val newEntityDialog: NewEntityDialog,
                 private val editEntityProperties: EntityEditorProperties
) {
    private val panelLayout: GridLayout
    private val panel: Panel
    private val createMaterialButton: Button
    private val createTilemapButton: Button
    private val createEntityButton: Button
    private val saveSceneButton: Button

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
        saveSceneButton = panel.createButton("Save Scene")
    }

    fun update() {
        panel.w = window.size.x.toFloat()

        if (createTilemapButton.clicked) {
            tilemapProperties.show()
            materialProperties.visible = false
            newEntityDialog.visible = false
            editEntityProperties.visible = false
            tilemapEditorProperties.visible = false
        }

        if (createMaterialButton.clicked) {
            materialProperties.show()
            tilemapProperties.visible = false
            newEntityDialog.visible = false
            editEntityProperties.visible = false
            tilemapEditorProperties.visible = false
        }

        if (createEntityButton.clicked) {
            newEntityDialog.show()
            materialProperties.visible = false
            tilemapProperties.visible = false
            editEntityProperties.visible = false
            tilemapEditorProperties.visible = false
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
