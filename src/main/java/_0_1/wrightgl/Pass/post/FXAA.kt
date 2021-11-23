package _0_1.wrightgl.Pass.post

import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.math.vector.Vec2
import _0_1.wrightgl.Pass.PassFX
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.shader.ProgFX

class FXAA constructor(
    _res: IVec2 = Global.engine.res
): PassFXFB(
    "FX/fxaa.fp",
    Global.engine.fileSystem.globalResourcesFolder,
    _res
) {
    var lumaThreshold: Float = 0.5f
        set(v) {
            field = v
            setUniform("lumaThreshold", lumaThreshold)
        }
    var maxSpan: Float = 8.0f
        set(v) {
            field = v
            setUniform("maxSpan", maxSpan)
        }
    var directionReduceMultiplier: Float = 0.0f
        set(v) {
            field = v
            setUniform("directionReduceMultiplier", directionReduceMultiplier)
        }
    var directionReduceMinimum: Float = 0.0f
        set(v) {
            field = v
            setUniform("directionReduceMinimum", directionReduceMinimum)
        }
    init{

        lumaThreshold = 0.5f
        maxSpan = 8.0f
        directionReduceMultiplier = 0.0f
        directionReduceMinimum= 0.0f
    }
}