package moscow.definevisuals.framework.shader.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ProjectionType;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.mixin.accessors.FramebufferAccessor;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.interfaces.IWindow;
import moscow.definevisuals.utility.render.CustomRenderTarget;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class FogBlurPost implements IMinecraft, IWindow {
   private static final float NEAR_PLANE = 0.05F;
   private static final float DOWNSCALE = 0.5F;

   private final CustomRenderTarget sharp = new CustomRenderTarget(false).setLinear();
   private final CustomRenderTarget blurA = new CustomRenderTarget(false).setDownscale(DOWNSCALE).setLinear();
   private final CustomRenderTarget blurB = new CustomRenderTarget(false).setDownscale(DOWNSCALE).setLinear();
   private KawaseBlurProgram kawaseDown;
   private KawaseBlurProgram kawaseUp;
   private FogBlurProgram composite;
   private int frameWidth;
   private int frameHeight;

   public void initShaders() {
      this.kawaseDown = new KawaseBlurProgram(DefineVisuals.id("kawase_down/data"));
      this.kawaseUp = new KawaseBlurProgram(DefineVisuals.id("kawase_up/data"));
      this.composite = new FogBlurProgram(DefineVisuals.id("fog_blur/data"));
   }

   public void apply(float fogStart, float fogEnd, float blurStrength, int passes, float blurOffset, float blurFloor, boolean skipSky) {
      Framebuffer main = mc.getFramebuffer();
      int depthTexture = main == null ? 0 : ((FramebufferAccessor)main).getDepthAttachment();
      if (this.kawaseDown == null || this.kawaseUp == null || this.composite == null || depthTexture <= 0) {
         return;
      }

      if (main == null || main.getColorAttachment() <= 0 || main.textureWidth <= 0 || main.textureHeight <= 0) {
         return;
      }

      this.frameWidth = main.textureWidth;
      this.frameHeight = main.textureHeight;
      // this.sharp.setFixedSize(this.frameWidth, this.frameHeight);
      // this.blurA.setFixedSize(this.frameWidth, this.frameHeight);
      // this.blurB.setFixedSize(this.frameWidth, this.frameHeight);

      RenderSystem.backupProjectionMatrix();
      Matrix4fStack modelView = RenderSystem.getModelViewStack();
      modelView.pushMatrix();
      modelView.translation(0.0F, 0.0F, -11000.0F);
      RenderSystem.setProjectionMatrix(
         new Matrix4f().setOrtho(0.0F, this.frameWidth, this.frameHeight, 0.0F, 1000.0F, 21000.0F),
         ProjectionType.ORTHOGRAPHIC
      );

      try {
         this.copyMainColor(main);
         int blurredTexture = this.buildBlur(Math.max(1.0F, blurOffset), Math.max(1, passes));
         this.composite(main, depthTexture, blurredTexture, fogStart, fogEnd, Math.max(this.computeFarPlane(), fogEnd + 16.0F), blurStrength, blurFloor, skipSky);
      } finally {
         modelView.popMatrix();
         RenderSystem.restoreProjectionMatrix();
         main.beginWrite(true);
      }
   }

   private void copyMainColor(Framebuffer main) {
      this.sharp.setDownscale(1.0F).setLinear();
      this.setupTarget(this.sharp);
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.disableBlend();
      RenderSystem.setShader(net.minecraft.client.gl.ShaderProgramKeys.POSITION_TEX_COLOR);
      main.beginRead();
      RenderSystem.setShaderTexture(0, main.getColorAttachment());
      this.drawFullscreenQuad();
      main.endRead();
      RenderSystem.setShaderTexture(0, 0);
      this.sharp.stop();
   }

   private int buildBlur(float offset, int passes) {
      this.blurA.setDownscale(DOWNSCALE).setLinear();
      this.blurB.setDownscale(DOWNSCALE).setLinear();
      CustomRenderTarget[] buffers = new CustomRenderTarget[]{this.blurA, this.blurB};
      Framebuffer source = this.sharp;
      int steps = Math.min(passes, 16);

      this.kawaseDown.use();
      for (int i = 0; i < steps; i++) {
         CustomRenderTarget target = buffers[i % buffers.length];
         this.setupTarget(target);
         source.beginRead();
         RenderSystem.setShaderTexture(0, source.getColorAttachment());
         this.kawaseDown.updateUniforms(offset, source.textureWidth, source.textureHeight);
         this.drawFullscreenQuad();
         source.endRead();
         target.stop();
         source = target;
      }

      this.kawaseUp.use();
      for (int i = 0; i < steps; i++) {
         CustomRenderTarget target = source == this.blurA ? this.blurB : this.blurA;
         this.setupTarget(target);
         source.beginRead();
         RenderSystem.setShaderTexture(0, source.getColorAttachment());
         this.kawaseUp.updateUniforms(offset, source.textureWidth, source.textureHeight);
         this.drawFullscreenQuad();
         source.endRead();
         target.stop();
         source = target;
      }

      RenderSystem.setShaderTexture(0, 0);
      return source.getColorAttachment();
   }

   private void setupTarget(CustomRenderTarget target) {
      target.setup();
      target.beginWrite(true);
   }

   private float computeFarPlane() {
      return mc.gameRenderer == null ? 1024.0F : Math.max(64.0F, mc.gameRenderer.getFarPlaneDistance());
   }

   private void composite(
      Framebuffer main,
      int depthTexture,
      int blurredTexture,
      float fogStart,
      float fogEnd,
      float farPlane,
      float blurStrength,
      float blurOffset,
      boolean skipSky
   ) {
      main.beginWrite(true);
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.disableBlend();
      this.composite.use();
      this.composite.updateUniforms(fogStart, fogEnd, NEAR_PLANE, farPlane, blurStrength, Math.max(0.0F, blurOffset), skipSky);
      RenderSystem.setShaderTexture(0, this.sharp.getColorAttachment());
      RenderSystem.setShaderTexture(1, blurredTexture);
      RenderSystem.setShaderTexture(2, depthTexture);
      this.drawFullscreenQuad();
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.setShaderTexture(1, 0);
      RenderSystem.setShaderTexture(2, 0);
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
   }

   private void drawFullscreenQuad() {
      this.drawQuad(0.0F, 0.0F, this.frameWidth, this.frameHeight);
   }

   private void drawQuad(float x, float y, float width, float height) {
      BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      builder.vertex(x, y, 0.0F).texture(0.0F, 1.0F).color(-1);
      builder.vertex(x, y + height, 0.0F).texture(0.0F, 0.0F).color(-1);
      builder.vertex(x + width, y + height, 0.0F).texture(1.0F, 0.0F).color(-1);
      builder.vertex(x + width, y, 0.0F).texture(1.0F, 1.0F).color(-1);
      BufferRenderer.drawWithGlobalProgram(builder.end());
   }
}
