package spatter

import rain.State
import rain.StateManager
import rain.api.Input
import rain.api.Window
import rain.api.gfx.ResourceFactory
import rain.api.scene.Scene
import spatter.entity.EntityEditor
import spatter.entity.NewEntityDialog
import spatter.tilemap.TilemapEditor
import spatter.tilemap.TilemapPropertiesPanel

class EditorState(private val window: Window, stateManager: StateManager): State(stateManager) {
    private lateinit var toolsPanel: ToolsPanel
    private lateinit var resourcePanel: ResourcePanel
    private lateinit var materialPropertiesPanel: MaterialPropertiesPanel
    private lateinit var tilemapPropertiesPanel: TilemapPropertiesPanel
    private lateinit var tilemapEditor: TilemapEditor
    private lateinit var entityEditor: EntityEditor
    private lateinit var newEntityDialog: NewEntityDialog

    override fun init(resourceFactory: ResourceFactory, scene: Scene) {
        setupEditorStyle()

        tilemapPropertiesPanel = TilemapPropertiesPanel(window)
        materialPropertiesPanel = MaterialPropertiesPanel(window)
        newEntityDialog = NewEntityDialog(window)
        toolsPanel = ToolsPanel(window, materialPropertiesPanel, tilemapPropertiesPanel, newEntityDialog)
        tilemapEditor = TilemapEditor(resourceFactory, scene)
        entityEditor = EntityEditor(resourceFactory, scene)
        resourcePanel = ResourcePanel(window)
    }

    override fun update(resourceFactory: ResourceFactory, scene: Scene, input: Input) {
        tilemapPropertiesPanel.update()
        toolsPanel.update()
        resourcePanel.update()
        materialPropertiesPanel.update()
        newEntityDialog.update(currentProjectScene, resourcePanel)

        tilemapEditor.update(input)
        entityEditor.update(input)

        if (input.keyState(Input.Key.KEY_T) == Input.InputState.PRESSED) {
            entityEditor.entityEditorProperties.visible = false
            tilemapEditor.tilemapEditorProperties.show(tilemapEditor.tilemapTexture)
        }
        else if (input.keyState(Input.Key.KEY_E) == Input.InputState.PRESSED) {
            tilemapEditor.tilemapEditorProperties.visible = false
            entityEditor.entityEditorProperties.show(currentProjectScene, entityEditor.spriteTexture)
        }

        if (tilemapPropertiesPanel.created) {
            tilemapEditor.createTilemap(
                tilemapPropertiesPanel.numTileX,
                tilemapPropertiesPanel.numTileY,
                tilemapPropertiesPanel.tileW.toFloat(),
                tilemapPropertiesPanel.tileH.toFloat())
        }
    }

}
