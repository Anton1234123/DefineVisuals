package moscow.definevisuals.framework.shader.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import moscow.definevisuals.framework.shader.GlProgram;
import moscow.definevisuals.utility.colors.ColorRGBA;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class SkyShaderProgram extends GlProgram {
   private GlUniform timeUniform;
   private GlUniform accentUniform;
   private GlUniform modelViewUniform;
   private GlUniform projMatUniform;

   public SkyShaderProgram(Identifier id) {
      super(id, VertexFormats.POSITION_TEXTURE_COLOR);
   }

   public void updateUniforms(float time, ColorRGBA accent) {
      // ИСПРАВЛЕНО: передаём ModelViewMat и ProjMat из RenderSystem
      if (this.modelViewUniform != null) {
         this.modelViewUniform.set(RenderSystem.getModelViewMatrix());
      }

      if (this.projMatUniform != null) {
         this.projMatUniform.set(RenderSystem.getProjectionMatrix());
      }

      if (this.timeUniform != null) {
         this.timeUniform.set(time);
      }

      if (this.accentUniform != null && accent != null) {
         this.accentUniform.set(accent.getRed() / 255.0F, accent.getGreen() / 255.0F, accent.getBlue() / 255.0F);
      }
   }

   @Override
   protected void setup() {
      this.modelViewUniform = this.findUniform("ModelViewMat");
      this.projMatUniform   = this.findUniform("ProjMat");
      this.timeUniform      = this.findUniform("Time");
      this.accentUniform    = this.findUniform("Accent");
   }
}
