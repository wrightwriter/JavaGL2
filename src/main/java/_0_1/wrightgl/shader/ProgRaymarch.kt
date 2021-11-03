package _0_1.wrightgl.shader

import _0_1.main.Global

class ProgRaymarch private constructor( ) : ProgRender(){
    constructor(
        _fileNameFrag: String,
        _folderPath: String = Global.engine.fileSystem.sketchResourcesFolder
    ) : this() {
        fragShader = Shader(
            _fileNameFrag,
            Shader.Type.FRAGMENT_SHADER,
            this,
            _folderPath)
        vertShader = Shader(
            "quadRaymarch.vp",
            Shader.Type.VERTEX_SHADER,
            this,
            Global.engine.fileSystem.globalResourcesFolder)

        link()
    }
}