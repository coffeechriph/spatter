package spatter

import rain.api.Window
import rain.api.gui.v2.*

class TilemapPropertiesPanel(private val window: Window) {
    private var layout: FillRowLayout = FillRowLayout()
    private var panel: Panel
    private var widthLabel: Label
    private var heightLabel: Label
    private var widthTextField: TextField
    private var heightTextField: TextField
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
        widthTextField = panel.createTextField("100")

        heightLabel = panel.createLabel("Height")
        heightTextField = panel.createTextField("100")

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