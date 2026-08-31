package moscow.definevisuals.mixin.minecraft.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moscow.definevisuals.systems.modules.modules.other.SpawnerTooltip;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.utility.game.ItemUtility;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class SpawnerHighlightMixin {
    @Inject(
        method = "drawItemInSlot",
        at = @At("HEAD"),
        require = 0
    )
    private void spawnerTooltip$highlight(ItemStack stack, int x, int y, CallbackInfo ci) {
        try {
            SpawnerTooltip module = DefineVisuals.getInstance().getModuleManager().getModule(SpawnerTooltip.class);
            if (module == null || !module.isEnabled()) {
                return;
            }

            NbtCompound nbt = ItemUtility.getNBT(stack);
            if (nbt == null || !nbt.contains("gs_marker")) {
                return;
            }

            int highlightColor = getHighlightColor(nbt);
            if (highlightColor == 0) {
                return;
            }

            DrawContext context = (DrawContext) (Object) this;
            context.fill(x, y, x + 16, y + 16, highlightColor);
        } catch (Throwable e) {
        }
    }

    private int getHighlightColor(NbtCompound nbt) {
        // Check for missing sword (red highlight)
        if (!nbt.contains("gs_sword_item")) {
            return 0x66FF0000;
        }

        String rawJson = nbt.getString("gs_sword_item");
        if (rawJson == null || rawJson.trim().isEmpty()) {
            return 0x66FF0000;
        }

        try {
            JsonObject json = JsonParser.parseString(rawJson).getAsJsonObject();
            
            // Check for farmer enchantment (green highlight)
            if (json.has("enchants") && json.get("enchants").isJsonObject()) {
                JsonObject enchants = json.getAsJsonObject("enchants");
                for (String enchantId : enchants.keySet()) {
                    int colon = enchantId.indexOf(':');
                    String path = colon >= 0 ? enchantId.substring(colon + 1) : enchantId;
                    if ("mob-farmer-enchant".equals(path)) {
                        return 0x6600FF00;
                    }
                }
            }
        } catch (Exception e) {
            return 0x66FF0000;
        }

        return 0;
    }
}
