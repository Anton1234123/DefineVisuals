package moscow.definevisuals.mixin.minecraft.client.render.entity;

import moscow.definevisuals.systems.modules.modules.visuals.SelfTag;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin implements IMinecraft {

   @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
   private void onHasLabel(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      if (entity == mc.player && SelfTag.isActive()) {
         cir.setReturnValue(true);
      }
   }
}
