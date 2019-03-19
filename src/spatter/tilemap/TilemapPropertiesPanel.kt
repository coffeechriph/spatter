package spatter.tilemap

import rain.api.Window
import rain.api.gui.v2.*
import spatter.editorSkin

class TilemapPropertiesPanel(private val window: Window) {
    var numTileX = 0
        get() {
            field = widthTextField.string.toInt()
            return field
        }
        private set
    var numTileY = 0
        get() {
            field = heightTextField.string.toInt()
            return field
        }
        private set
    var tileW = 0
        get(){
            field = tileWidthTextField.string.toInt()
            return field
        }
        private set
    var tileH = 0
        get(){
            field = tileHeightTextField.string.toInt()
            return field
        }
        private set

    private var layout: FillRowLayout = FillRowLayout()
    private var panel: Panel
    private var widthLabel: Label
    private var heightLabel: Label
    private var widthTextField: TextField
    private var heightTextField: TextField


    private var tileWidthLabel: Label
    private var tileHeightLabel: Label
    private var tileWidthTextField: TextField
    private var tileHeightTextField: TextField
    private val doneButton: Button

    var created = false
        private set

    init {
        layout.componentsPerRow = 1
        layout.componentHeight = 24.0f

        panel = guiManagerCreatePanel(layout)
        panel.w = 100.0f
        panel.h = 400.0f
        panel.skin = editorSkin
        panel.visible = false

        widthLabel = panel.createLabel("Width")
        widthTextField = panel.createTextField("10")
        heightLabel = panel.createLabel("Height")
        heightTextField = panel.createTextField("10")

        tileWidthLabel = panel.createLabel("Tile Width")
        tileWidthTextField = panel.createTextField("32")
        tileHeightLabel = panel.createLabel("Tile Height")
        tileHeightTextField = panel.createTextField("32")

        doneButton = panel.createButton("Done")
    }

    fun update() {
        created = false
        if (doneButton.clicked) {
            panel.visible = false
            created = true
        }
    }

    fun show() {
        panel.x = window.size.x / 2.0f - panel.w / 2.0f
        panel.y = window.size.y / 2.0f - panel.h / 2.0f
        panel.visible = true
    }
}
