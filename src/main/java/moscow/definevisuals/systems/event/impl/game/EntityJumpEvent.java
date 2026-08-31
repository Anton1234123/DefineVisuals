package moscow.definevisuals.systems.event.impl.game;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventCancellable;
import net.minecraft.entity.LivingEntity;

public class EntityJumpEvent extends EventCancellable {
   private final LivingEntity entity;

   @Generated
   public LivingEntity getEntity() {
      return this.entity;
   }

   @Generated
   public EntityJumpEvent(LivingEntity entity) {
      this.entity = entity;
   }
}
