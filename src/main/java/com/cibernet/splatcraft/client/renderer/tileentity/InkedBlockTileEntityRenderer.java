package com.cibernet.splatcraft.client.renderer.tileentity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;

public class InkedBlockTileEntityRenderer extends TileEntityRenderer<InkedBlockTileEntity>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "blocks/inked_block");
    public static final ResourceLocation TEXTURE_GLOWING = new ResourceLocation(Splatcraft.MODID, "blocks/glitter");
    public static final ResourceLocation TEXTURE_PERMANENT = new ResourceLocation(Splatcraft.MODID, "blocks/permanent_ink_overlay");

    protected static InkBlockUtils.InkType type;

    public InkedBlockTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    private static void renderBlock(InkedBlockTileEntity te, BlockRendererDispatcher blockRendererDispatcher, MatrixStack matrixStackIn, IRenderTypeBuffer bufferTypeIn, int combinedLightIn, int combinedOverlayIn)
    {
        type = InkBlockUtils.getInkType(te.getBlockState());
        BlockState blockStateIn = te.getBlockState();
        BlockRenderType blockrendertype = te.getSavedState().getRenderType();
        if (blockrendertype.equals(BlockRenderType.MODEL))
        {
            blockStateIn = te.getSavedState();
        }

        IBakedModel ibakedmodel = blockRendererDispatcher.getModelForState(blockStateIn);


        int i = ColorUtils.getInkColor(te);
        float f = (float) (i >> 16 & 255) / 255.0F;
        float f1 = (float) (i >> 8 & 255) / 255.0F;
        float f2 = (float) (i & 255) / 255.0F;

        //f = 0;
        //f1 = 1;
        //f2 = 1;

        renderModel(matrixStackIn.getLast(), bufferTypeIn, blockStateIn, ibakedmodel, f, f1, f2, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE, te);

    }

    private static void renderModel(MatrixStack.Entry matrixEntry, IRenderTypeBuffer buffer, @Nullable BlockState state, IBakedModel modelIn, float red, float green, float blue, int combinedLightIn, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData, InkedBlockTileEntity te)
    {
        Random random = new Random();
        long i = 42L;

        for (Direction direction : Direction.values())
        {
            random.setSeed(42L);
            renderModelBrightnessColorQuads(matrixEntry, buffer, state, red, green, blue, modelIn.getQuads(state, direction, random, modelData), combinedLightIn, combinedOverlayIn, te);
        }

        random.setSeed(42L);
        renderModelBrightnessColorQuads(matrixEntry, buffer, state, red, green, blue, modelIn.getQuads(state, null, random, modelData), combinedLightIn, combinedOverlayIn, te);
    }

    private static void renderModelBrightnessColorQuads(MatrixStack.Entry matrixEntry, IRenderTypeBuffer buffer, BlockState state, float red, float green, float blue, List<BakedQuad> quads, int combinedLightIn, int combinedOverlayIn, InkedBlockTileEntity te)
    {
        IVertexBuilder builder =  type.equals(InkBlockUtils.InkType.GLOWING) ? buffer.getBuffer(RenderType.getTranslucent()) : buffer.getBuffer(RenderTypeLookup.func_239220_a_(state, false));

        for (BakedQuad bakedquad : quads)
        {
            float f = MathHelper.clamp(red, 0.0F, 1.0F);
            float f1 = MathHelper.clamp(green, 0.0F, 1.0F);
            float f2 = MathHelper.clamp(blue, 0.0F, 1.0F);

            if(type.equals(InkBlockUtils.InkType.CLEAR))
                builder.addQuad(matrixEntry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
            else
            {
                addQuad(builder, Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE), matrixEntry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
                if (type.equals(InkBlockUtils.InkType.GLOWING))
                    addQuad(builder, Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE_GLOWING), matrixEntry, bakedquad, 1, 1, 1, combinedLightIn, combinedOverlayIn);
            }

            if(Minecraft.getInstance().gameSettings.showDebugInfo && te.getColor() == te.getPermanentColor())
                addQuad(builder, Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE_PERMANENT), matrixEntry, bakedquad, 1, 1, 1, combinedLightIn, combinedOverlayIn);
        }

    }


    private static void addQuad(IVertexBuilder bufferIn, TextureAtlasSprite sprite, MatrixStack.Entry matrixEntryIn, BakedQuad quadIn, float redIn, float greenIn, float blueIn, int combinedLightIn, int combinedOverlayIn)
    {
        float[] colorMuls = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
        int[] combinedLights = new int[]{combinedLightIn, combinedLightIn, combinedLightIn, combinedLightIn};
        boolean mulColor = false;

        int[] aint = quadIn.getVertexData();
        Vector3i vector3i = quadIn.getFace().getDirectionVec();
        Vector3f vector3f = new Vector3f((float) vector3i.getX(), (float) vector3i.getY(), (float) vector3i.getZ());
        Matrix4f matrix4f = matrixEntryIn.getMatrix();
        vector3f.transform(matrixEntryIn.getNormal());
        int j = aint.length / 8;

        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormats.BLOCK.getSize());
            IntBuffer intbuffer = bytebuffer.asIntBuffer();

            VertexData[] vertexArray = new VertexData[j];

            for (int k = 0; k < j; ++k)
            {
                ((Buffer) intbuffer).clear();
                intbuffer.put(aint, k * 8, 8);
                float f = bytebuffer.getFloat(0);
                float f1 = bytebuffer.getFloat(4);
                float f2 = bytebuffer.getFloat(8);
                float f3;
                float f4;
                float f5;
                if (mulColor) {
                    float f6 = (float) (bytebuffer.get(12) & 255) / 255.0F;
                    float f7 = (float) (bytebuffer.get(13) & 255) / 255.0F;
                    float f8 = (float) (bytebuffer.get(14) & 255) / 255.0F;
                    f3 = f6 * colorMuls[k] * redIn;
                    f4 = f7 * colorMuls[k] * greenIn;
                    f5 = f8 * colorMuls[k] * blueIn;
                } else {
                    f3 = colorMuls[k] * redIn;
                    f4 = colorMuls[k] * greenIn;
                    f5 = colorMuls[k] * blueIn;
                }

                int l = bufferIn.applyBakedLighting(combinedLights[k], bytebuffer);

                Vector4f vector4f = new Vector4f(f, f1, f2, 1.0F);

                vertexArray[k] = new VertexData(vector4f, f3, f4, f5, 1.0F, l, vector3f.getX(), vector3f.getY(), vector3f.getZ());

            }

            if (vertexArray.length <= 0)
                return;

            boolean matchesX = true;
            boolean matchesY = true;
            Direction.Axis axis;

            for(int i = 0; i < vertexArray.length-1; i++)
            {
                if(matchesX && vertexArray[i].pos.getX() != vertexArray[i+1].pos.getX())
                    matchesX = false;
                if(matchesY && vertexArray[i].pos.getY() != vertexArray[i+1].pos.getY())
                    matchesY = false;
            }

            if(matchesX)
                axis = Direction.Axis.X;
            else if(matchesY)
                axis = Direction.Axis.Y;
            else axis = Direction.Axis.Z;

            for (int k = 0; k < j; ++k)
            {
                VertexData vertex = vertexArray[k];

                float texU = sprite.getMinU() + (axis.equals(Direction.Axis.X) ? vertex.pos.getZ() : vertex.pos.getX())*(sprite.getMaxU()-sprite.getMinU());
                float texV = sprite.getMinV() + (axis.equals(Direction.Axis.Y) ? vertex.pos.getZ() : vertex.pos.getY())*(sprite.getMaxV()-sprite.getMinV());

                vertex.pos.transform(matrix4f);
                bufferIn.applyBakedNormals(vertex.normal, bytebuffer, matrixEntryIn.getNormal());
                bufferIn.addVertex(vertex.pos.getX(), vertex.pos.getY(), vertex.pos.getZ(), vertex.rgba.getX(), vertex.rgba.getY(), vertex.rgba.getZ(), vertex.rgba.getW(), texU, texV, combinedOverlayIn, vertex.lightmapUV, vertex.normal.getX(), vertex.normal.getY(), vertex.normal.getZ());
            }

        }
    }

    private static final class VertexData
    {
        final Vector4f pos;
        final Vector4f rgba;
        final int lightmapUV;
        final Vector3f normal;

        VertexData(Vector4f pos, float red, float green, float blue, float alpha, int lightmapUV, float normalX, float normalY, float normalZ)
        {
            this.pos = pos;
            rgba = new Vector4f(red, green, blue, alpha);
            this.lightmapUV = lightmapUV;
            normal = new Vector3f(normalX, normalY, normalZ);
        }
    }

    @Override
    public void render(InkedBlockTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
        renderBlock(tileEntityIn, blockRenderer, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        //blockRenderer.renderBlock(SplatcraftBlocks.sardiniumBlock.getDefaultState(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        //blockRenderer.renderBlock(tileEntityIn.getSavedState(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }


}