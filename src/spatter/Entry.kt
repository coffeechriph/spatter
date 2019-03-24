package spatter

import rain.Rain
import rain.api.Api

class Entry: Rain() {
    private lateinit var editorState: EditorState
    override fun init() {
        editorState = EditorState(window, stateManager)
        stateManager.states["editor"] = editorState
        stateManager.startState("editor")
    }
}

fun main(args: Array<String>) {
    val app = Entry()
    app.create(2560, 1440, "Spatter", Api.VULKAN)
    app.run()
}
