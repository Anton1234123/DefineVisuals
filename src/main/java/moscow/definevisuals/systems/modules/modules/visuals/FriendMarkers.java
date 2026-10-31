package moscow.definevisuals.systems.modules.modules.visuals;

import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.Render3DEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.render.CrystalRenderer;
import moscow.definevisuals.utility.render.RenderUtility;
import moscow.definevisuals.utility.render.Utils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
@ModuleInfo(name = "Friend Markers", desc = "Выделяет друзей", category = ModuleCategory.VISUALS)
public class FriendMarkers extends BaseModule {
   private final ModeSetting setting = new ModeSetting(this, "modules.settings.friends_markers.setting");
   private final ModeSetting.Value heads = new ModeSetting.Value(this.setting, "modules.settings.friends_markers.heads");
   private final ModeSetting.Value sims = new ModeSetting.Value(this.setting, "Sims");
   private final EventListener<Render3DEvent> onRender3D = event -> {
      if (this.sims.isSelected()) {
         RenderUtility.setupRender3D(true);
         MatrixStack ms = event.getMatrices();
         Camera camera = mc.gameRenderer.getCamera();
         Vec3d cameraPos = camera.getPos();
         ColorRGBA color = new ColorRGBA(52.0F, 199.0F, 89.0F);
         BufferBuilder builder = CrystalRenderer.createBuffer();

         for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
            if (DefineVisuals.getInstance().getFriendManager().isFriend(player.getName().getString())) {
               ms.push();
               RenderUtility.prepareMatrices(ms, Utils.getInterpolatedPos(player, event.getTickDelta()));
               float size = 0.1F;
               CrystalRenderer.render(ms, builder, 0.0F, player.getHeight() + 0.4F, 0.0F, size, color.withAlpha(255.0F));
               ms.pop();
            }
         }

         BuiltBuffer built = builder.endNullable();
         if (built != null) {
            BufferRenderer.drawWithGlobalProgram(built);
         }

         RenderUtility.endRender3D();
      }
   };

   @Generated
   public ModeSetting.Value getHeads() {
      return this.heads;
   }
}
