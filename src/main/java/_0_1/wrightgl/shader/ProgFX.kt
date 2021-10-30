package _0_1.wrightgl.shader

import _0_1.main.Glob

class ProgFX( ) : ProgRender(){
    constructor(
        _fileNameFrag: String,
        _folderPath: String = Glob.engine!!.fileSystem.sketchResourcesFolder
    ) : this() {
        fragShader = Shader(
            _fileNameFrag,
            Shader.Type.FRAGMENT_SHADER,
            this,
            _folderPath)
        vertShader = Shader(
            "quad.vp",
            Shader.Type.VERTEX_SHADER,
            this,
            Glob.engine!!.fileSystem.globalResourcesFolder)

        link()
    }

}