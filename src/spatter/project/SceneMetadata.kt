package spatter.project

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SceneMetadata constructor(
    @SerialName("name")
    var name: String,
    @SerialName("value")
    var value: String)
