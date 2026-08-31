package moscow.definevisuals.mixin.minecraft.client.gui.hud;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.modules.modules.other.BetterTab;
import net.minecraft.client.gui.hud.PlayerListHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
   @Inject(
      method = "getMaxPlayerCount",
      at = @At("HEAD"),
      cancellable = true,
      require = 0
   )
   private void onGetMaxPlayerCount(CallbackInfoReturnable<Integer> cir) {
      BetterTab betterTab = DefineVisuals.getInstance().getModuleManager().getModule(BetterTab.class);
      if (betterTab != null && betterTab.isEnabled()) {
         cir.setReturnValue(80);
      }
   }
}
