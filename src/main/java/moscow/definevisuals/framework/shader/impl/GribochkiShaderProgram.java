package moscow.definevisuals.framework.shader.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import moscow.definevisuals.framework.shader.GlProgram;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class GribochkiShaderProgram extends GlProgram {
   private GlUniform timeUniform;
   private GlUniform rgbOffsetUniform;
   private GlUniform chromaticIntensityUniform;
   private GlUniform glitchIntensityUniform;
   private GlUniform pulseSpeedUniform;
   private GlUniform distortionUniform;
   private GlUniform randomUniform;
   private GlUniform vignetteIntensityUniform;
   private GlUniform scanlineIntensityUniform;
   private GlUniform fishEyeAmountUniform;
   private GlUniform pixelationAmountUniform;
   private GlUniform colorInversionUniform;
   private GlUniform noiseAmountUniform;

   public GribochkiShaderProgram(Identifier id) {
      super(id, VertexFormats.POSITION_TEXTURE_COLOR);
   }

   public void updateUniforms(float time, float rgbOffset, float chromaticIntensity, float glitchIntensity, float pulseSpeed, float distortion, float random, float vignetteIntensity, float scanlineIntensity, float fishEyeAmount, float pixelationAmount, float colorInversion, float noiseAmount) {
      if (this.timeUniform != null) {
         this.timeUniform.set(time);
      }

      if (this.rgbOffsetUniform != null) {
         this.rgbOffsetUniform.set(rgbOffset);
      }

      if (this.chromaticIntensityUniform != null) {
         this.chromaticIntensityUniform.set(chromaticIntensity);
      }

      if (this.glitchIntensityUniform != null) {
         this.glitchIntensityUniform.set(glitchIntensity);
      }

      if (this.pulseSpeedUniform != null) {
         this.pulseSpeedUniform.set(pulseSpeed);
      }

      if (this.distortionUniform != null) {
         this.distortionUniform.set(distortion);
      }

      if (this.randomUniform != null) {
         this.randomUniform.set(random);
      }

      if (this.vignetteIntensityUniform != null) {
         this.vignetteIntensityUniform.set(vignetteIntensity);
      }

      if (this.scanlineIntensityUniform != null) {
         this.scanlineIntensityUniform.set(scanlineIntensity);
      }

      if (this.fishEyeAmountUniform != null) {
         this.fishEyeAmountUniform.set(fishEyeAmount);
      }

      if (this.pixelationAmountUniform != null) {
         this.pixelationAmountUniform.set(pixelationAmount);
      }

      if (this.colorInversionUniform != null) {
         this.colorInversionUniform.set(colorInversion);
      }

      if (this.noiseAmountUniform != null) {
         this.noiseAmountUniform.set(noiseAmount);
      }
   }

   @Override
   protected void setup() {
      this.timeUniform = this.findUniform("Time");
      this.rgbOffsetUniform = this.findUniform("RgbOffset");
      this.chromaticIntensityUniform = this.findUniform("ChromaticIntensity");
      this.glitchIntensityUniform = this.findUniform("GlitchIntensity");
      this.pulseSpeedUniform = this.findUniform("PulseSpeed");
      this.distortionUniform = this.findUniform("Distortion");
      this.randomUniform = this.findUniform("Random");
      this.vignetteIntensityUniform = this.findUniform("VignetteIntensity");
      this.scanlineIntensityUniform = this.findUniform("ScanlineIntensity");
      this.fishEyeAmountUniform = this.findUniform("FishEyeAmount");
      this.pixelationAmountUniform = this.findUniform("PixelationAmount");
      this.colorInversionUniform = this.findUniform("ColorInversion");
      this.noiseAmountUniform = this.findUniform("NoiseAmount");
   }
}
