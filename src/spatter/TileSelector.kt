package spatter

import org.joml.Vector2i
import rain.api.Window
import rain.api.gfx.Texture2d
import rain.api.gui.v2.*

class TileSelector(private val window: Window) {
    private var layout: GridLayout = GridLayout()
    private var panel: Panel

    var visible = false
        set(value) {
            field = value
            panel.visible = value
        }
        get() {
            return panel.visible
        }
    var created = false
        private set
    var selectedTileIndex: Vector2i = Vector2i(0,0)

    val images = ArrayList<Image>()
    init {
        layout.gridW = 50.0f
        layout.gridH = 50.0f

        panel = guiManagerCreatePanel(layout)
        panel.w = 400.0f
        panel.h = 400.0f
        panel.skin = editorSkin
        panel.visible = false
    }

    fun update() {
        created = false

        for(image in images) {
            if (image.clicked) {
                selectedTileIndex = Vector2i(image.imageTileIndexX, image.imageTileIndexY)
                println(selectedTileIndex)
            }
        }
    }

    fun show(selectedTilemapTexture: Texture2d) {
        if (!panel.visible) {
            for (image in images) {
                panel.removeComponent(image)
            }

            val numTilesX = selectedTilemapTexture.getWidth() / selectedTilemapTexture.getTileWidthPixels()
            val numTilesY = selectedTilemapTexture.getHeight() / selectedTilemapTexture.getTileHeightPixels()

            var tx = 0
            var ty = 0
            for (i in 0 until numTilesX*numTilesY) {
                val image = panel.createImage(tx, ty, "")
                images.add(image)

                tx += 1
                if (tx >= numTilesX) {
                    tx = 0
                    ty += 1
                }
            }
        }

        panel.x = window.size.x / 2.0f - panel.w / 2.0f
        panel.y = window.size.y / 2.0f - panel.h / 2.0f
        panel.visible = true
    }
}