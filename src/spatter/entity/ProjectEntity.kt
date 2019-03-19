package spatter.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import spatter.Metadata

data class ProjectEntityInstance @JsonCreator constructor(
    @JsonProperty("pos_x")
    var posX: Float,
    @JsonProperty("pos_y")
    var posY: Float,
    @JsonProperty("pos_z")
    var posZ: Float,
    var width: Float,
    var height: Float
)

data class ProjectEntity @JsonCreator constructor(
    val metadata: MutableList<Metadata>,
    val instances: MutableList<ProjectEntityInstance>)
