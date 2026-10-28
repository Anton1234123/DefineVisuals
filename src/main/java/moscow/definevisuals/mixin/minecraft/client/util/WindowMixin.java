package moscow.definevisuals.mixin.minecraft.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.client.util.Icons;
import net.minecraft.client.util.Window;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Window.class)
public class WindowMixin implements IMinecraft {

    private static final String[] ICON_PATHS = {
        "/assets/definevisuals/icons/window/icon16x16.png",
        "/assets/definevisuals/icons/window/icon32x32.png",
        "/assets/definevisuals/icons/window/icon64x64.png",
        "/assets/definevisuals/icons/window/icon128x128.png"
    };

    @Redirect(
        method = "setIcon",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Icons;getIcons(Lnet/minecraft/resource/ResourcePack;)Ljava/util/List;")
    )
    public List<InputSupplier<InputStream>> setCustomIcon(Icons instance, ResourcePack resourcePack) {
        if (DefineVisuals.getInstance() != null && DefineVisuals.getInstance().isPanic()) {
            try {
                return instance.getIcons(resourcePack);
            } catch (IOException e) {
                return List.of();
            }
        }

        List<InputSupplier<InputStream>> suppliers = new ArrayList<>();
        for (String path : ICON_PATHS) {
            if (WindowMixin.class.getResource(path) != null) {
                suppliers.add(() -> {
                    InputStream stream = WindowMixin.class.getResourceAsStream(path);
                    if (stream == null) {
                        throw new IOException("Icon resource not found: " + path);
                    }
                    return stream;
                });
            }
        }

        if (!suppliers.isEmpty()) {
            DefineVisuals.LOGGER.info("[DefineVisuals] Custom window icons loaded ({} sizes)", suppliers.size());
            return suppliers;
        }

        try {
            return instance.getIcons(resourcePack);
        } catch (IOException e) {
            return List.of();
        }
    }
}
