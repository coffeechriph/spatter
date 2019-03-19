package spatter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

private var mapper = jacksonObjectMapper()

var jsonSceneWriter = mapper.writerFor(ProjectScene::class.java)
var jsonSceneReader = mapper.readerFor(ProjectScene::class.java)
