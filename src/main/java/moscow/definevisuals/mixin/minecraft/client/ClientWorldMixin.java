package moscow.definevisuals.mixin.minecraft.client;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.modules.modules.visuals.Ambience;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.DimensionEffects.End;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
   @Unique
   private final DimensionEffects endSky = new End();

   @Inject(method = "getDimensionEffects", at = @At("HEAD"), cancellable = true)
   private void onGetSkyProperties(CallbackInfoReturnable<DimensionEffects> info) {
      if (DefineVisuals.getInstance().getModuleManager().getModule(Ambience.class).isEnabled()
         && DefineVisuals.getInstance().getModuleManager().getModule(Ambience.class).getEndSky().isEnabled()) {
         info.setReturnValue(this.endSky);
      }
   }
}
