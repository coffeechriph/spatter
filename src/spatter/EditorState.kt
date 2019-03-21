package spatter

import rain.State
import rain.StateManager
import rain.api.Input
import rain.api.Window
import rain.api.gfx.ResourceFactory
import rain.api.scene.Scene
import spatter.entity.EntityEditor
import spatter.entity.EntityEditorDialog
import spatter.entity.NewEntityDialog
import spatter.tilemap.TilemapEditor
import spatter.tilemap.NewTilemapDialog
import spatter.tilemap.TilemapEditorDialog

class EditorState(private val window: Window, stateManager: StateManager): State(stateManager) {
    private lateinit var toolsPanel: ToolsPanel
    private lateinit var tilemapEditor: TilemapEditor
    private lateinit var entityEditor: EntityEditor

    private lateinit var tilemapEditorDialog: TilemapEditorDialog
    private lateinit var materialPropertiesDialog: MaterialPropertiesDialog
    private lateinit var newTilemapDialog: NewTilemapDialog
    private lateinit var newEntityDialog: NewEntityDialog
    private lateinit var entityEditorDialog: EntityEditorDialog

    override fun init(resourceFactory: ResourceFactory, scene: Scene) {
        setupEditorStyle()

        newTilemapDialog = NewTilemapDialog(window)
        tilemapEditorDialog = TilemapEditorDialog(window)
        materialPropertiesDialog = MaterialPropertiesDialog(window)
        newEntityDialog = NewEntityDialog(window)
        entityEditorDialog = EntityEditorDialog(window)

        entityEditor = EntityEditor(resourceFactory, scene, entityEditorDialog)
        tilemapEditor = TilemapEditor(resourceFactory, scene, tilemapEditorDialog)
        toolsPanel = ToolsPanel(window, materialPropertiesDialog, newTilemapDialog, tilemapEditorDialog,
            newEntityDialog, entityEditorDialog)
    }

    override fun update(resourceFactory: ResourceFactory, scene: Scene, input: Input) {
        toolsPanel.update()
        newTilemapDialog.update()
        materialPropertiesDialog.update()
        newEntityDialog.update(currentProjectScene)

        tilemapEditor.update(input)
        entityEditor.update(input)

        if (newTilemapDialog.created) {
            tilemapEditor.createTilemap(
                newTilemapDialog.numTileX,
                newTilemapDialog.numTileY,
                newTilemapDialog.tileW.toFloat(),
                newTilemapDialog.tileH.toFloat())
        }
    }

}
