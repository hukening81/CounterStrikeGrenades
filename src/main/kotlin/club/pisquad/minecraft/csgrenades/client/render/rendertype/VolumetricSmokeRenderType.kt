package club.pisquad.minecraft.csgrenades.client.render.rendertype

import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.RenderType

//String pName,
// VertexFormat pFormat,
// VertexFormat.Mode pMode,
// int pBufferSize,
// boolean pAffectsCrumbling,
// boolean pSortOnUpload,
// Runnable pSetupState,
// Runnable pClearState
class VolumetricSmokeRenderType(
    name: String,
    vertexFormat: VertexFormat,
    vertexMode: VertexFormat.Mode,
    bufferSize: Int,
    affectsCrumbling: Boolean,
    sortOnUpload: Boolean,
    setupState: Runnable,
    clearState: Runnable
) : RenderType(name, vertexFormat, vertexMode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState) {
    init {
        throw NotImplementedError("This class is not meant to be constructed!")
    }

//    companion object {
//        val name: String = "${CounterStrikeGrenades.ID}_${VolumetricSmokeRenderType::class.simpleName}"
//        fun get(): VolumetricSmokeRenderType {
//            CompositeState.builder().createCompositeState(false)
//            create(name)
//            TODO()
//        }
//    }
}
//val shaderInstance: ShaderInstance
// }