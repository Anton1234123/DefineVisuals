package moscow.definevisuals.mixin.minecraft.item;

import moscow.definevisuals.systems.modules.modules.other.CooldownState;
import moscow.definevisuals.systems.modules.modules.other.CompassTooltip;
import moscow.definevisuals.DefineVisuals;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Locale;

@Mixin(ItemStack.class)
public abstract class CompassTooltipMixin {
    @Inject(
        method = "getTooltip",
        at = @At("RETURN")
    )
    private void compassTooltip$addCooldownInfo(CallbackInfoReturnable<List<Text>> cir) {
        try {
            CompassTooltip module = DefineVisuals.getInstance().getModuleManager().getModule(CompassTooltip.class);
            if (module == null || !module.isEnabled()) {
                return;
            }

            long currentTime = System.currentTimeMillis();
            CooldownState.Result result = CooldownState.read((ItemStack) (Object) this, currentTime);
            if (result.status() == CooldownState.NOT_RELEVANT) {
                return;
            }

            List<Text> tooltip = cir.getReturnValue();
            if (tooltip == null || tooltip.isEmpty()) {
                return;
            }

            Text firstLine = tooltip.get(0);
            String suffix;
            if (result.status() == CooldownState.ACTIVE) {
                String remaining = compassTooltip$formatRemaining(result.cooldownEnd() - currentTime);
                suffix = " [КД: " + remaining + "]";
            } else {
                suffix = " [КД нет]";
            }

            Text newText = Text.literal(suffix).styled(style -> style.withItalic(false).withColor(Formatting.YELLOW));
            firstLine = firstLine.copy().append(newText);
            tooltip.set(0, firstLine);
        } catch (Throwable e) {
        }
    }

    private static String compassTooltip$formatRemaining(long remainingMs) {
        long remainingSec = Math.max(1L, (remainingMs + 999L) / 1000L);
        long days = remainingSec / 86400L;
        long hours = remainingSec % 86400L / 3600L;
        long minutes = remainingSec % 3600L / 60L;
        long seconds = remainingSec % 60L;
        
        if (days > 0L) {
            return days + "д " + String.format(Locale.ROOT, "%02dч %02dм %02dс", hours, minutes, seconds);
        } else if (hours > 0L) {
            return hours + "ч " + String.format(Locale.ROOT, "%02dм %02dс", minutes, seconds);
        } else {
            return minutes > 0L ? minutes + "м " + String.format(Locale.ROOT, "%02dс", seconds) : seconds + "с";
        }
    }
}
