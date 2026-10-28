package moscow.definevisuals.mixin.minecraft.client.gui.screen;

import com.llamalad7.mixinextras.sugar.Local;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.impl.render.AspectRatioEvent;
import moscow.definevisuals.systems.modules.modules.visuals.AspectRatio;
import moscow.definevisuals.systems.modules.modules.visuals.Removals;
import moscow.definevisuals.utility.render.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
   @Shadow @Final private MinecraftClient client;
   @Shadow private float zoom;
   @Shadow private float zoomX;
   @Shadow private float zoomY;

   @Shadow
   public abstract float getFarPlaneDistance();

   @Inject(method = "getBasicProjectionMatrix", at = @At("HEAD"), cancellable = true)
   private void onGetBasicProjectionMatrix(float fov, CallbackInfoReturnable<Matrix4f> cir) {
      AspectRatio aspectRatioModule = DefineVisuals.getInstance().getModuleManager().getModule(AspectRatio.class);
      if (aspectRatioModule != null && aspectRatioModule.isEnabled()) {
         float defaultAspect = (float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight();
         AspectRatioEvent event = new AspectRatioEvent(defaultAspect);
         DefineVisuals.getInstance().getEventManager().triggerEvent(event);
         float aspect = event.getAspectRatio();
         Matrix4f matrix4f = new Matrix4f();
         if (this.zoom != 1.0F) {
            matrix4f.translate(this.zoomX, -this.zoomY, 0.0F);
            matrix4f.scale(this.zoom, this.zoom, 1.0F);
         }

         matrix4f.perspective(fov * (float) (Math.PI / 180.0), aspect, 0.05F, this.getFarPlaneDistance());
         cir.setReturnValue(matrix4f);
      }
   }

   @Inject(
      method = "renderWorld",
      at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=hand")
   )
   private void onRenderWorld(
      RenderTickCounter tickCounter,
      CallbackInfo ci,
      @Local(ordinal = 0) Matrix4f projection,
      @Local(ordinal = 2) Matrix4f view,
      @Local(ordinal = 1) float tickDelta,
      @Local MatrixStack matrices
   ) {
      Utils.onRender(view, projection);
   }

   @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
   private void tiltViewWhenHurtHook(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
      Removals removals = DefineVisuals.getInstance().getModuleManager().getModule(Removals.class);
      if (removals.isEnabled() && removals.getHurtCam().isSelected()) {
         ci.cancel();
      }
   }

   @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
   private float renderWorldHook(float delta, float first, float second) {
      Removals removals = DefineVisuals.getInstance().getModuleManager().getModule(Removals.class);
      return removals.isEnabled() && removals.getNausea().isSelected() ? 0.0F : MathHelper.lerp(delta, first, second);
   }
}
