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

    private static final Identifier ICON_16 = DefineVisuals.id("icons/window/icon16x16.png");
    private static final Identifier ICON_32 = DefineVisuals.id("icons/window/icon32x32.png");

    @Redirect(
        method = "setIcon",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Icons;getIcons(Lnet/minecraft/resource/ResourcePack;)Ljava/util/List;")
    )
    public List<InputSupplier<InputStream>> setCustomIcon(Icons instance, ResourcePack resourcePack) {
        // Fallback: use vanilla icons if in panic mode or resource manager not ready
        if (DefineVisuals.getInstance().isPanic() || mc == null || mc.getResourceManager() == null) {
            try {
                return instance.getIcons(resourcePack);
            } catch (IOException e) {
                return List.of();
            }
        }

        try {
            Resource icon16 = mc.getResourceManager().getResource(ICON_16).orElse(null);
            Resource icon32 = mc.getResourceManager().getResource(ICON_32).orElse(null);

            if (icon16 != null && icon32 != null) {
                DefineVisuals.LOGGER.info("[DefineVisuals] Custom window icons loaded successfully.");
                final Resource finalIcon16 = icon16;
                final Resource finalIcon32 = icon32;
                // First supplier = small (16), second = large (32)
                return List.of(
                    finalIcon16::getInputStream,
                    finalIcon32::getInputStream
                );
            }

            // Try just the 32x32 if 16x16 is missing
            if (icon32 != null) {
                DefineVisuals.LOGGER.warn("[DefineVisuals] icon16x16.png not found, using icon32x32.png twice.");
                final Resource finalIcon32 = icon32;
                return List.of(finalIcon32::getInputStream, finalIcon32::getInputStream);
            }

            DefineVisuals.LOGGER.warn("[DefineVisuals] Custom window icons not found at icons/window/. Falling back to vanilla.");
        } catch (Exception e) {
            DefineVisuals.LOGGER.error("[DefineVisuals] Failed to load custom window icons", e);
        }

        // Fallback to default Minecraft icon
        try {
            return instance.getIcons(resourcePack);
        } catch (IOException e) {
            return List.of();
        }
    }
}
