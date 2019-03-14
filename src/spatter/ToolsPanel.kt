package spatter

import rain.api.Window
import rain.api.gui.v2.*

class ToolsPanel(private val window: Window,
                 private val materialProperties: MaterialPropertiesPanel,
                 private val tilemapProperties: TilemapPropertiesPanel) {
    private val panelLayout: GridLayout
    private val panel: Panel
    private val createMaterialButton: Button
    private val createTilemapButton: Button
    private val createEntityButton: Button

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
    }

    fun update() {
        panel.w = window.size.x.toFloat()

        if (createTilemapButton.clicked) {
            tilemapProperties.show()
        }

        if (createMaterialButton.clicked) {
            materialProperties.show()
        }
    }
}
