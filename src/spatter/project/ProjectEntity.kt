package spatter.project

import rain.api.entity.Entity
import rain.api.scene.parse.SceneMetadata

data class ProjectEntityInstance constructor(
    var posX: Float,
    var posY: Float,
    var posZ: Float,
    var imageX: Int,
    var imageY: Int,
    var width: Float,
    var height: Float,
    var entity: Entity
)

data class ProjectEntity constructor(
    var materialName: String,
    val metadata: MutableList<SceneMetadata>,
    val instances: MutableList<ProjectEntityInstance>)
