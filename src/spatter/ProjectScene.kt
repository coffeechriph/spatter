package spatter

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class ProjectScene @JsonCreator constructor(
    @JsonProperty("map")
    val mapData: MutableList<TilemapData>)

val currentProjectScene = ProjectScene(mutableListOf())
