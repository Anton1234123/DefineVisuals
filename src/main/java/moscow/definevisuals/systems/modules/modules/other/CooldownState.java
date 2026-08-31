package moscow.definevisuals.systems.modules.modules.other;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public final class CooldownState {
    public static final int NOT_RELEVANT = -1;
    public static final int READY = 0;
    public static final int ACTIVE = 1;
    private static final Pattern COOLDOWN_DELAY = Pattern.compile("(?i)(?:\\\"?delay\\\"?)\\s*:\\s*([0-9]{12,})[lL]?");

    private CooldownState() {
    }

    public static Result read(ItemStack stack, long currentTime) {
        try {
            if (stack == null) {
                return Result.notRelevant();
            } else {
                NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
                if (component != null && !component.isEmpty()) {
                    NbtCompound nbt = component.getNbt();
                    return nbt == null ? Result.notRelevant() : parse(nbt.toString(), currentTime);
                } else {
                    return Result.notRelevant();
                }
            }
        } catch (Throwable e) {
            return Result.notRelevant();
        }
    }

    private static Result parse(String data, long currentTime) {
        if (data != null && !data.isEmpty()) {
            Matcher matcher = COOLDOWN_DELAY.matcher(data);
            boolean hasDelay = false;
            long minCooldown = Long.MAX_VALUE;

            while(matcher.find()) {
                hasDelay = true;

                try {
                    long cooldownEnd = Long.parseLong(matcher.group(1));
                    if (cooldownEnd > currentTime && cooldownEnd < minCooldown) {
                        minCooldown = cooldownEnd;
                    }
                } catch (NumberFormatException e) {
                }
            }

            String lowerData = data.toLowerCase(Locale.ROOT);
            boolean isCompass = hasDelay || lowerData.contains("region-radar") || lowerData.contains("regionradar");
            if (!isCompass) {
                return Result.notRelevant();
            } else {
                return minCooldown != Long.MAX_VALUE ? new Result(ACTIVE, minCooldown) : new Result(READY, 0L);
            }
        } else {
            return Result.notRelevant();
        }
    }

    public static final class Result {
        private final int status;
        private final long cooldownEnd;

        public Result(int status, long cooldownEnd) {
            this.status = status;
            this.cooldownEnd = cooldownEnd;
        }

        public int status() {
            return this.status;
        }

        public long cooldownEnd() {
            return this.cooldownEnd;
        }

        public static Result notRelevant() {
            return new Result(NOT_RELEVANT, 0L);
        }
    }
}
