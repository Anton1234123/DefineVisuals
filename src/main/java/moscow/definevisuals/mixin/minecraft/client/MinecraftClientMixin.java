package moscow.definevisuals.mixin.minecraft.client;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.impl.game.GameTickEvent;
import moscow.definevisuals.systems.modules.modules.player.InputGuard;
import moscow.definevisuals.systems.modules.modules.visuals.ShaderHand;
import moscow.definevisuals.utility.render.penis.PenisAtlas;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
   @Shadow
   private int itemUseCooldown;

   @Inject(method = "tick", at = @At("HEAD"))
   public void tick(CallbackInfo ci) {
      DefineVisuals.getInstance().getEventManager().triggerEvent(new GameTickEvent());
   }

   @Inject(method = "getFramebuffer", at = @At("HEAD"), cancellable = true)
   private void onGetFramebuffer(CallbackInfoReturnable<Framebuffer> cir) {
      if (ShaderHand.rendering) {
         ShaderHand mod = DefineVisuals.getInstance().getModuleManager().getModule(ShaderHand.class);
         if (mod != null && mod.getHandsBuffer() != null) {
            cir.setReturnValue(mod.getHandsBuffer());
         }
      }
   }

   @Inject(method = "<init>", at = @At("RETURN"))
   public void endInitialize(RunArgs args, CallbackInfo ci) {
      PenisAtlas atlas = PenisAtlas.getOrCreateAtlasFor(16, 16);
      atlas.registerAnimationFromPenisFile(DefineVisuals.id("penises/combat.penis"));
      atlas.registerAnimationFromPenisFile(DefineVisuals.id("penises/movement.penis"));
      atlas.registerAnimationFromPenisFile(DefineVisuals.id("penises/visuals.penis"));
      atlas.registerAnimationFromPenisFile(DefineVisuals.id("penises/player.penis"));
      atlas.registerAnimationFromPenisFile(DefineVisuals.id("penises/other.penis"));
      atlas.registerAnimationFromPenisFile(DefineVisuals.id("penises/search.penis"));
      atlas.buildAtlas();
      PenisAtlas atlas12 = PenisAtlas.getOrCreateAtlasFor(12, 12);
      atlas12.registerAnimationFromPenisFile(DefineVisuals.id("penises/check_enable.penis"));
      atlas12.registerAnimationFromPenisFile(DefineVisuals.id("penises/check_disable.penis"));
      atlas12.buildAtlas();
   }

   @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
   public void changeWindowTitle(CallbackInfoReturnable<String> cir) {
      if (!DefineVisuals.INSTANCE.isPanic()) {
         cir.setReturnValue("Define Visuals");
      }
   }

   @Inject(method = "doItemUse", at = @At("TAIL"))
   private void applyInputGuard(CallbackInfo ci) {
      InputGuard inputGuard = DefineVisuals.getInstance().getModuleManager().getModule(InputGuard.class);
      if (inputGuard.isEnabled()) {
         this.itemUseCooldown = Math.max(this.itemUseCooldown, inputGuard.getMinimumUseDelay());
      }
   }
}
