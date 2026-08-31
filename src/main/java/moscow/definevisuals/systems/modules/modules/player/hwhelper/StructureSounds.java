package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class StructureSounds {
    private static final double RANGE = 64.0;
    private static final long GROWL_WINDOW_MS = 800L;
    private static long lastDragonGrowlAt;

    private StructureSounds() {
    }

    public static void reset() {
        lastDragonGrowlAt = 0L;
    }

    public static boolean recentDragonGrowl() {
        return lastDragonGrowlAt > 0L && System.currentTimeMillis() - lastDragonGrowlAt < 800L;
    }

    public static void onSound(String soundName, double x, double y, double z, float volume, float pitch) {
        if (soundName != null && !soundName.isEmpty()) {
            String lower = soundName.toLowerCase();
            MinecraftClient client = MinecraftClient.getInstance();
            if (isPearlSound(lower)) {
                if (client.player != null && client.player.squaredDistanceTo(x, y, z) <= 4096.0) {
                    if (lower.contains("throw")) {
                        ItemNames.markPearlUse();
                    } else {
                        PearlGuard.onImpact(x, y, z);
                    }
                }

            } else if (!isWindSound(lower)) {
                if (lower.contains("ender_dragon.growl")) {
                    lastDragonGrowlAt = System.currentTimeMillis();
                    TimerTracker.upgradeNearbyTrapToDragon(new Vec3d(x, y, z));
                } else if (client.player != null) {
                    Vec3d soundPos = new Vec3d(x, y, z);
                    if (!ItemNames.isDistractorHeld(client.player) && !PearlGuard.shouldIgnoreNear(soundPos)) {
                        Kind kind = null;
                        StructureType type = null;
                        long duration = 0L;
                        if (!lower.contains("block.anvil.place") || !funTimeVolume(volume) || !near(pitch, 1.1F) && !near(pitch, 0.5F)) {
                            if ((lower.contains("block.piston.extend") || lower.contains("block.piston.contract")) && funTimeTrapVolume(volume) && funTimeTrapPitch(pitch)) {
                                kind = Kind.TRAP;
                                boolean dragon = recentDragonGrowl();
                                type = dragon ? StructureType.DRAGON_TRAPKA : StructureType.TRAPKA;
                                duration = dragon ? 30000L : 15000L;
                            }
                        }

                        if (kind != null && type != null) {
                            if (!(client.player.squaredDistanceTo(soundPos) > 4096.0)) {
                                BlockPos blockPos = BlockPos.ofFloored(x, y, z);
                                Vec3d label = Vec3d.ofCenter(blockPos);
                                Box box = new Box(blockPos).expand(0.0, 1.0, 0.0);
                                TimerTracker.start(type, label, box, duration, System.currentTimeMillis(), false);
                                StructureDetector.onSoundPlacement(kind, blockPos);
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isPearlSound(String lower) {
        return lower.contains("ender_pearl") || lower.contains("enderman.teleport") || lower.contains("entity.enderman.teleport") || lower.contains("chorus_fruit");
    }

    private static boolean isWindSound(String lower) {
        return lower.contains("wind_charge") || lower.contains("wind_burst") || lower.contains("wind_charge_burst") || lower.contains("entity.breeze") || lower.contains("entity.wind_charge") || lower.contains("gust");
    }

    public static Kind peekKind(Vec3d center) {
        return null;
    }

    public static Long pollStart(Kind kind, Vec3d center) {
        return null;
    }

    private static boolean funTimeVolume(float volume) {
        return near(volume, 0.5F) || near(volume, 0.7F);
    }

    private static boolean funTimeTrapVolume(float volume) {
        return volume >= 0.4F && volume <= 0.85F;
    }

    private static boolean funTimeTrapPitch(float pitch) {
        return near(pitch, 0.5F) || near(pitch, 0.55F) || near(pitch, 0.45F);
    }

    private static boolean near(float value, float target) {
        return Math.abs(value - target) < 0.08F;
    }
}
