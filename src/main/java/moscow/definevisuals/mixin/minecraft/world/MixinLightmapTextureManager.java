package moscow.definevisuals.mixin.minecraft.world;

import com.llamalad7.mixinextras.sugar.Local;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.modules.modules.visuals.Ambience;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.LightmapTextureManager;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {
   @Shadow
   @Final
   private SimpleFramebuffer lightmapFramebuffer;

   @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/SimpleFramebuffer;endWrite()V", shift = Shift.BEFORE))
   private void onUpdate(CallbackInfo info) {
   }

   @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/SimpleFramebuffer;beginWrite(Z)V"))
   private void DefineVisuals$applyNightModeUniforms(float tickDelta, CallbackInfo info, @Local ShaderProgram shaderProgram) {
      Ambience ambience = DefineVisuals.getInstance().getModuleManager() == null
         ? null
         : DefineVisuals.getInstance().getModuleManager().getModule(Ambience.class);
      if (ambience == null || !ambience.isNightModeActive()) {
         this.setNightModeUniforms(shaderProgram, new Vector3f(1.0F, 1.0F, 1.0F), 0.0F);
         return;
      }

      this.setNightModeUniforms(shaderProgram, ambience.getNightModeTint(), ambience.getNightModeStrengthValue());
   }

   private void setNightModeUniforms(ShaderProgram shaderProgram, Vector3f tint, float strength) {
      GlUniform tintUniform = shaderProgram.getUniform("DefineVisualsNightTint");
      GlUniform strengthUniform = shaderProgram.getUniform("DefineVisualsNightStrength");
      if (tintUniform != null) {
         tintUniform.set(tint);
      }

      if (strengthUniform != null) {
         strengthUniform.set(strength);
      }
   }
}
