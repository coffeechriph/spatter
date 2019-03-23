package spatter

import kotlinx.serialization.json.Json
import org.joml.Vector2i
import rain.State
import rain.StateManager
import rain.api.Input
import rain.api.Window
import rain.api.entity.Entity
import rain.api.gfx.Mesh
import rain.api.gfx.ResourceFactory
import rain.api.scene.*
import spatter.entity.EntityEditor
import spatter.entity.EntityEditorDialog
import spatter.entity.NewEntityDialog
import spatter.project.ProjectScene
import spatter.project.currentProjectScene
import spatter.tilemap.TilemapEditor
import spatter.tilemap.NewTilemapDialog
import spatter.tilemap.TilemapEditorDialog
import java.io.File
import java.nio.charset.StandardCharsets

class EditorState(private val window: Window, stateManager: StateManager): State(stateManager) {
    private lateinit var toolsPanel: ToolsPanel
    private lateinit var tilemapEditor: TilemapEditor
    private lateinit var entityEditor: EntityEditor

    private lateinit var tilemapEditorDialog: TilemapEditorDialog
    private lateinit var materialPropertiesDialog: MaterialPropertiesDialog
    private lateinit var newTilemapDialog: NewTilemapDialog
    private lateinit var newEntityDialog: NewEntityDialog
    private lateinit var entityEditorDialog: EntityEditorDialog
    private lateinit var loadSceneDialog: FileChooseDialog
    private lateinit var exportSceneDialog: ExportSceneDialog

    private lateinit var activeCamera: Camera
    private var doCameraMove = false
    private var cameraMoveStartPoint = Vector2i(0,0)

    override fun init(resourceFactory: ResourceFactory, scene: Scene) {
        setupEditorStyle()

        newTilemapDialog = NewTilemapDialog(window)
        tilemapEditorDialog = TilemapEditorDialog(window)
        materialPropertiesDialog = MaterialPropertiesDialog(window)
        newEntityDialog = NewEntityDialog(window)
        entityEditorDialog = EntityEditorDialog(window)
        loadSceneDialog = FileChooseDialog(window)
        exportSceneDialog = ExportSceneDialog(window)

        entityEditor = EntityEditor(resourceFactory, scene, entityEditorDialog)
        tilemapEditor = TilemapEditor(resourceFactory, scene, tilemapEditorDialog)
        toolsPanel = ToolsPanel(window, materialPropertiesDialog, newTilemapDialog, tilemapEditorDialog,
            newEntityDialog, entityEditorDialog, loadSceneDialog, exportSceneDialog)

        activeCamera = scene.activeCamera
    }

    override fun update(resourceFactory: ResourceFactory, scene: Scene, input: Input) {
        toolsPanel.update()
        newTilemapDialog.update()
        materialPropertiesDialog.update()
        newEntityDialog.update(currentProjectScene)
        exportSceneDialog.update()

        tilemapEditor.update(input, scene.activeCamera)
        entityEditor.update(input, scene.activeCamera)

        if (newTilemapDialog.created) {
            tilemapEditor.createTilemap(
                newTilemapDialog.numTileX,
                newTilemapDialog.numTileY,
                newTilemapDialog.tileW.toFloat(),
                newTilemapDialog.tileH.toFloat())
        }

        if (!newTilemapDialog.shown() && !tilemapEditorDialog.shown() && !materialPropertiesDialog.shown()
            && !newEntityDialog.shown() && !entityEditorDialog.shown()) {
            moveCamera(input)
        }

        if (loadSceneDialog.hasSelected) {
            val content = File("./projects/project1/scenes/" + loadSceneDialog.lastSelectedItem!!.string).readText(StandardCharsets.UTF_8)
            currentProjectScene = Json.parse(ProjectScene.serializer(), content)
            for (data in currentProjectScene.mapData) {
                var depth = 0.0f
                for (layer in data.layers) {
                    val tilemap = scene.createTilemap(tilemapEditor.tilemapMaterial, data.tileNumX, data.tileNumY, data.tileWidth, data.tileHeight)
                    for (group in layer.tileGroup) {
                        for (index in group.tileIndicesIntoMap) {
                            tilemap.setTile(index%data.tileNumX, index/data.tileNumY, group.imageX, group.imageY, 1.0f, 1.0f, 1.0f, 1.0f)
                        }
                    }

                    // TODO: This can cause trouble if the layers don't appear in order
                    // We should save the depth of the layer as well
                    tilemap.transform.z = depth
                    layer.tilemapRef = tilemap

                    depth += 1.0f
                }

                if (data.layers.size > 0) {
                    data.activeLayer = data.layers[0]
                }
            }
        }

        for (projectEntity in currentProjectScene.entities) {
            for (instance in projectEntity.value.instances) {
                val entity = Entity()
                entityEditor.entitySystem.newEntity(entity)
                    .attachRenderComponent(entityEditor.spriteMaterial, Mesh(entityEditor.entityQuad, null))
                    .build()
                entity.transform.x = instance.posX
                entity.transform.y = instance.posY
                entity.transform.z = instance.posZ
                entity.transform.sx = instance.width
                entity.transform.sy = instance.height
                entity.getRenderComponents()[0].textureTileOffset.x = instance.imageX
                entity.getRenderComponents()[0].textureTileOffset.y = instance.imageY
            }
        }

        loadSceneDialog.update()
    }

    fun moveCamera(input: Input) {
        if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.DOWN) {
            if (!doCameraMove) {
                doCameraMove = true
                cameraMoveStartPoint.x = input.mousePosition.x
                cameraMoveStartPoint.y = input.mousePosition.y
            }
            else  {
                activeCamera.x += (input.mousePosition.x - cameraMoveStartPoint.x)
                activeCamera.y += (input.mousePosition.y - cameraMoveStartPoint.y)
                cameraMoveStartPoint.x = input.mousePosition.x
                cameraMoveStartPoint.y = input.mousePosition.y
            }
        }
        else if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.RELEASED) {
            doCameraMove = false
        }
    }
}
