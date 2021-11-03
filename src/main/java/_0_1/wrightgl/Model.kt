package _0_1.wrightgl

import _0_1.main.Global
import _0_1.math.vector.Vec4
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.buffer.VBIndexed
import org.lwjgl.BufferUtils
import org.lwjgl.PointerBuffer
import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.util.*


class Model(
    val fileName: String,
    val folderPath: String = Global.engine.fileSystem.sketchResourcesFolder
) {


    var errorLog: String? = null

    companion object{
//        var fileTexturesList: MutableList<Texture> = ArrayList()
//            private set
//        var fileTexturesFailedLoading: MutableList<Texture> = ArrayList()
//            private set
    }


    lateinit var positions: FloatArray
    lateinit var texCoords: FloatArray
    lateinit var normals: FloatArray
    lateinit var colours: FloatArray


    val materials: MutableList<Material> = ArrayList<Material>()
    var meshes: ArrayList<Mesh> = ArrayList<Mesh>()
        protected set

    init{
        loadFile(fileName,folderPath)
    }

    private fun loadFile(
        _filePath: String,
        _folderPath: String = Global.engine.fileSystem.sketchResourcesFolder
    ): Boolean{
        try {
            val scene: AIScene = Assimp.aiImportFile(
                (_folderPath + _filePath),
                Assimp.aiProcess_Triangulate
            )!!
//            var upAxis = 1
//            var upAxisSign = 1
//            var frontAxis = 2
//            var frontAxisSign = 1
//            var coordAxis = 0
//            var coordAxisSign = 1
//
//            var unitScaleFactor = 1.0
//
//            val metaData =scene.mMetaData()!!
//            for (metaDataIndex in 0 until metaData.mNumProperties()){
//                val key = metaData.mKeys().get( metaDataIndex).dataString()
//                val value = metaData.mValues().get(metaDataIndex)
//
//                if( key == "UnitScaleFactor" ){
//                    val data = value.mData(1).get(0).toDouble()
//                    unitScaleFactor = data
//                } else {
//                    val data = value.mData(1).get(0).toInt()
//                    if( key == "UpAxis" ){
//                        upAxis = data
//                    } else if( key == "UpAxisSign" ){
//                        upAxisSign = data
//                    } else if( key == "FrontAxis" ){
//                        frontAxis = data
//                    } else if( key == "FrontAxisSign" ){
//                        frontAxisSign = data
//                    } else if( key == "CoordAxis" ){
//                        coordAxis = data
//                    } else if( key == "CoordAxisSign" ){
//                        coordAxisSign = data
//                    }
//                }
//            }
//            var upVec: AIVector3D = AIVector3D.calloc()
//            var forwardVec: AIVector3D = AIVector3D.calloc()
//            var rightVec: AIVector3D = AIVector3D.calloc()
//
//            if(upAxis == 0){
//                upVec.set(1.0f*upAxisSign*unitScaleFactor.toFloat(),0.0f,0.0f)
//            } else if(upAxis == 1){
//                upVec.set(0.0f,1.0f*upAxisSign*unitScaleFactor.toFloat(),0.0f)
//            } else if(upAxis == 2){
//                upVec.set(0.0f,0.0f,1.0f*upAxisSign*unitScaleFactor.toFloat())
//            }
//
//            if(frontAxis == 0){
//                forwardVec.set(1.0f*frontAxisSign*unitScaleFactor.toFloat(),0.0f,0.0f)
//            } else if(frontAxis == 1){
//                forwardVec.set(0.0f,1.0f*frontAxisSign*unitScaleFactor.toFloat(),0.0f)
//            } else if(frontAxis == 2){
//                forwardVec.set(0.0f,0.0f,1.0f*frontAxisSign*unitScaleFactor.toFloat())
//            }
//
//            if(coordAxis == 0){
//                rightVec.set(1.0f*coordAxisSign*unitScaleFactor.toFloat(),0.0f,0.0f)
//            } else if(coordAxis == 1){
//                rightVec.set(0.0f,1.0f*coordAxisSign*unitScaleFactor.toFloat(),0.0f)
//            } else if(coordAxis == 2){
//                rightVec.set(0.0f,0.0f,1.0f*coordAxisSign*unitScaleFactor.toFloat())
//            }
//
//
//
//            var mat: AIMatrix4x4 = AIMatrix4x4.calloc()
//            mat.set(
//                rightVec.x(), rightVec.y(), rightVec.z(), 0.0f,
//                upVec.x(), upVec.y(), upVec.z(), 0.0f,
//                forwardVec.x(), forwardVec.y(), forwardVec.z(), 0.0f,
//                0.0f, 0.0f, 0.0f, 1.0f,
//            )
//
//            scene.mRootNode()!!.mTransformation(mat)


            if (
                scene == null
//                or scene.mFlags() and AI_SCENE_FLAGS_INCOMPLETE or scene.mRootNode() == null) {
            ){
                // TODO: Error handling
                println("Couldn't load model")
            }
            val numMaterials: Int = scene.mNumMaterials()
            val aiMaterials: PointerBuffer = scene.mMaterials()!!
            for (i in 0 until numMaterials) {
                val aiMaterial = AIMaterial.create(aiMaterials[i])
//                processMaterial(aiMaterial, materials, texturesDir)
            }

            val aiMeshes = scene.mMeshes()!!
            val numMeshes: Int = scene.mNumMeshes()

            for (i in 0 until numMeshes) {
                val aiMesh = AIMesh.create(aiMeshes.get(i))
                val mesh = Mesh(aiMesh)
                meshes.add(mesh)
            }
        } catch(e: Exception){
            println("Couldn't load model")
        }

        return true
    }

    class Mesh(aiMesh: AIMesh){
        val posList: ArrayList<Float> = ArrayList()
        val texCoordList: ArrayList<Float> = ArrayList()
        val normalsList: ArrayList<Float> = ArrayList()
        val coloursList: ArrayList<Float> = ArrayList()
        var indicesList: ArrayList<Int> = ArrayList()
            internal set


        lateinit var vertexBuffer: VBIndexed
            private set
        val faceCount = aiMesh.mNumFaces()
        val elementCount = faceCount * 3
        val vertexCount = aiMesh.mNumVertices()

        init{
            val positionsAssimp = aiMesh.mVertices()
            for( i in 0 until positionsAssimp.limit()){
                val pos = positionsAssimp.get(i)
                posList.add(pos.x())
                posList.add(pos.y())
                posList.add(pos.z())
            }

            val texCoordsAssip = aiMesh.mTextureCoords(0)!!
            for( i in 0 until texCoordsAssip.limit()){
                val texCoord = texCoordsAssip.get(i)
                texCoordList.add(texCoord.x())
                texCoordList.add(texCoord.y())
            }

            val normalsAssimp = aiMesh.mNormals()!!
            for( i in 0 until normalsAssimp.limit()){
                val normal = normalsAssimp.get(i)
                normalsList.add(normal.x())
                normalsList.add(normal.y())
                normalsList.add(normal.z())
            }

            val facesBuffer = aiMesh.mFaces()
            for (i in 0 until faceCount) {
                val face: AIFace = facesBuffer[i]
//                check(face.mNumIndices() == 3) { "AIFace.mNumIndices() != 3" }
//                elementArrayBufferData.put(face.mIndices())
                if (face.mNumIndices() != 3){
                    println("Wrong index cnt")
                }
                for (j in 0 until face.mNumIndices()){
                    indicesList.add(face.mIndices()[j])
                }
            }

            vertexBuffer = VBIndexed(this)

//        val coloursAssimp = mesh.mColors(0)!!
//        for( i in 0 until coloursAssimp.limit()){
//            val colour = coloursAssimp.get(i)
//            coloursList.add(colour.r())
//            coloursList.add(colour.g())
//            coloursList.add(colour.b())
//            coloursList.add(colour.a())
//        }

//        colours = coloursList.toFloatArray()
        }
    }


    class Material(
        var ambient: Vec4 = Vec4(DEFAULT_COLOUR),
        var diffuse: Vec4 = Vec4(DEFAULT_COLOUR),
        var specular: Vec4 = Vec4(DEFAULT_COLOUR),
        var aaaaaaaaaaa: Float = 1.0f
    ) {
        companion object{
            val DEFAULT_COLOUR = Vec4(1,1,1,1)
        }
    }
    @Throws(java.lang.Exception::class)
    private fun processMaterial(aiMaterial: AIMaterial, materials: MutableList<Material>, texturesDir: String) {
//        val colour = AIColor4D.create()
//        val path = AIString.calloc()
//        Assimp.aiGetMaterialTexture(
//            aiMaterial,
//            aiTextureType_DIFFUSE,
//            0,
//            path,
//            null as IntBuffer?,
//            null,
//            null,
//            null,
//            null,
//            null
//        )
//        val textPath = path.dataString()
//        var texture: Texture? = null
//        if (textPath != null && textPath.length > 0) {
//            val textCache: TextureCache = TextureCache.getInstance()
//            texture = textCache.getTexture("$texturesDir/$textPath")
//        }
//        var ambient: Vec4 = Material.DEFAULT_COLOUR
//        var result: Int = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, colour)
//        if (result == 0) {
//            ambient = Vec4(colour.r(), colour.g(), colour.b(), colour.a())
//        }
//        var diffuse: Vec4 = Material.DEFAULT_COLOUR
//        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, colour)
//        if (result == 0) {
//            diffuse = Vec4(colour.r(), colour.g(), colour.b(), colour.a())
//        }
//        var specular: Vec4 = Material.DEFAULT_COLOUR
//        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, colour)
//        if (result == 0) {
//            specular = Vec4(colour.r(), colour.g(), colour.b(), colour.a())
//        }
//        val material = Material(ambient, diffuse, specular, 1.0f)
//        material.setTexture(texture)
//        materials.add(material)
    }

    fun render(
        _primitiveType: VB.PrimitiveType? = null,
        _culling: VB.CullMode? = null,
        _instanceCnt: Int = 1,
    ) {
        for (mesh in meshes) {
            mesh.vertexBuffer.render(
                _primitiveType,
                _culling,
                _instanceCnt,
            )

        }
    }
}