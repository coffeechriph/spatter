package spatter.project

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ProjectScene constructor(
    @SerialName("map")
    val mapData: MutableList<TilemapData>,
    @SerialName("entities")
    val entities: MutableMap<String, ProjectEntity>)

var currentProjectScene = ProjectScene(mutableListOf(), mutableMapOf())
