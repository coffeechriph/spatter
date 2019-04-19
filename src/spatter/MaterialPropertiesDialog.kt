package spatter

import rain.api.WindowContext
import rain.api.gui.v2.*

class MaterialPropertiesDialog(window: WindowContext): EditorDialog {
    private val panel: Panel
    private val selectVertexShader: Button
    private val vertexShaderLabel: Label
    private val selectFragmentShader: Button
    private val fragmentShaderLabel: Label
    private val selectTexture: Button
    private val textureLabel: Label
    private val doneButton: Button
    private val fileChooseDialog: FileChooseDialog
    private var resourceSelectionType = 0

    init {
        val layout = FillRowLayout()
        layout.componentsPerRow = 2
        panel = guiManagerCreatePanel(layout)
        panel.w = 300.0f
        panel.h = 500.0f
        panel.x = window.framebufferSize.x / 2 - panel.w / 2
        panel.y = window.framebufferSize.y / 2 - panel.h / 2
        panel.visible = false
        panel.skin = editorSkin

        selectVertexShader = panel.createButton("Vertex Shader")
        vertexShaderLabel = panel.createLabel("None")
        selectFragmentShader = panel.createButton("Fragment Shader")
        fragmentShaderLabel = panel.createLabel("None")
        selectTexture = panel.createButton("Texture")
        textureLabel = panel.createLabel("None")
        doneButton = panel.createButton("Done")

        fileChooseDialog = FileChooseDialog(window)
    }

    fun update() {
        fileChooseDialog.update()
        if (!fileChooseDialog.isOpen) {
            if (selectVertexShader.clicked) {
                resourceSelectionType = 1
                fileChooseDialog.show("./data/shaders")
            }
            else if (selectFragmentShader.clicked) {
                resourceSelectionType = 2
                fileChooseDialog.show("./data/shaders")
            }
            else if (selectTexture.clicked) {
                resourceSelectionType = 3
                fileChooseDialog.show("./data/textures")
            }
        }

        if (fileChooseDialog.hasSelected) {
            when (resourceSelectionType) {
                1 -> vertexShaderLabel.string = fileChooseDialog.lastSelectedItem!!.string
                2 -> fragmentShaderLabel.string = fileChooseDialog.lastSelectedItem!!.string
                3 -> textureLabel.string = fileChooseDialog.lastSelectedItem!!.string
            }
        }

        if (doneButton.clicked) {
            // TODO: Verify that we have atleast vertex & fragment shader
            panel.visible = false
        }
    }

    override fun shown(): Boolean {
        return panel.visible
    }

    override fun hide() {
        panel.visible = false
    }

    override fun show() {
        panel.visible = true
    }
}
