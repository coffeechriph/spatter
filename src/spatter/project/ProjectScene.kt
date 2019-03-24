package spatter.project

import kotlinx.serialization.json.Json
import rain.api.scene.parse.*
import java.io.File

class ProjectScene constructor(
    val mapData: MutableList<TilemapData>,
    val entities: MutableMap<String, ProjectEntity>) {
    fun export(file: String) {
        val mapDefinition = ArrayList<MapDefinition>()
        val entityDefinition = HashMap<String, EntityDefinition>()

        for (data in mapData) {
            val layers = ArrayList<MapLayerDefinition>()
            for (layer in data.layers) {
                val tileGroups = ArrayList<MapLayerTileGroup>()
                for (group in layer.tileGroup) {
                    tileGroups.add(MapLayerTileGroup(group.imageX, group.imageY, group.tileIndicesIntoMap))
                }
                layers.add(MapLayerDefinition(tileGroups, layer.metadata))
            }
            mapDefinition.add(MapDefinition(data.tileNumX, data.tileNumY, data.tileWidth, data.tileHeight, layers))
        }

        for (entity in entities) {
            val instances = ArrayList<EntityDefinitionInstance>()
            for (instance in entity.value.instances) {
                instances.add(
                    EntityDefinitionInstance(
                        instance.posX,
                        instance.posY,
                        instance.posZ,
                        instance.imageX,
                        instance.imageY,
                        instance.width,
                        instance.height
                    )
                )
            }

            entityDefinition[entity.key] = EntityDefinition(entity.value.materialName, entity.value.metadata, instances)
        }

        val sceneDefinition = SceneDefinition(mapDefinition, entityDefinition)
        val json = Json.stringify(SceneDefinition.serializer(), sceneDefinition)

        // TODO: We want to specify actual project directories
        if (!File("projects").exists()) {
            File("projects").mkdir()
            File("projects/project1").mkdir()
            File("projects/project1/scenes").mkdir()
        }
        File("projects/project1/scenes/$file.json").writeText(json)
    }
}

var currentProjectScene = ProjectScene(mutableListOf(), mutableMapOf())
