package spatter

import rain.api.Window
import rain.api.gui.v2.ListItem
import rain.api.gui.v2.TreeView
import rain.api.gui.v2.guiManagerCreateTreeView

class ResourcePanel(private val window: Window) {
    private val treeView: TreeView
    private val entitiesListItem: ListItem
    private val entities = ArrayList<ListItem>()

    fun getSelectedEntityItem(): ListItem? {
        if (treeView.selectedItem != null) {
            if (entities.contains(treeView.selectedItem!!)) {
                return treeView.selectedItem
            }
        }

        return null
    }

    init {
        treeView = guiManagerCreateTreeView()
        treeView.x = 0.0f
        treeView.y = 34.0f
        treeView.w = 100.0f
        treeView.h = window.size.y.toFloat() - treeView.y
        treeView.skin = editorSkin
        treeView.addItem("Materials")
        entitiesListItem = treeView.addItem("Entities")
    }

    fun update() {
        treeView.h = window.size.y.toFloat() - treeView.y
    }

    fun recreateEntitiesList(projectScene: ProjectScene) {
        for (entity in entities) {
            treeView.removeItem(entity)
        }
        entities.clear()

        for (entity in projectScene.entities) {
            val item = entitiesListItem.addItem(entity.key)
            entities.add(item)
        }
    }
}
