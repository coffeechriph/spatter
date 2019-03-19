package spatter

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class ProjectScene @JsonCreator constructor(
    @JsonProperty("tilemap")
    val tilemapData: MutableList<TilemapData>)

val currentProjectScene = ProjectScene(mutableListOf())
