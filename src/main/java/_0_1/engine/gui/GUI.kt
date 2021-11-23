package _0_1.engine

import _0_1.engine.gui.*
import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.wrightgl.AbstractUniformsContainer
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.AbstractProgram
import _0_1.wrightgl.shader.Shader
import imgui.ImGui
import imgui.ImVec2
import imgui.extension.imguizmo.ImGuizmo
import imgui.extension.implot.ImPlot
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiColorEditFlags
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30


class GUI {
    var settings: ArrayList<AbstractGUISetting> = ArrayList()
    var exposedUniforms: HashMap<AbstractUniformsContainer, ArrayList<AbstractUniformsContainer.AbstractUniform>> =
        HashMap<AbstractUniformsContainer, ArrayList<AbstractUniformsContainer.AbstractUniform>>()


    companion object{
        var windowOnTop = ImBoolean(false)
    }
    private fun showExposedUniforms(){
        // kill me

        var imID = 0
        for((container, uniformList) in exposedUniforms){
            if (container.name != ""){
                ImGui.text( container.name )
            }
            for (abstractUniform in uniformList){

                ImGui.pushID(1000 + imID++)
                if (abstractUniform.javaClass.simpleName == "UniformFloat"){
                    val uniform = abstractUniform as AbstractUniformsContainer.UniformFloat
                    if (
                        ImGui.dragFloat(
                            uniform.name,
                            uniform.valueArray,
                        )
                    ) {
                        uniform.set(uniform.valueArray[0])
                    }
                } else if (abstractUniform.javaClass.simpleName == "UniformBoolean"){
                    val uniform = abstractUniform as AbstractUniformsContainer.UniformBoolean
                    if(
                        ImGui.checkbox(
                            uniform.name,
                            uniform.value
                        )
                    ) {
                        uniform.set(!uniform.value)
                    }
                } else if (abstractUniform.javaClass.simpleName == "UniformInt"){
                    val uniform = abstractUniform as AbstractUniformsContainer.UniformInt
                    if (
                        ImGui.dragInt(
                            uniform.name,
                            uniform.valueArray,
                        )
                    ) {
                        uniform.set(uniform.valueArray[0])
                    }
                }
            ImGui.popID()
        }
    }
}

fun showSettingsInImGui(){
        // ---------------- SHOW GUI ---------------- //
        ImGui.begin("Settings")
        for(guiSetting in settings){
            if (guiSetting is GUISliderFloat){
                ImGui.sliderFloat(
                    guiSetting.name,
                    guiSetting.value as FloatArray,
                    guiSetting.min,
                    guiSetting.max
                )
            } else if (guiSetting is GUISliderFloatInfinite){
                ImGui.dragFloat(
                    guiSetting.name,
                    guiSetting.value as FloatArray,
                )
            } else if (guiSetting is GUISliderVec3){
                ImGui.sliderFloat3(
                    guiSetting.name,
                    guiSetting.value as FloatArray,
                    guiSetting.min,
                    guiSetting.max
                )
            } else if (guiSetting is GUISliderVec3Infinite){
                ImGui.dragFloat3(
                    guiSetting.name,
                    guiSetting.value as FloatArray,
                )
            } else if (guiSetting is GUISliderVec4){
                ImGui.sliderFloat4(
                    guiSetting.name,
                    guiSetting.value as FloatArray,
                    guiSetting.min,
                    guiSetting.max
                )
            } else if (guiSetting is GUISliderVec4Infinite){
                ImGui.dragFloat4(
                    guiSetting.name,
                    guiSetting.value as FloatArray,
                )
            } else if (guiSetting is GUICheckbox){
                ImGui.checkbox(
                    guiSetting.name,
                    (guiSetting.value as ImBoolean)
                )
            }
        }

        showExposedUniforms()


        if (ImGui.checkbox( "On Top", windowOnTop ))
            glfwSetWindowAttrib(Global.engine.glfwWindow, GLFW_FLOATING, if (windowOnTop.get()) 1 else 0);

        ImGui.end()
    }
    fun showTexturesInImGui() {
        // TODO: move away from here
        // ---------------- SHOW TEXTURES ---------------- //
        ImGui.begin("FB Textures")
        val imWinSz = IVec2(ImGui.getWindowSize().x, ImGui.getWindowSize().y)
        var unnamedFBIdx = 0
        for(fb in FB.frameBuffersList) {

            if (fb.name != null)
                ImGui.text(fb.name)
            else {
                ImGui.text("Unnamed FB " + unnamedFBIdx++.toString())
            }

            if(fb.textures.size > 0){
                for(tex in fb.textures) {
                    if (tex.res.z == 1){
                        //                        val
                        val texRes = tex.res.xy
                        //                        val texRatio = tex.res.x/tex.res.y
                        var newTexRes = texRes
                        val maxSz = imWinSz.x
                        if (texRes.x > maxSz){
                            val ratioImWinxTexSzX = texRes.x/maxSz
                            newTexRes /= ratioImWinxTexSzX
                            newTexRes /= 2
                        }
                        //                        val bigger
                        ImGui.image(tex.pid,newTexRes.x.toFloat(),newTexRes.y.toFloat(), 0.0f, 1.0f, 1.0f, 0.0f)

                    }
                }
            } else {
                val tex = fb.depthTexture!!
                if (tex.res.z == 1){
                    //                        val
                    val texRes = tex.res.xy
                    //                        val texRatio = tex.res.x/tex.res.y
                    var newTexRes = texRes
                    val maxSz = imWinSz.x
                    if (texRes.x > maxSz){
                        val ratioImWinxTexSzX = texRes.x/maxSz
                        newTexRes /= ratioImWinxTexSzX
                        newTexRes /= 2
                    }
                    ImGui.image(tex.pid,newTexRes.x.toFloat(),newTexRes.y.toFloat(), 0.0f, 1.0f, 1.0f, 0.0f)
                }
            }
        }
        ImGui.end()
    }

    fun showErrorsInImGui() {
        val thereAreShaderErrors: Boolean = Shader.shadersFailedCompilationList.size > 0
        val thereAreTextureErrors: Boolean = Texture.fileTexturesFailedLoading.size > 0
        if (thereAreTextureErrors || thereAreShaderErrors) {
            ImGui.setNextWindowPos(0f, 0f)
            val open_ptr = ImBoolean()
            open_ptr.set(true)
            ImGui.begin(
                "0 ",
                open_ptr,
                ImGuiWindowFlags.NoBackground or ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.AlwaysAutoResize or
                        ImGuiWindowFlags.NoResize or ImGuiWindowFlags.NoMove or ImGuiWindowFlags.NoTitleBar
            )
            ImGui.colorEdit3(
                " ",
                floatArrayOf(1f, 0f, 0f),
                ImGuiColorEditFlags.NoInputs or ImGuiColorEditFlags.NoPicker
            )

            // Shader errors
            if(thereAreShaderErrors){
                val programsWithFailedCompilation = LinkedHashSet<AbstractProgram>()
                for (sh in Shader.shadersFailedCompilationList) {
                    val title = sh.fileName
                    for (program in sh.programs) {
                        programsWithFailedCompilation.add(program)
                    }
                    //                title += itrShader->first->name;
                    ImGui.text(title)
                    ImGui.text(sh.errorLog)
                }
                for (program in programsWithFailedCompilation) {
                    val err = program.errorLog
                    if (err != null) ImGui.text(err)
                }
            }

            // Texture errors
            if(thereAreTextureErrors){
                for (texture in Texture.fileTexturesFailedLoading) {
                    val title = texture.fileName
                    ImGui.text(title)
                    ImGui.text(texture.errorLog)
                }
            }

            ImGui.setWindowCollapsed(true)
            ImGui.end()
            //            std::map<Shader*, std::string>::iterator itrProgram;
//            for (itrProgram = shaderCompilationErrors.begin(); itrProgram != shaderCompilationErrors.end(); ++itrProgram) {
//                std::string title = "Program";
//                title += itrProgram->first->name;
//                ImGui::Text( title.c_str());
//                ImGui::Text( itrProgram->second.c_str());
//            }
//
//            ImGui::SetWindowCollapsed(true);
//            ImGui::End();
        }

    }
    fun setupImGui(glfwWindow: Long){

        ImGui.styleColorsDark()
        val style = ImGui.getStyle()
        style.windowRounding = 0.0f

        ImGui.getIO().configFlags.or(ImGuiConfigFlags.ViewportsEnable)


        // IMGUI EXTENSIONS
        ImPlot.createContext();
        ImGuizmo.setEnabled(true);


        // ImGui theme.
        val colors = ImGui.getStyle().colors
        run {
            colors[ImGuiCol.Text] = floatArrayOf(1.00f, 1.00f, 1.00f, 0.95f)
            colors[ImGuiCol.TextDisabled] = floatArrayOf(0.50f, 0.50f, 0.50f, 1.00f)
            colors[ImGuiCol.WindowBg] = floatArrayOf(0.13f, 0.12f, 0.12f, 1.00f)
            colors[ImGuiCol.ChildBg] = floatArrayOf(1.00f, 1.00f, 1.00f, 0.00f)
            colors[ImGuiCol.PopupBg] = floatArrayOf(0.05f, 0.05f, 0.05f, 0.94f)
            colors[ImGuiCol.Border] = floatArrayOf(0.53f, 0.53f, 0.53f, 0.46f)
            colors[ImGuiCol.BorderShadow] = floatArrayOf(0.00f, 0.00f, 0.00f, 0.00f)
            colors[ImGuiCol.FrameBg] = floatArrayOf(0.00f, 0.00f, 0.00f, 0.85f)
            colors[ImGuiCol.FrameBgHovered] = floatArrayOf(0.22f, 0.22f, 0.22f, 0.40f)
            colors[ImGuiCol.FrameBgActive] = floatArrayOf(0.16f, 0.16f, 0.16f, 0.53f)
            colors[ImGuiCol.TitleBg] = floatArrayOf(0.00f, 0.00f, 0.00f, 1.00f)
            colors[ImGuiCol.TitleBgActive] = floatArrayOf(0.00f, 0.00f, 0.00f, 1.00f)
            colors[ImGuiCol.TitleBgCollapsed] = floatArrayOf(0.00f, 0.00f, 0.00f, 0.51f)
            colors[ImGuiCol.MenuBarBg] = floatArrayOf(0.12f, 0.12f, 0.12f, 1.00f)
            colors[ImGuiCol.ScrollbarBg] = floatArrayOf(0.02f, 0.02f, 0.02f, 0.53f)
            colors[ImGuiCol.ScrollbarGrab] = floatArrayOf(0.31f, 0.31f, 0.31f, 1.00f)
            colors[ImGuiCol.ScrollbarGrabHovered] = floatArrayOf(0.41f, 0.41f, 0.41f, 1.00f)
            colors[ImGuiCol.ScrollbarGrabActive] = floatArrayOf(0.48f, 0.48f, 0.48f, 1.00f)
            colors[ImGuiCol.CheckMark] = floatArrayOf(0.79f, 0.79f, 0.79f, 1.00f)
            colors[ImGuiCol.SliderGrab] = floatArrayOf(0.48f, 0.47f, 0.47f, 0.91f)
            colors[ImGuiCol.SliderGrabActive] = floatArrayOf(0.56f, 0.55f, 0.55f, 0.62f)
            colors[ImGuiCol.Button] = floatArrayOf(0.50f, 0.50f, 0.50f, 0.63f)
            colors[ImGuiCol.ButtonHovered] = floatArrayOf(0.67f, 0.67f, 0.68f, 0.63f)
            colors[ImGuiCol.ButtonActive] = floatArrayOf(0.4f, 0.26f, 0.26f, 0.63f)
            colors[ImGuiCol.Header] = floatArrayOf(0.54f, 0.54f, 0.54f, 0.58f)
            colors[ImGuiCol.HeaderHovered] = floatArrayOf(0.64f, 0.65f, 0.65f, 0.80f)
            colors[ImGuiCol.HeaderActive] = floatArrayOf(0.25f, 0.25f, 0.25f, 0.80f)
            colors[ImGuiCol.Separator] = floatArrayOf(0.58f, 0.58f, 0.58f, 0.50f)
            colors[ImGuiCol.SeparatorHovered] = floatArrayOf(0.81f, 0.81f, 0.81f, 0.64f)
            colors[ImGuiCol.SeparatorActive] = floatArrayOf(0.81f, 0.81f, 0.81f, 0.64f)
            colors[ImGuiCol.ResizeGrip] = floatArrayOf(0.87f, 0.87f, 0.87f, 0.53f)
            colors[ImGuiCol.ResizeGripHovered] = floatArrayOf(0.87f, 0.87f, 0.87f, 0.74f)
            colors[ImGuiCol.ResizeGripActive] = floatArrayOf(0.87f, 0.87f, 0.87f, 0.74f)
            colors[ImGuiCol.Tab] = floatArrayOf(0.01f, 0.01f, 0.01f, 0.86f)
            colors[ImGuiCol.TabHovered] = floatArrayOf(0.29f, 0.29f, 0.29f, 1.00f)
            colors[ImGuiCol.TabActive] = floatArrayOf(0.31f, 0.31f, 0.31f, 1.00f)
            colors[ImGuiCol.TabUnfocused] = floatArrayOf(0.02f, 0.02f, 0.02f, 1.00f)
            colors[ImGuiCol.TabUnfocusedActive] = floatArrayOf(0.19f, 0.19f, 0.19f, 1.00f)
            //colors[ImGuiCol.DockingPreview] = new float[]{0.38f, 0.48f, 0.60f, 1.00f};
            //colors[ImGuiCol.DockingEmptyBg] = new float[]{0.20f, 0.20f, 0.20f, 1.00f};
            colors[ImGuiCol.PlotLines] = floatArrayOf(0.61f, 0.61f, 0.61f, 1.00f)
            colors[ImGuiCol.PlotLinesHovered] = floatArrayOf(0.68f, 0.68f, 0.68f, 1.00f)
            colors[ImGuiCol.PlotHistogram] = floatArrayOf(0.90f, 0.77f, 0.33f, 1.00f)
            colors[ImGuiCol.PlotHistogramHovered] = floatArrayOf(0.87f, 0.55f, 0.08f, 1.00f)
            colors[ImGuiCol.TextSelectedBg] = floatArrayOf(0.47f, 0.60f, 0.76f, 0.47f)
            colors[ImGuiCol.DragDropTarget] = floatArrayOf(0.58f, 0.58f, 0.58f, 0.90f)
            colors[ImGuiCol.NavHighlight] = floatArrayOf(0.60f, 0.60f, 0.60f, 1.00f)
            colors[ImGuiCol.NavWindowingHighlight] = floatArrayOf(1.00f, 1.00f, 1.00f, 0.70f)
            colors[ImGuiCol.NavWindowingDimBg] = floatArrayOf(0.80f, 0.80f, 0.80f, 0.20f)
            colors[ImGuiCol.ModalWindowDimBg] = floatArrayOf(0.80f, 0.80f, 0.80f, 0.35f)
            colors[ImGuiCol.WindowBg] = floatArrayOf(0.05f, 0.05f, 0.05f, 0.5f)
            colors[ImGuiCol.PopupBg] = floatArrayOf(0.05f, 0.05f, 0.05f, 0.5f)
            colors[ImGuiCol.TitleBg] = floatArrayOf(0.05f, 0.05f, 0.05f, 0.5f)
            colors[ImGuiCol.TitleBgActive] = floatArrayOf(0.05f, 0.05f, 0.05f, 0.5f)
            colors[ImGuiCol.Border][3] = 0.0f
        }
    }
}


