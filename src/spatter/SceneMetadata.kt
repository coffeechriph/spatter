package spatter

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class SceneMetadata @JsonCreator constructor(
    @JsonProperty("name")
    var name: String,
    @JsonProperty("value")
    var value: String)