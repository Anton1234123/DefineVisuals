package moscow.definevisuals.mixin.minecraft.client.render;

import moscow.definevisuals.systems.modules.modules.other.Optimization;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin_Optimization {

   @Inject(method = "addParticle", at = @At("HEAD"), cancellable = true)
   private void onAddParticle(
      net.minecraft.particle.ParticleEffect parameters, 
      double x, double y, double z, 
      double velocityX, double velocityY, double velocityZ, 
      CallbackInfoReturnable<Particle> cir
   ) {
      if (Optimization.isActive() && !Optimization.allowParticle(x, y, z)) {
         cir.setReturnValue(null);
      }
   }
}
