package ru.pulse.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.CapeResolver;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {
   @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
   private void pulse$useCustomCapeTexture(CallbackInfoReturnable<SkinTextures> var1) {
      AbstractClientPlayerEntity var2 = (AbstractClientPlayerEntity)(Object)this;
      if (!moscow.definevisuals.systems.modules.modules.visuals.Cosmetics.isCapesEnabled()) return;
      if (CapeResolver.b(var2)) {
         Identifier var3 = CapeResolver.a(var2);
         if (var3 != null) {
            SkinTextures var4 = (SkinTextures)var1.getReturnValue();
            SkinTextures var5 = new SkinTextures(var4.texture(), var4.textureUrl(), var3, var4.elytraTexture(), var4.model(), var4.secure());
            var1.setReturnValue(var5);
         }
      }
   }
}
