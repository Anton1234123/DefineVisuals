package moscow.definevisuals.mixin.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
   @Inject(method = "handleBlockUpdate", at = @At("HEAD"))
   private void onHandleBlockUpdate(BlockPos pos, BlockState state, int flags, CallbackInfo ci) {
   }
}
