package spatter

import rain.State
import rain.StateManager
import rain.api.Input
import rain.api.Window
import rain.api.gfx.Material
import rain.api.gfx.ResourceFactory
import rain.api.gfx.Texture2d
import rain.api.gfx.TextureFilter
import rain.api.scene.Scene
import rain.api.scene.TileGfx
import rain.api.scene.Tilemap

class EditorState(private val window: Window, stateManager: StateManager): State(stateManager) {
    private lateinit var toolsPanel: ToolsPanel
    private lateinit var resourcePanel: ResourcePanel
    private lateinit var materialPropertiesPanel: MaterialPropertiesPanel
    private lateinit var tilemapPropertiesPanel: TilemapPropertiesPanel

    private val tilemaps = ArrayList<Tilemap>()

    // TODO: Replace these with dynamic materials
    private lateinit var tilemapTexture: Texture2d
    private lateinit var tilemapMaterial: Material
    override fun init(resourceFactory: ResourceFactory, scene: Scene, input: Input) {
        setupEditorStyle()

        tilemapPropertiesPanel = TilemapPropertiesPanel(window)
        materialPropertiesPanel = MaterialPropertiesPanel(window)
        toolsPanel = ToolsPanel(window, materialPropertiesPanel, tilemapPropertiesPanel)
        resourcePanel = ResourcePanel(window)

        tilemapTexture = resourceFactory.buildTexture2d()
            .withName("tilemapTexture")
            .fromImageFile("./data/textures/tiles.png")
            .withFilter(TextureFilter.NEAREST)
            .build()

        tilemapTexture.setTiledTexture(16,16)
        tilemapMaterial = resourceFactory.buildMaterial()
            .withName("tilemapMaterial")
            .withVertexShader("./data/shaders/tilemap.vert.spv")
            .withFragmentShader("./data/shaders/tilemap.frag.spv")
            .withTexture(tilemapTexture)
            .withBlendEnabled(false)
            .build()
    }

    override fun update(resourceFactory: ResourceFactory, scene: Scene, input: Input, deltaTime: Float) {
        tilemapPropertiesPanel.update()
        toolsPanel.update()
        resourcePanel.update()
        materialPropertiesPanel.update()

        if (tilemapPropertiesPanel.created) {
            val defaultTileGfx = Array(tilemapPropertiesPanel.numTileX * tilemapPropertiesPanel.numTileY) {
                TileGfx(0, 0)
            }
            val tilemap = Tilemap()
            tilemap.create (
                resourceFactory,
                tilemapMaterial,
                tilemapPropertiesPanel.numTileX,
                tilemapPropertiesPanel.numTileY,
                tilemapPropertiesPanel.tileW.toFloat(),
                tilemapPropertiesPanel.tileH.toFloat(),
                defaultTileGfx
            )
            tilemap.update(defaultTileGfx)
            tilemap.transform.setPosition(100.0f, 24.0f, 1.0f)
            scene.addTilemap(tilemap)
        }
    }

}
