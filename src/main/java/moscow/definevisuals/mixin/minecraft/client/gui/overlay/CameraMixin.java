package moscow.definevisuals.mixin.minecraft.client.gui.overlay;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.modules.modules.player.FreeLook;
import moscow.definevisuals.systems.modules.modules.visuals.Removals;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {
   @Inject(method = "getSubmersionType", at = @At("HEAD"), cancellable = true)
   private void getSubmergedFluidState(CallbackInfoReturnable<CameraSubmersionType> ci) {
      Removals removals = DefineVisuals.getInstance().getModuleManager().getModule(Removals.class);
      if (removals.isEnabled() && removals.getWater().isSelected()) {
         ci.setReturnValue(CameraSubmersionType.NONE);
      }
   }

   @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
   private void onClipToSpace(float desiredCameraDistance, CallbackInfoReturnable<Float> info) {
      Removals removals = DefineVisuals.getInstance().getModuleManager().getModule(Removals.class);
      if (removals.getClip().isSelected() && removals.isEnabled()) {
         info.setReturnValue(desiredCameraDistance);
      }
   }

   @ModifyArg(
      method = "update",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"),
      index = 0
   )
   private float modifyCameraYaw(float yaw) {
      FreeLook freeLook = DefineVisuals.getInstance().getModuleManager().getModule(FreeLook.class);
      if (freeLook != null && freeLook.isEnabled() && freeLook.isActive()) {
         return freeLook.getCameraYaw();
      }
      return yaw;
   }

   @ModifyArg(
      method = "update",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"),
      index = 1
   )
   private float modifyCameraPitch(float pitch) {
      FreeLook freeLook = DefineVisuals.getInstance().getModuleManager().getModule(FreeLook.class);
      if (freeLook != null && freeLook.isEnabled() && freeLook.isActive()) {
         return freeLook.getCameraPitch();
      }
      return pitch;
   }

   @ModifyArg(
      method = "update",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;clipToSpace(F)F"),
      index = 0
   )
   private float modifyCameraDistance(float distance) {
      FreeLook freeLook = DefineVisuals.getInstance().getModuleManager().getModule(FreeLook.class);
      if (freeLook != null && freeLook.isEnabled() && freeLook.isActive()) {
         return freeLook.getCameraDistance();
      }
      return distance;
   }
}
