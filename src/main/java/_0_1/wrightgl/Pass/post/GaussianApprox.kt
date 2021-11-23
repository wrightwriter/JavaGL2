package _0_1.wrightgl.Pass.post

import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.math.vector.Vec2
import _0_1.wrightgl.Pass.PassFX
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FBPingPong
import _0_1.wrightgl.shader.ProgFX

class GaussianApprox constructor(
    _resolution: IVec2 = Global.engine.res.copy(),
    _isPingPong: Boolean = false
): PassFXFB() {
    var blurDirection = Vec2(1,0)
        set(v){
            field = v
            setUniform("blurDirection", blurDirection)
        }
    var window = 10
        set(v){
            field = v
            setUniform("window", window)
        }
    var sigma= 1.0f
        set(v){
            field = v
            setUniform("sigma", sigma)
        }
    var spread = 1.0f
        set(v){
            field = v
            setUniform("spread", spread)
        }
    var gain = 1.0f
        set(v){
            field = v
            setUniform("gain", gain)
        }
    init{
        shaderProgram = ProgFX(
            "FX/gaussianApprox.fp",
            Global.engine.fileSystem.globalResourcesFolder
        )

        blurDirection = Vec2(1,0)
        window = 5
        sigma= 1.0f
        spread = 1.0f
        gain = 1.0f

        fb = if (_isPingPong)
            FBPingPong(_width = _resolution.x, _height = _resolution.y)
        else
            FB(_width = _resolution.x, _height = _resolution.y)
    }
}