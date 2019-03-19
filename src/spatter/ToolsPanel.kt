package spatter

import rain.api.Window
import rain.api.gui.v2.*
import rain.api.scene.TileGfx
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ToolsPanel(private val window: Window,
                 private val materialProperties: MaterialPropertiesPanel,
                 private val tilemapProperties: TilemapPropertiesPanel) {
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
        panel.h = 34.0f
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
        }

        if (createMaterialButton.clicked) {
            materialProperties.show()
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
