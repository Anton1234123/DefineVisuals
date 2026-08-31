package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.Locale;

public final class ItemNames {
    private static final long WIND_SUPPRESS_MS = 2500L;
    private static final long PEARL_SUPPRESS_MS = 1500L;
    private static long lastWindChargeAt;
    private static long lastPearlAt;

    private ItemNames() {
    }

    public static void reset() {
        lastWindChargeAt = 0L;
        lastPearlAt = 0L;
    }

    public static void markWindChargeUse() {
        lastWindChargeAt = System.currentTimeMillis();
    }

    public static boolean recentWindChargeUse() {
        return lastWindChargeAt > 0L && System.currentTimeMillis() - lastWindChargeAt < 2500L;
    }

    public static void markPearlUse() {
        lastPearlAt = System.currentTimeMillis();
    }

    public static boolean recentPearlUse() {
        return lastPearlAt > 0L && System.currentTimeMillis() - lastPearlAt < 1500L;
    }

    public static boolean isWindCharge(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            if (stack.isOf(Items.WIND_CHARGE)) {
                return true;
            } else {
                return textWind(stack.getName()) || textWind(stack.get(DataComponentTypes.CUSTOM_NAME));
            }
        } else {
            return false;
        }
    }

    public static boolean isWindChargeHeld(PlayerEntity player) {
        if (player == null) {
            return false;
        } else {
            return isWindCharge(player.getMainHandStack()) || isWindCharge(player.getOffHandStack());
        }
    }

    public static boolean isEnderPearl(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            if (stack.isOf(Items.ENDER_PEARL)) {
                return true;
            } else {
                return textPearl(stack.getName()) || textPearl(stack.get(DataComponentTypes.CUSTOM_NAME));
            }
        } else {
            return false;
        }
    }

    public static boolean isEnderPearlHeld(PlayerEntity player) {
        if (player == null) {
            return false;
        } else {
            return isEnderPearl(player.getMainHandStack()) || isEnderPearl(player.getOffHandStack());
        }
    }

    public static boolean isDistractorHeld(PlayerEntity player) {
        if (player == null) {
            return false;
        } else if (fromHeld(player) != null) {
            return false;
        } else {
            return isWindChargeHeld(player) || isEnderPearlHeld(player);
        }
    }

    private static boolean textWind(Text text) {
        if (text == null) {
            return false;
        } else {
            String raw = text.getString();
            if (raw != null && !raw.isEmpty()) {
                String lower = raw.toLowerCase(Locale.ROOT);
                return lower.contains("заряд ветра") || lower.contains("wind charge") || lower.contains("windcharge") || lower.contains("ветряной заряд") || lower.contains("заряд") && lower.contains("ветр");
            } else {
                return false;
            }
        }
    }

    private static boolean textPearl(Text text) {
        if (text == null) {
            return false;
        } else {
            String raw = text.getString();
            if (raw != null && !raw.isEmpty()) {
                String lower = raw.toLowerCase(Locale.ROOT);
                return lower.contains("жемчуг") || lower.contains("ender pearl") || lower.contains("enderpearl") || lower.contains("эндер") && lower.contains("жемчуг");
            } else {
                return false;
            }
        }
    }

    public static Kind fromHeld(PlayerEntity player) {
        if (player == null) {
            return null;
        } else {
            Kind main = fromStack(player.getMainHandStack());
            return main != null ? main : fromStack(player.getOffHandStack());
        }
    }

    public static boolean isDragonHeld(PlayerEntity player) {
        if (player == null) {
            return false;
        } else {
            return isDragon(player.getMainHandStack()) || isDragon(player.getOffHandStack());
        }
    }

    public static boolean isDragon(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            if (textDragon(stack.getName())) {
                return true;
            } else {
                Text custom = stack.get(DataComponentTypes.CUSTOM_NAME);
                if (textDragon(custom)) {
                    return true;
                } else {
                    LoreComponent lore = stack.get(DataComponentTypes.LORE);
                    if (lore != null) {
                        for (Text line : lore.lines()) {
                            if (textDragon(line)) {
                                return true;
                            }
                        }
                    }

                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private static boolean textDragon(Text text) {
        if (text == null) {
            return false;
        } else {
            String raw = text.getString();
            if (raw != null && !raw.isEmpty()) {
                String lower = raw.toLowerCase(Locale.ROOT);
                return lower.contains("дракон") || lower.contains("драк") || lower.contains("dragon") || lower.contains("драг");
            } else {
                return false;
            }
        }
    }

    public static Kind fromStack(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            Kind byItem = fromItem(stack);
            if (byItem != null) {
                return byItem;
            } else {
                Kind named = fromText(stack.getName());
                if (named != null) {
                    return named;
                } else {
                    Text custom = stack.get(DataComponentTypes.CUSTOM_NAME);
                    if (custom != null) {
                        named = fromText(custom);
                        if (named != null) {
                            return named;
                        }
                    }

                    LoreComponent lore = stack.get(DataComponentTypes.LORE);
                    if (lore != null) {
                        for (Text line : lore.lines()) {
                            named = fromText(line);
                            if (named != null) {
                                return named;
                            }
                        }
                    }

                    return null;
                }
            }
        } else {
            return null;
        }
    }

    private static Kind fromItem(ItemStack stack) {
        if (stack.isOf(Items.WIND_CHARGE)) {
            return null;
        } else if (stack.isOf(Items.POPPED_CHORUS_FRUIT) || stack.isOf(Items.PISTON) || stack.isOf(Items.STICKY_PISTON) || stack.isOf(Items.SLIME_BLOCK)) {
            return Kind.TRAP;
        } else if (stack.isOf(Items.PRISMARINE_SHARD)) {
            return Kind.EXPLOSIVE_TRAP; // Взрывная трапка
        } else {
            return null;
        }
    }

    public static Kind fromText(Text text) {
        return text == null ? null : fromString(text.getString());
    }

    public static Kind fromString(String raw) {
        if (raw != null && !raw.isEmpty()) {
            String text = raw.toLowerCase(Locale.ROOT);
            if (!text.contains("заряд ветра") && !text.contains("wind charge") && !text.contains("ветряной") && !text.contains("жемчуг") && !text.contains("ender pearl") && !text.contains("enderpearl")) {
                if (text.contains("трапк") || text.contains("трапа") || text.contains("trapka") || text.contains("драконк") || (containsWord(text, "trap") && !text.contains("chest") && !text.contains("сундук"))) {
                    return Kind.TRAP;
                }
            }
        }
        return null;
    }

    private static boolean containsWord(String text, String word) {
        int index;
        for (int from = 0; from <= text.length() - word.length(); from = index + 1) {
            index = text.indexOf(word, from);
            if (index < 0) {
                return false;
            }

            boolean left = index == 0 || !Character.isLetterOrDigit(text.charAt(index - 1));
            int end = index + word.length();
            boolean right = end == text.length() || !Character.isLetterOrDigit(text.charAt(end));
            if (left && right) {
                return true;
            }
        }

        return false;
    }
}
