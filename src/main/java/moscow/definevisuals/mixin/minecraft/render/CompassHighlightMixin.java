package moscow.definevisuals.mixin.minecraft.render;

import moscow.definevisuals.systems.modules.modules.other.CooldownState;
import moscow.definevisuals.systems.modules.modules.other.CompassTooltip;
import moscow.definevisuals.DefineVisuals;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class CompassHighlightMixin {
    @Inject(
        method = "drawItemInSlot",
        at = @At("HEAD"),
        require = 0
    )
    private void compassTooltip$highlight(ItemStack stack, int x, int y, CallbackInfo ci) {
        try {
            CompassTooltip module = DefineVisuals.getInstance().getModuleManager().getModule(CompassTooltip.class);
            if (module == null || !module.isEnabled() || !module.shouldHighlight()) {
                return;
            }

            CooldownState.Result result = CooldownState.read(stack, System.currentTimeMillis());
            if (result.status() != CooldownState.READY) {
                return;
            }

            DrawContext context = (DrawContext) (Object) this;
            context.fill(x, y, x + 16, y + 16, 0x6600FF00);
        } catch (Throwable e) {
        }
    }
}
