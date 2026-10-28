package moscow.definevisuals.mixin.minecraft.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.rotations.MoveCorrection;
import moscow.definevisuals.utility.rotations.RotationHandler;
import moscow.definevisuals.utility.rotations.RotationTask;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import moscow.definevisuals.systems.modules.modules.player.FreeLook;

@Mixin(Entity.class)
public class EntityMixin implements IMinecraft {
   @Shadow
   private Box boundingBox;

   @ModifyExpressionValue(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isControlledByPlayer()Z"))
   public boolean fixFalldistanceValue(boolean original) {
      return (Object) this == mc.player ? false : original;
   }


   @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getYaw()F"))
   public float movementCorrection(Entity instance) {
      RotationHandler rotationHandler = DefineVisuals.INSTANCE.getRotationHandler();
      RotationTask currentTask = rotationHandler.getCurrentTask();
      return currentTask != null && currentTask.getMoveCorrection() != MoveCorrection.NONE
            && instance instanceof ClientPlayerEntity
                  ? rotationHandler.getCurrentRotation().getYaw()
                  : instance.getYaw();
   }

   @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
   public void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
      if (mc.player != null && (Object) this == mc.player) {
         FreeLook freeLook = DefineVisuals.getInstance().getModuleManager().getModule(FreeLook.class);
         if (freeLook != null && freeLook.isEnabled() && freeLook.isActive()) {
            freeLook.updateCameraAngles(cursorDeltaX, cursorDeltaY);
            ci.cancel();
         }
      }
   }
}
