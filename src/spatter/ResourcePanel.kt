package spatter

import org.joml.Vector4f
import rain.api.Window
import rain.api.gui.v2.*

class ResourcePanel(private val window: Window) {
    private val treeView: TreeView

    init {
        treeView = guiManagerCreateTreeView()
        treeView.x = 0.0f
        treeView.y = 34.0f
        treeView.w = 100.0f
        treeView.h = window.size.y.toFloat() - treeView.y
        treeView.skin = editorSkin
        treeView.addItem("Materials")
        treeView.addItem("Entities")
    }

    fun update() {
        treeView.h = window.size.y.toFloat() - treeView.y
    }
}