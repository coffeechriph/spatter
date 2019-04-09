package spatter.entity

import org.joml.Vector2i
import rain.api.Input
import rain.api.entity.Entity
import rain.api.gfx.*
import rain.api.scene.Camera
import rain.api.scene.Scene
import spatter.project.ProjectEntityInstance
import spatter.project.currentProjectScene

class EntityEditor(resourceFactory: ResourceFactory, private val entityEditorDialog: EntityEditorDialog) {
    var spriteTexture: Texture2d
    var spriteMaterial: Material
    var entityQuad: VertexBuffer
    var entityMesh: Mesh

    init {
        spriteTexture = resourceFactory.buildTexture2d()
            .withName("spriteTexture")
            .fromImageFile("./data/textures/tilemap_8x8.png")
            .withFilter(TextureFilter.NEAREST)
            .build()

        spriteTexture.setTiledTexture(8,8)
        spriteMaterial = resourceFactory.buildMaterial()
            .withName("spriteMaterial")
            .withVertexShader("./data/shaders/basic.vert.spv")
            .withFragmentShader("./data/shaders/basic.frag.spv")
            .withTexture(spriteTexture)
            .withBlendEnabled(false)
            .build()

        entityQuad = resourceFactory.buildVertexBuffer()
            .as2dQuad()

        entityMesh = Mesh(entityQuad, null)
    }

    fun update(scene: Scene, input: Input, camera: Camera, gridSize: Vector2i) {
        entityEditorDialog.update(currentProjectScene, spriteTexture)
        if (entityEditorDialog.visible) {
            if (input.mouseState(Input.Button.MOUSE_BUTTON_LEFT) == Input.InputState.PRESSED) {
                if (entityEditorDialog.selectedEntity != null) {
                    val mx = input.mousePosition.x.toFloat() - camera.x
                    val my = input.mousePosition.y.toFloat() - camera.y

                    val entity = Entity()
                    scene.newEntity(entity)
                        .attachRenderComponent(spriteMaterial, entityMesh)
                        .build()
                    entity.transform.x = ((mx.toInt() / gridSize.x) * gridSize.x).toFloat() + gridSize.x/2
                    entity.transform.y = ((my.toInt() / gridSize.y) * gridSize.y).toFloat() + gridSize.y/2
                    entity.transform.sx = entityEditorDialog.entityWidth()
                    entity.transform.sy = entityEditorDialog.entityHeight()
                    entity.transform.z = 1.0f
                    entity.getRenderComponents()[0].textureTileOffset.x = entityEditorDialog.selectedImageIndex.x
                    entity.getRenderComponents()[0].textureTileOffset.y = entityEditorDialog.selectedImageIndex.y

                    entityEditorDialog.selectedEntity!!.instances.add(
                        ProjectEntityInstance(
                            mx,
                            my,
                            1.0f,
                            entityEditorDialog.selectedImageIndex.x,
                            entityEditorDialog.selectedImageIndex.y,
                            32.0f, 32.0f,
                            entity
                        )
                    )
                }
            }
        }
    }
}
