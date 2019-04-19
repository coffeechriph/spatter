package spatter

import rain.api.WindowContext
import rain.api.gui.v2.Button
import rain.api.gui.v2.GridLayout
import rain.api.gui.v2.Panel
import rain.api.gui.v2.guiManagerCreatePanel
import spatter.entity.EntityEditorDialog
import spatter.entity.NewEntityDialog
import spatter.tilemap.NewTilemapDialog
import spatter.tilemap.TilemapEditorDialog

const val TOOLS_PANEL_HEIGHT = 34.0f

class ToolsPanel(private val window: WindowContext,
                 private val materialProperties: MaterialPropertiesDialog,
                 private val newTilemapDialog: NewTilemapDialog,
                 private val tilemapEditorDialog: TilemapEditorDialog,
                 private val newEntityDialog: NewEntityDialog,
                 private val editEntityDialog: EntityEditorDialog,
                 private val loadSceneDialog: FileChooseDialog,
                 private val exportSceneDialog: ExportSceneDialog
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
        panelLayout.gridH = 34.0f

        panel = guiManagerCreatePanel(panelLayout)
        panel.skin = editorSkin
        panel.x = 0.0f
        panel.y = 0.0f
        panel.w = window.framebufferSize.x.toFloat()
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
        panel.w = window.framebufferSize.x.toFloat()

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
            exportSceneDialog.show()
            newTilemapDialog.hide()
            materialProperties.hide()
            newEntityDialog.hide()
            tilemapEditorDialog.hide()
            editEntityDialog.hide()
        }
    }
}
