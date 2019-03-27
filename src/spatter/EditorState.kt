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
import rain.api.scene.Camera
import rain.api.scene.Scene
import rain.api.scene.parse.JsonSceneLoader
import rain.api.scene.parse.SceneDefinition
import rain.api.scene.parse.SceneMetadata
import spatter.entity.EntityEditor
import spatter.entity.EntityEditorDialog
import spatter.entity.NewEntityDialog
import spatter.project.*
import spatter.tilemap.NewTilemapDialog
import spatter.tilemap.TilemapEditor
import spatter.tilemap.TilemapEditorDialog
import java.io.File
import java.nio.charset.StandardCharsets

/*
    TODO: Must fix
    1) Delete metadata
    2) Close every popup without performing action
    3) Delete tilemaps
    4) Delete layers
    5) Delete entities
    6) Fit entities to current hovered tilemap (unless specified to be free-positioned)
    7) Ghost entities without a sprite attached
 */
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
            val loadedScene = JsonSceneLoader().load("./projects/project1/scenes/" + loadSceneDialog.lastSelectedItem!!.string)
            val sceneMap = ArrayList<TilemapData>()
            for (data in loadedScene.map) {
                var depth = 0.0f
                val tileLayers = ArrayList<TilemapLayer>()
                for (layer in data.layers) {
                    val tileGroupList = ArrayList<TileGroup>()
                    val tilemap = scene.createTilemap(tilemapEditor.tilemapMaterial, data.tileNumX, data.tileNumY, data.tileWidth, data.tileHeight)
                    for (group in layer.mapLayerTileGroup) {
                        val tileGroupIndices = HashSet<Int>()
                        for (index in group.tileIndicesIntoMap) {
                            tilemap.setTile(index%data.tileNumX, index/data.tileNumX, group.imageX, group.imageY, 1.0f, 1.0f, 1.0f, 1.0f)
                            tileGroupIndices.add(index)
                        }
                        tileGroupList.add(TileGroup(group.imageX, group.imageY, tileGroupIndices))
                    }

                    // TODO: This can cause trouble if the layers don't appear in order
                    // We should save the depth of the layer as well
                    tilemap.transform.z = depth
                    tileLayers.add(TilemapLayer(tileGroupList, layer.metadata, tilemap))

                    depth += 1.0f
                }

                sceneMap.add(TilemapData(data.tileNumX, data.tileNumY, data.tileWidth, data.tileHeight, tileLayers, tileLayers[0]))
            }

            val entities = HashMap<String, ProjectEntity>()
            for (entity in loadedScene.entities) {
                val instances = ArrayList<ProjectEntityInstance>()
                for (instance in entity.value.definitionInstances) {
                    val entityInstance = Entity()
                    entityEditor.entitySystem.newEntity(entityInstance)
                        .attachRenderComponent(entityEditor.spriteMaterial, entityEditor.entityMesh)
                        .build()

                    entityInstance.transform.x = instance.posX
                    entityInstance.transform.y = instance.posY
                    entityInstance.transform.z = instance.posZ
                    entityInstance.transform.sx = instance.width
                    entityInstance.transform.sy = instance.height
                    entityInstance.getRenderComponents()[0].textureTileOffset.x = instance.imageX
                    entityInstance.getRenderComponents()[0].textureTileOffset.y = instance.imageY

                    val newE = ProjectEntityInstance(instance.posX, instance.posY, instance.posZ, instance.imageX, instance.imageY, instance.width, instance.height, entityInstance)
                    instances.add(newE)
                }

                entities[entity.key] = ProjectEntity(entity.value.material, entity.value.metadata, instances)
            }
            currentProjectScene = ProjectScene(sceneMap, entities)
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
