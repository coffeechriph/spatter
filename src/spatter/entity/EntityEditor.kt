package spatter.entity

import org.joml.Vector2f
import rain.api.Input
import rain.api.gfx.Material
import rain.api.gfx.ResourceFactory
import rain.api.gfx.Texture2d
import rain.api.gfx.TextureFilter
import rain.api.gui.v2.guiManagerSetMaterial
import rain.api.scene.Scene
import rain.api.scene.TileGfx
import rain.api.scene.Tilemap
import spatter.EditMode
import spatter.currentProjectScene
import java.util.*
import kotlin.collections.HashSet

class EntityEditor(private val resourceFactory: ResourceFactory, private val scene: Scene) {
    var entityEditorProperties: EntityEditorProperties
    var spriteTexture: Texture2d
    var editMode: EditMode = EditMode.MOVE
    private var spriteMaterial: Material
    private val guiMaterial: Material

    private var selectedEntity: ProjectEntity? = null
    private var beginMovePosition = false
    private var movePosition = false
    private var moveDiffStart = Vector2f(0.0f, 0.0f)

    init {
        spriteTexture = resourceFactory.buildTexture2d()
            .withName("spriteTexture")
            .fromImageFile("./data/textures/tiles.png")
            .withFilter(TextureFilter.NEAREST)
            .build()

        spriteTexture.setTiledTexture(16,16)
        spriteMaterial = resourceFactory.buildMaterial()
            .withName("spriteMaterial")
            .withVertexShader("./data/shaders/tilemap.vert.spv")
            .withFragmentShader("./data/shaders/tilemap.frag.spv")
            .withTexture(spriteTexture)
            .withBlendEnabled(false)
            .build()

        guiMaterial = resourceFactory.buildMaterial()
            .withName("spriteGuiMaterial")
            .withVertexShader("./data/shaders/guiv2.vert.spv")
            .withFragmentShader("./data/shaders/gui.frag.spv")
            .withTexture(spriteTexture)
            .withBlendEnabled(true)
            .build()
        guiManagerSetMaterial(guiMaterial)

        entityEditorProperties = EntityEditorProperties(scene.window)
    }

    fun update(input: Input) {
        entityEditorProperties.update(currentProjectScene)
        if (entityEditorProperties.visible) {
            if (input.keyState(Input.Key.KEY_1) == Input.InputState.PRESSED) {
                editMode = EditMode.MOVE
            } else if (input.keyState(Input.Key.KEY_2) == Input.InputState.PRESSED) {
                editMode = EditMode.EDIT
            }


        }
    }
}
