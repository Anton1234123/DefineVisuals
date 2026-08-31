package moscow.definevisuals.mixin.minecraft.client.network;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.impl.game.CloseScreenEvent;
import moscow.definevisuals.systems.event.impl.player.ClientPlayerTickEndEvent;
import moscow.definevisuals.systems.event.impl.player.ClientPlayerTickEvent;
import moscow.definevisuals.systems.event.impl.player.SlowDownEvent;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.mixins.ClientPlayerEntityAddition;
import moscow.definevisuals.utility.rotations.RotationHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements ClientPlayerEntityAddition, IMinecraft {
   @Unique
   private int groundTicks = 0;

   @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), require = 0)
   private boolean onIsUsingItemRedirect(ClientPlayerEntity player) {
      SlowDownEvent slowDownEvent = new SlowDownEvent();
      DefineVisuals.getInstance().getEventManager().triggerEvent(slowDownEvent);
      return player.isUsingItem() && player.getVehicle() == null && !slowDownEvent.isCancelled();
   }

   @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"))
   public boolean unpressSprintKey(boolean original) {
      return original;
   }

   @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;canSprint()Z"))
   private boolean disallowSprinting(boolean original) {
      return original;
   }

   @WrapWithCondition(
      method = "closeScreen",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V")
   )
   private boolean preventCloseScreen(MinecraftClient instance, Screen screen) {
      DefineVisuals.getInstance().getEventManager().triggerEvent(new CloseScreenEvent(screen));
      return true;
   }

   @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
   public void removePushOutFromBlocks(double x, double z, CallbackInfo ci) {
   }

   @Inject(method = "tick", at = @At("HEAD"))
   public void triggerTickEvent(CallbackInfo ci) {
      DefineVisuals.getInstance().getEventManager().triggerEvent(new ClientPlayerTickEvent());
   }

   @Inject(method = "tick", at = @At("RETURN"))
   public void triggerTickEndEvent(CallbackInfo ci) {
      DefineVisuals.getInstance().getEventManager().triggerEvent(new ClientPlayerTickEndEvent());
   }

   @Inject(method = "tickMovement", at = @At("HEAD"))
   public void updateOnGroundTicks(CallbackInfo ci) {
      if (mc.player != null && mc.player.isOnGround()) {
         this.groundTicks++;
      } else {
         this.groundTicks = 0;
      }
   }

   @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
   public float replaceMovePacketYaw(ClientPlayerEntity instance) {
      RotationHandler rotationHandler = DefineVisuals.getInstance().getRotationHandler();
      float yaw = rotationHandler.isIdling() ? instance.getYaw() : rotationHandler.getCurrentRotation().getYaw();
      rotationHandler.getServerRotation().setYaw(yaw);
      return yaw;
   }

   @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
   public float replaceMovePacketPitch(ClientPlayerEntity instance) {
      RotationHandler rotationHandler = DefineVisuals.getInstance().getRotationHandler();
      float pitch = rotationHandler.isIdling() ? instance.getPitch() : rotationHandler.getCurrentRotation().getPitch();
      rotationHandler.getServerRotation().setYaw(pitch);
      return pitch;
   }


   @Override
   public int DefineVisuals$getOnGroundTicks() {
      return this.groundTicks;
   }
}
