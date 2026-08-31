package moscow.definevisuals.mixin.minecraft.client.render.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import moscow.definevisuals.systems.modules.modules.other.SeeInvisibles;
import moscow.definevisuals.systems.modules.modules.visuals.SelfTag;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
   @Inject(method = "updateRenderState", at = @At("RETURN"), cancellable = true, require = 0)
   private void definevisuals_onUpdateRenderState(T entity, S state, float tickDelta, CallbackInfo ci) {
      if (SelfTag.isActive()) {
         MinecraftClient mc = MinecraftClient.getInstance();
         if (entity == mc.player && state.displayName == null && !mc.options.getPerspective().isFirstPerson()) {
            state.displayName = entity.getDisplayName();
            state.nameLabelPos = entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getYaw());
         }
      }
      if (SeeInvisibles.isActive()) {
         if (entity.isInvisible() && state.displayName == null) {
            state.displayName = entity.getDisplayName();
            state.nameLabelPos = entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getYaw());
         }
      }
   }
}
