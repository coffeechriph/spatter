package spatter

import rain.Rain
import rain.api.Api

class Entry: Rain() {
    override fun init() {

    }
}
fun main(args: Array<String>) {
    val app = Entry()
    app.create(1600, 900, "Spatter", Api.VULKAN)
    app.run()
}
