package spatter.project

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ProjectEntityInstance @JsonCreator constructor(
    @JsonProperty("pos_x")
    var posX: Float,
    @JsonProperty("pos_y")
    var posY: Float,
    @JsonProperty("pos_z")
    var posZ: Float,
    @JsonProperty("image_x")
    var imageX: Int,
    @JsonProperty("image_y")
    var imageY: Int,
    var width: Float,
    var height: Float
)

data class ProjectEntity @JsonCreator constructor(
    var materialName: String,
    val metadata: MutableList<SceneMetadata>,
    val instances: MutableList<ProjectEntityInstance>)
