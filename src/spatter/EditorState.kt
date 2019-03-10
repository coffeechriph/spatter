package spatter

import rain.State
import rain.StateManager
import rain.api.Input
import rain.api.Window
import rain.api.gfx.ResourceFactory
import rain.api.scene.Scene
import rain.api.scene.Tilemap

class EditorState(private val window: Window, stateManager: StateManager): State(stateManager) {
    private lateinit var toolsPanel: ToolsPanel
    private lateinit var resourcePanel: ResourcePanel
    private lateinit var materialPropertiesPanel: MaterialPropertiesPanel
    private lateinit var tilemapPropertiesPanel: TilemapPropertiesPanel

    private val tilemaps = ArrayList<Tilemap>()
    override fun init(resourceFactory: ResourceFactory, scene: Scene, input: Input) {
        setupEditorStyle()

        tilemapPropertiesPanel = TilemapPropertiesPanel(window)
        materialPropertiesPanel = MaterialPropertiesPanel(window)
        toolsPanel = ToolsPanel(window, materialPropertiesPanel, tilemapPropertiesPanel)
        resourcePanel = ResourcePanel(window)
    }

    override fun update(resourceFactory: ResourceFactory, scene: Scene, input: Input, deltaTime: Float) {
        tilemapPropertiesPanel.update()
        toolsPanel.update()
        resourcePanel.update()
        materialPropertiesPanel.update()

        if (tilemapPropertiesPanel.created) {
        }
    }

}