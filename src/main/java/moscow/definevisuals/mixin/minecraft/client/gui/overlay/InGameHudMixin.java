package moscow.definevisuals.mixin.minecraft.client.gui.overlay;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.CustomDrawContext;
import moscow.definevisuals.systems.event.impl.render.HudRenderEvent;
import moscow.definevisuals.systems.event.impl.render.PostHudRenderEvent;
import moscow.definevisuals.systems.event.impl.render.PreHudRenderEvent;
import moscow.definevisuals.systems.modules.modules.visuals.Removals;
import moscow.definevisuals.utility.game.server.ServerUtility;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.render.DrawUtility;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public class InGameHudMixin implements IMinecraft {
   @Inject(
      method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void renderScoreboardSidebarHook(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
      if (objective.getDisplayName().getString().contains("Анархия") && (ServerUtility.isFT() || ServerUtility.isST())) {
         try {
            ServerUtility.ftAn = Integer.parseInt(objective.getDisplayName().getString().split("-")[1].trim());
         } catch (Exception var5) {
         }
      }

      Removals removals = DefineVisuals.getInstance().getModuleManager().getModule(Removals.class);
      if (removals.isEnabled() && removals.getScoreboard().isSelected()) {
         ci.cancel();
      }
   }

   @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
   private void renderPortalOverlayHook(DrawContext context, float nauseaStrength, CallbackInfo ci) {
      Removals removals = DefineVisuals.getInstance().getModuleManager().getModule(Removals.class);
      if (removals.isEnabled() && removals.getPortal().isSelected()) {
         ci.cancel();
      }
   }

   @ModifyArgs(
      method = "renderMiscOverlays",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V",
         ordinal = 0
      )
   )
   private void onRenderPumpkinOverlay(Args args) {
      Removals removals = DefineVisuals.getInstance().getModuleManager().getModule(Removals.class);
      if (removals.isEnabled() && removals.getPumpkin().isSelected()) {
         args.set(2, 0.0F);
      }
   }

   @Inject(method = "render", at = @At("HEAD"))
   public void triggerPreHudRenderEvent(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      CustomDrawContext customDrawContext = CustomDrawContext.of(context);
      DefineVisuals.getInstance().getEventManager().triggerEvent(new PreHudRenderEvent(customDrawContext, tickCounter.getTickDelta(false)));
   }

   @Inject(method = "render", at = @At("RETURN"))
   public void triggerPostHudRenderEvent(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      CustomDrawContext customDrawContext = CustomDrawContext.of(context);
      DefineVisuals.getInstance().getEventManager().triggerEvent(new PostHudRenderEvent(customDrawContext, tickCounter.getTickDelta(false)));
   }

   @Inject(method = "renderMainHud", at = @At("TAIL"))
   private void triggerHudRenderEvent(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      CustomDrawContext customDrawContext = CustomDrawContext.of(context);
      if (mc.currentScreen == null) {
         DrawUtility.blurProgram.draw();
      }
      DefineVisuals.getInstance().getEventManager().triggerEvent(new HudRenderEvent(customDrawContext, tickCounter.getTickDelta(false)));
   }
}
