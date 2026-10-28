package pulse.cosmetic;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import ru.pulse.cosmetic.render.CosmeticRenderer;

public class CosmeticFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
   public CosmeticFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
      super(context);
   }

   @Override
   public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float tickDelta) {
      MinecraftClient client = MinecraftClient.getInstance();
      AbstractClientPlayerEntity player = client.player;
      if (!moscow.definevisuals.systems.modules.modules.visuals.Cosmetics.isModelsEnabled()) return;
      if (player == null || vertexConsumers == null || state == null || state.spectator || state.id != player.getId()) {
         return;
      }

      List<Integer> selected = LocalCosmetics.selectedIndices();
      if (selected.isEmpty()) {
         return;
      }

      for (Integer selectedIndex : selected) {
         if (selectedIndex == null || "cape".equals(LocalCosmetics.type(selectedIndex))) {
            continue;
         }

         ru.pulse.cosmetic.model.CosmeticModel model = LocalCosmetics.modelFor(selectedIndex);
         if (model == null || model.getTextureId() == null) {
            continue;
         }

         RenderLayer layer = RenderLayer.getEntityCutoutNoCull(model.getTextureId());
         VertexConsumer vertexConsumer = vertexConsumers.getBuffer(layer);
         CosmeticRenderer.getInstance().renderCosmetic(model, player, matrices, vertexConsumer, light, this.getContextModel(), tickDelta);
      }
   }
}