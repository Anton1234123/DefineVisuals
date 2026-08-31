package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import net.minecraft.util.math.Vec3d;

public final class PearlGuard {
    private static final long IMPACT_SUPPRESS_MS = 2500L;
    private static final double IMPACT_RADIUS_SQ = 100.0;
    private static long impactAt;
    private static Vec3d impactPos;

    private PearlGuard() {
    }

    public static void reset() {
        impactAt = 0L;
        impactPos = null;
    }

    public static void onThrow() {
    }

    public static void onImpact(double x, double y, double z) {
        impactAt = System.currentTimeMillis();
        impactPos = new Vec3d(x, y, z);
    }

    public static boolean shouldIgnoreNear(Vec3d pos) {
        if (pos != null && impactPos != null && impactAt > 0L) {
            if (System.currentTimeMillis() - impactAt >= 2500L) {
                return false;
            } else {
                return impactPos.squaredDistanceTo(pos) <= 100.0;
            }
        } else {
            return false;
        }
    }
}
