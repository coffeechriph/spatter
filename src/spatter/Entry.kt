package spatter

import rain.Rain
import rain.api.Api

class Entry: Rain() {
    private lateinit var editorState: EditorState
    override fun init() {
        editorState = EditorState(window, sceneManager, resourceFactory)
        sceneManager.loadScene(editorState)
    }
}

fun main(args: Array<String>) {
    val app = Entry()
    app.create(2560, 1440, "Spatter", Api.VULKAN)
    app.run()
}
