package moscow.definevisuals.mixin.minecraft.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.client.util.Icons;
import net.minecraft.client.util.Window;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Window.class)
public class WindowMixin implements IMinecraft {
   @Redirect(
      method = "setIcon",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Icons;getIcons(Lnet/minecraft/resource/ResourcePack;)Ljava/util/List;")
   )
   public List<InputSupplier<InputStream>> setCustomIcon(Icons instance, ResourcePack resourcePack) {
      if (DefineVisuals.getInstance().isPanic()) {
         try {
            return instance.getIcons(resourcePack);
         } catch (Exception var5) {
         }
      }

      // mc is null during initialization, fall back to default icons
      if (mc == null || mc.getResourceManager() == null) {
         try {
            return instance.getIcons(resourcePack);
         } catch (IOException e) {
            return List.of();
         }
      }

      try {
         // Try different resource paths
         String[] possiblePaths = {
            "icon.png",
            "textures/icon.png",
            "assets/definevisuals/icon.png",
            "src/main/resources/assets/definevisuals/icons/window/icon32x32.png"
         };
         
         for (String path : possiblePaths) {
            Identifier iconId = Identifier.of(DefineVisuals.MOD_ID, path);
            Resource iconResource = mc.getResourceManager().getResource(iconId).orElse(null);
            
            if (iconResource != null) {
               DefineVisuals.LOGGER.info("Loading custom window icon from ResourceManager: " + path);
               return List.of(
                  () -> iconResource.getInputStream(),
                  () -> iconResource.getInputStream()
               );
            }
         }
         
         DefineVisuals.LOGGER.warn("Custom window icon not found in ResourceManager");
      } catch (Exception e) {
         DefineVisuals.LOGGER.error("Failed to set custom window icon from ResourceManager", e);
      }
      
      try {
         return instance.getIcons(resourcePack);
      } catch (IOException e) {
         return List.of();
      }
   }
}
