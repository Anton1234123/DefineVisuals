package moscow.definevisuals.mixin.cosmetics;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import moscow.definevisuals.cosmetics.CosmeticFeatureRenderer;

@Mixin(PlayerEntityRenderer.class)
public abstract class CosmeticRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {
   public CosmeticRendererMixin(Context var1, PlayerEntityModel var2, float var3) {
      super(var1, var2, var3);
   }

   @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Z)V", at = @At("RETURN"))
   private void onInit(Context var1, boolean var2, CallbackInfo var3) {
      this.addFeature(new CosmeticFeatureRenderer(this));
   }
}
