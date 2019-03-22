package spatter.project

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import spatter.tilemap.TilemapData

class ProjectScene @JsonCreator constructor(
    @JsonProperty("map")
    val mapData: MutableList<TilemapData>,
    @JsonProperty("entities")
    val entities: MutableMap<String, ProjectEntity>)

var currentProjectScene = ProjectScene(mutableListOf(), mutableMapOf())
