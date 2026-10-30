package moscow.definevisuals.cosmetics.render;

import com.mojang.blaze3d.systems.RenderSystem;

public final class RenderSystemHelper {
   private RenderSystemHelper() {
   }

   public static void disableCull() {
      RenderSystem.disableCull();
   }

   public static void enableCull() {
      RenderSystem.enableCull();
   }
}
