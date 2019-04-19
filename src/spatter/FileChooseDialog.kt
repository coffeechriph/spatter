package spatter

import rain.api.WindowContext
import rain.api.gui.v2.*
import java.io.File

private data class FileItem(val parent: FileItem?, val path: String, val name: String, val children: MutableList<FileItem>)

class FileChooseDialog(private val window: WindowContext) {
    val isOpen: Boolean
        get() {
            return panel.visible
        }

    var hasSelected: Boolean = false
    var lastSelectedItem: ListItem? = null
    private val panel: Window = guiManagerCreateWindow(FillRowLayout(), "File Select")
    private val directoryLabel: Label
    private val selectButton: Button
    private val fileTreeView: TreeView
    private val fileItems = ArrayList<FileItem>()

    init {
        panel.w = (window.framebufferSize.x / 4).toFloat()
        panel.h = 400.0f
        panel.x = window.framebufferSize.x / 2 - panel.w / 2
        panel.y = window.framebufferSize.y / 2 - panel.h / 2
        panel.visible = false
        panel.skin = editorSkin

        directoryLabel = panel.createLabel(File("./").absolutePath)
        directoryLabel.x = 0.0f
        directoryLabel.y = 0.0f
        directoryLabel.w = panel.w - 80.0f
        directoryLabel.h = 24.0f

        selectButton = panel.createButton("Select")
        selectButton.x = directoryLabel.x + directoryLabel.w
        selectButton.y = 0.0f
        selectButton.w = 80.0f
        selectButton.h = 24.0f

        fileTreeView = guiManagerCreateTreeView()
        fileTreeView.x = panel.x + panel.w
        fileTreeView.y = panel.y
        fileTreeView.w = 400.0f
        fileTreeView.h = 400.0f
        fileTreeView.visible = false
        fileTreeView.skin = editorSkin
    }

    private fun populateTreeView(fileItems: MutableList<FileItem>, listItem: ListItem?) {
        if (listItem == null) {
            for (item in fileItems) {
                val parent = fileTreeView.addItem(item.name)
                populateTreeView(item.children, parent)
            }
        }
        else {
            for (item in fileItems) {
                val parent = listItem.addItem(item.name)
                populateTreeView(item.children, parent)
            }
        }
    }

    private fun createItemsForCurrentPath(parent: FileItem) {
        val file = File(parent.path)
        if (file.isDirectory) {
            for (subFile in file.listFiles()) {
                val fileItem = FileItem(parent, subFile.absolutePath, subFile.name, mutableListOf())
                createItemsForCurrentPath(fileItem)
                parent.children.add(fileItem)
            }
        }
    }

    fun show(rootDir: String) {
        fileTreeView.clearItems()
        fileItems.clear()

        directoryLabel.string = File(rootDir).absolutePath
        for (file in File(rootDir).listFiles()) {
            val item = FileItem(null, file.absolutePath, file.name, mutableListOf())
            createItemsForCurrentPath(item)
            fileItems.add(item)
        }

        populateTreeView(fileItems, null)
        panel.visible = true
        fileTreeView.visible = true
    }

    fun update() {
        fileTreeView.visible = panel.visible
        hasSelected = false
        fileTreeView.x = panel.x + panel.w
        fileTreeView.y = panel.y

        if (fileTreeView.selectedItem != null) {
            lastSelectedItem = fileTreeView.selectedItem
        }

        if (selectButton.clicked && lastSelectedItem != null) {
            panel.visible = false
            fileTreeView.visible = false
            hasSelected = true
        }
    }
}
