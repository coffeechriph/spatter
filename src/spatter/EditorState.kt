package spatter

import rain.State
import rain.StateManager
import rain.api.Input
import rain.api.Window
import rain.api.gfx.ResourceFactory
import rain.api.scene.Scene
import spatter.entity.EditEntityDialog
import spatter.entity.NewEntityDialog
import spatter.tilemap.TilemapEditor
import spatter.tilemap.TilemapPropertiesPanel

class EditorState(private val window: Window, stateManager: StateManager): State(stateManager) {
    private lateinit var toolsPanel: ToolsPanel
    private lateinit var resourcePanel: ResourcePanel
    private lateinit var materialPropertiesPanel: MaterialPropertiesPanel
    private lateinit var tilemapPropertiesPanel: TilemapPropertiesPanel
    private lateinit var tilemapEditor: TilemapEditor
    private lateinit var newEntityDialog: NewEntityDialog
    private lateinit var editEntityDialog: EditEntityDialog

    override fun init(resourceFactory: ResourceFactory, scene: Scene) {
        setupEditorStyle()

        tilemapPropertiesPanel = TilemapPropertiesPanel(window)
        materialPropertiesPanel = MaterialPropertiesPanel(window)
        newEntityDialog = NewEntityDialog(window)
        editEntityDialog = EditEntityDialog(window)
        toolsPanel = ToolsPanel(window, materialPropertiesPanel, tilemapPropertiesPanel, newEntityDialog)
        tilemapEditor = TilemapEditor(resourceFactory, scene)
        resourcePanel = ResourcePanel(window, editEntityDialog)
    }

    override fun update(resourceFactory: ResourceFactory, scene: Scene, input: Input) {
        tilemapPropertiesPanel.update()
        toolsPanel.update()
        resourcePanel.update()
        materialPropertiesPanel.update()
        tilemapEditor.update(input)
        newEntityDialog.update(currentProjectScene, resourcePanel)
        editEntityDialog.update(currentProjectScene, resourcePanel)

        if (tilemapPropertiesPanel.created) {
            tilemapEditor.createTilemap(
                tilemapPropertiesPanel.numTileX,
                tilemapPropertiesPanel.numTileY,
                tilemapPropertiesPanel.tileW.toFloat(),
                tilemapPropertiesPanel.tileH.toFloat())
        }
    }

}
