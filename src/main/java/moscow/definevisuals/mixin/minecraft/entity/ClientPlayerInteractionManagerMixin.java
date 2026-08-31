package moscow.definevisuals.mixin.minecraft.entity;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.impl.game.BlockBreakEvent;
import moscow.definevisuals.systems.event.impl.game.InternalAttackEvent;
import moscow.definevisuals.systems.event.impl.game.StartBreakBlockEvent;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
   @Shadow
   @Final
   private MinecraftClient client;

   @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
   private void DefineVisuals$critPre(PlayerEntity player, Entity target, CallbackInfo ci) {
      InternalAttackEvent event = new InternalAttackEvent(target);
      DefineVisuals.getInstance().getEventManager().triggerEvent(event);
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(method = "breakBlock", at = @At("RETURN"), cancellable = true)
   public void breakBlockHook(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
      BlockBreakEvent event = new BlockBreakEvent(pos);
      DefineVisuals.getInstance().getEventManager().triggerEvent(event);
      if (event.isCancelled()) {
         cir.setReturnValue(false);
      }
   }

   @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
   private void onAttackBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> info) {
      StartBreakBlockEvent event = new StartBreakBlockEvent(blockPos);
      DefineVisuals.getInstance().getEventManager().triggerEvent(event);
      if (event.isCancelled()) {
         info.cancel();
      }
   }
}
