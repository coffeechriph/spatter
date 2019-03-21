package spatter.entity

import org.joml.Vector2f
import rain.api.Input
import rain.api.entity.Entity
import rain.api.entity.EntitySystem
import rain.api.gfx.*
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
    var entitySystem: EntitySystem<Entity>
    var entityQuad: VertexBuffer
    var editMode: EditMode = EditMode.MOVE
    private var spriteMaterial: Material

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
            .withVertexShader("./data/shaders/basic.vert.spv")
            .withFragmentShader("./data/shaders/basic.frag.spv")
            .withTexture(spriteTexture)
            .withBlendEnabled(false)
            .build()

        entityEditorProperties = EntityEditorProperties(scene.window)
        entitySystem = scene.newSystem(spriteMaterial)
        entityQuad = resourceFactory.buildVertexBuffer()
            .as2dQuad()
    }

    fun update(input: Input) {
        entityEditorProperties.update(currentProjectScene)
        if (entityEditorProperties.visible) {
            if (input.keyState(Input.Key.KEY_1) == Input.InputState.PRESSED) {
                editMode = EditMode.MOVE
            } else if (input.keyState(Input.Key.KEY_2) == Input.InputState.PRESSED) {
                editMode = EditMode.EDIT
            }

            if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.PRESSED) {
                if (entityEditorProperties.selectedEntity != null) {
                    entityEditorProperties.selectedEntity!!.instances.add(ProjectEntityInstance(
                        input.mousePosition.x.toFloat(),
                        input.mousePosition.y.toFloat(),
                        1.0f,
                        entityEditorProperties.selectedImageIndex.x,
                        entityEditorProperties.selectedImageIndex.y,
                        32.0f, 32.0f))

                    val entity = Entity()
                    entitySystem.newEntity(entity)
                        .attachRenderComponent(spriteMaterial, Mesh(entityQuad, null))
                        .build()
                    entity.transform.x = input.mousePosition.x.toFloat()
                    entity.transform.y = input.mousePosition.y.toFloat()
                    entity.transform.sx = 32.0f
                    entity.transform.sy = 32.0f
                    entity.transform.z = 1.0f
                    entity.getRenderComponents()[0].textureTileOffset.x = entityEditorProperties.selectedImageIndex.x
                    entity.getRenderComponents()[0].textureTileOffset.y = entityEditorProperties.selectedImageIndex.y
                }
            }
        }
    }
}
