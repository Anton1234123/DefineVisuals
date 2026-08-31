package moscow.definevisuals.systems.modules.modules.visuals;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.shader.impl.GribochkiShaderProgram;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.HudRenderEvent;
import moscow.definevisuals.systems.event.impl.render.PostHudRenderEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.utility.render.DrawUtility;
import moscow.definevisuals.utility.sounds.ClientSoundInstance;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Random;

@ModuleInfo(name = "Gribochki", category = ModuleCategory.VISUALS, desc = "ща грибов поедим")
public class Gribochki extends BaseModule {
   private final Random random = new Random();
   private long time = 0;
   private float shakeIntensity = 0.0f;
   private float rgbOffset = 0.0f;
   private float distortionAmount = 0.0f;
   private float chromaticIntensity = 0.0f;
   private float glitchIntensity = 0.0f;
   private float pulseSpeed = 0.0f;
   private float vignetteIntensity = 0.0f;
   private float scanlineIntensity = 0.0f;
   private float fishEyeAmount = 0.0f;
   private float pixelationAmount = 0.0f;
   private float colorInversion = 0.0f;
   private float noiseAmount = 0.0f;
   
   private final ClientSoundInstance track1 = new ClientSoundInstance("gribochki/track1", 1.0f);
   private final ClientSoundInstance track2 = new ClientSoundInstance("gribochki/track2", 1.0f);
   private final ClientSoundInstance track3 = new ClientSoundInstance("gribochki/track3", 1.0f);
   
   private final EventListener<HudRenderEvent> onRender = event -> {
      if (!this.isEnabled()) return;
      
      time += event.getTickDelta() * 16;
      shakeIntensity = 30.0f + (float) Math.sin(time * 0.015) * 15.0f;
      rgbOffset = 25.0f + (float) Math.sin(time * 0.025) * 12.0f;
      distortionAmount = 0.7f + (float) Math.sin(time * 0.008) * 0.4f;
      chromaticIntensity = 0.8f + (float) Math.sin(time * 0.02) * 0.5f;
      glitchIntensity = 0.6f + (float) Math.sin(time * 0.01) * 0.3f;
      pulseSpeed = 2.0f + (float) Math.sin(time * 0.005) * 1.5f;
      vignetteIntensity = 0.5f + (float) Math.sin(time * 0.012) * 0.3f;
      scanlineIntensity = 0.4f + (float) Math.sin(time * 0.018) * 0.2f;
      fishEyeAmount = 0.3f + (float) Math.sin(time * 0.007) * 0.15f;
      pixelationAmount = 0.2f + (float) Math.sin(time * 0.022) * 0.1f;
      colorInversion = 0.15f + (float) Math.sin(time * 0.014) * 0.1f;
      noiseAmount = 0.25f + (float) Math.sin(time * 0.009) * 0.15f;
      
      MatrixStack matrices = event.getContext().getMatrices();
      matrices.push();
      
      float shakeX = (random.nextFloat() - 0.5f) * shakeIntensity * 3;
      float shakeY = (random.nextFloat() - 0.5f) * shakeIntensity * 3;
      float shakeZ = (random.nextFloat() - 0.5f) * shakeIntensity * 0.8f;
      
      matrices.translate(shakeX, shakeY, shakeZ);
      
      float rotation = (float) Math.sin(time * 0.04) * 8.0f + (float) Math.cos(time * 0.03) * 5.0f;
      matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
      
      float scale = 1.0f + (float) Math.sin(time * 0.015) * 0.08f + (float) Math.cos(time * 0.01) * 0.05f;
      matrices.scale(scale, scale, scale);
      
      // Additional screen warping
      float warpX = (float) Math.sin(time * 0.03) * 5.0f;
      float warpY = (float) Math.cos(time * 0.025) * 5.0f;
      matrices.translate(warpX, warpY, 0);
      
      matrices.pop();
   };
   
   private final EventListener<PostHudRenderEvent> onPostRender = event -> {
      if (!this.isEnabled()) return;
      
      // GribochkiShaderProgram shader = DrawUtility.gribochkiProgram;
      // if (shader != null) {
      //    Framebuffer main = mc.getFramebuffer();
      //    if (main != null && main.getColorAttachment() > 0) {
      //       RenderSystem.setShaderTexture(0, main.getColorAttachment());
      //       shader.use();
      //       shader.updateUniforms(
      //          time * 0.001f,
      //          rgbOffset,
      //          chromaticIntensity,
      //          glitchIntensity,
      //          pulseSpeed,
      //          distortionAmount,
      //          random.nextFloat(),
      //          vignetteIntensity,
      //          scanlineIntensity,
      //          fishEyeAmount,
      //          pixelationAmount,
      //          colorInversion,
      //          noiseAmount
      //       );
      //       DrawUtility.drawFullscreenQuad();
      //       RenderSystem.setShaderTexture(0, 0);
      //    }
      // }
   };

   @Override
   public void onEnable() {
      super.onEnable();
      time = 0;
      track1.play(2.5f, 1.2f + random.nextFloat() * 0.6f);
      track2.play(2.0f, 0.9f + random.nextFloat() * 0.5f);
      track3.play(2.2f, 1.4f + random.nextFloat() * 0.4f);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      track1.stop();
      track2.stop();
      track3.stop();
      shakeIntensity = 0.0f;
      rgbOffset = 0.0f;
      distortionAmount = 0.0f;
      chromaticIntensity = 0.0f;
      glitchIntensity = 0.0f;
      pulseSpeed = 0.0f;
      vignetteIntensity = 0.0f;
      scanlineIntensity = 0.0f;
      fishEyeAmount = 0.0f;
      pixelationAmount = 0.0f;
      colorInversion = 0.0f;
      noiseAmount = 0.0f;
   }

   @Generated
   public EventListener<HudRenderEvent> getOnRender() {
      return this.onRender;
   }

   @Generated
   public EventListener<PostHudRenderEvent> getOnPostRender() {
      return this.onPostRender;
   }

   @Generated
   public float getShakeIntensity() {
      return this.shakeIntensity;
   }

   @Generated
   public float getRgbOffset() {
      return this.rgbOffset;
   }

   @Generated
   public float getDistortionAmount() {
      return this.distortionAmount;
   }

   @Generated
   public float getChromaticIntensity() {
      return this.chromaticIntensity;
   }

   @Generated
   public float getGlitchIntensity() {
      return this.glitchIntensity;
   }

   @Generated
   public float getPulseSpeed() {
      return this.pulseSpeed;
   }

   @Generated
   public float getVignetteIntensity() {
      return this.vignetteIntensity;
   }

   @Generated
   public float getScanlineIntensity() {
      return this.scanlineIntensity;
   }

   @Generated
   public float getFishEyeAmount() {
      return this.fishEyeAmount;
   }

   @Generated
   public float getPixelationAmount() {
      return this.pixelationAmount;
   }

   @Generated
   public float getColorInversion() {
      return this.colorInversion;
   }

   @Generated
   public float getNoiseAmount() {
      return this.noiseAmount;
   }
}
