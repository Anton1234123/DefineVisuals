package moscow.definevisuals.mixin.minecraft.render.entity;

import moscow.definevisuals.utility.mixins.EntityRenderStateAddition;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements EntityRenderStateAddition {
   @Unique
   private Entity DefineVisuals$entity;

   @Unique
   @Override
   public void DefineVisuals$setEntity(Entity entity) {
      this.DefineVisuals$entity = entity;
   }

   @Unique
   @Override
   public Entity DefineVisuals$getEntity() {
      return this.DefineVisuals$entity;
   }
}
