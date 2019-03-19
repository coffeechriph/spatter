package spatter

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import spatter.tilemap.TilemapData

class ProjectScene @JsonCreator constructor(
    @JsonProperty("map")
    val mapData: MutableList<TilemapData>)

val currentProjectScene = ProjectScene(mutableListOf())
