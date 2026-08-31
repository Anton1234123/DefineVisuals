package moscow.definevisuals.systems.event.impl.game;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventCancellable;
import net.minecraft.util.math.BlockPos;

public class StartBreakBlockEvent extends EventCancellable {
   private final BlockPos blockPos;

   public StartBreakBlockEvent(BlockPos blockPos) {
      this.blockPos = blockPos;
   }

   @Generated
   public BlockPos getBlockPos() {
      return this.blockPos;
   }
}
