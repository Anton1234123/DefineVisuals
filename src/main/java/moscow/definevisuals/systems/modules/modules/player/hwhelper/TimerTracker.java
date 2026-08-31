package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public final class TimerTracker {
    private static final List<ActiveTimer> TIMERS = new ArrayList();
    private static final double MERGE_DIST_SQ = 64.0;
    private static final long MERGE_WINDOW_MS = 5000L;

    private TimerTracker() {
    }

    public static void clear() {
        TIMERS.clear();
    }

    public static List<ActiveTimer> active() {
        return TIMERS;
    }

    public static void tick() {
        long now = System.currentTimeMillis();
        TIMERS.removeIf((timer) -> timer.remainingMs(now) <= 0L);
    }

    public static void start(StructureType type, Vec3d pos, Box box, long durationMs) {
        start(type, pos, box, durationMs, (Long)null, false);
    }

    public static void start(StructureType type, Vec3d pos, Box box, long durationMs, Long soundStartedAt) {
        start(type, pos, box, durationMs, soundStartedAt, false);
    }

    public static void start(StructureType type, Vec3d pos, Box box, long durationMs, Long soundStartedAt, boolean lockDuration) {
        if (type != null && pos != null && durationMs > 0L) {
            long now = System.currentTimeMillis();
            ActiveTimer existing = findNearby(pos, now);
            if (existing != null) {
                existing.pos = pos;
                existing.type = type;
                if (box != null) {
                    existing.box = box;
                }

                if (lockDuration || !existing.durationLocked) {
                    existing.durationMs = durationMs;
                }

                if (lockDuration) {
                    existing.durationLocked = true;
                }

                if (soundStartedAt != null && soundStartedAt <= now) {
                    existing.startedAt = soundStartedAt;
                } else if (existing.kind() != type.kind()) {
                    existing.startedAt = now;
                }

            } else {
                long startedAt = soundStartedAt != null ? soundStartedAt : now;
                TIMERS.add(new ActiveTimer(type, pos, box, startedAt, durationMs, lockDuration));
            }
        }
    }

    public static void refine(StructureType type, Vec3d pos, Box box, long durationMs) {
        if (type != null && pos != null && durationMs > 0L) {
            long now = System.currentTimeMillis();
            ActiveTimer existing = findNearby(pos, now);
            if (existing == null) {
                start(type, pos, box, durationMs, now, false);
            } else {
                existing.pos = pos;
                existing.type = type;
                if (box != null) {
                    existing.box = box;
                }

                if (!existing.durationLocked || durationMs > existing.durationMs) {
                    existing.durationMs = durationMs;
                }

            }
        }
    }

    public static void upgradeNearbyTrapToDragon(Vec3d soundPos) {
        if (soundPos != null) {
            long now = System.currentTimeMillis();

            for (ActiveTimer timer : TIMERS) {
                if (timer.kind() == Kind.TRAP && now - timer.startedAt <= 5000L && timer.pos.squaredDistanceTo(soundPos) <= 64.0) {
                    timer.type = StructureType.DRAGON_TRAPKA;
                    timer.durationMs = StructureType.DRAGON_TRAPKA.durationMs();
                    timer.durationLocked = true;
                }
            }

        }
    }

    private static ActiveTimer findNearby(Vec3d pos, long now) {
        ActiveTimer best = null;
        double bestDist = 64.0;

        for (ActiveTimer timer : TIMERS) {
            if (now - timer.startedAt <= 5000L) {
                double dist = timer.pos.squaredDistanceTo(pos);
                if (dist <= bestDist) {
                    bestDist = dist;
                    best = timer;
                }
            }
        }

        return best;
    }

    public static final class ActiveTimer {
        public StructureType type;
        public Vec3d pos;
        public Box box;
        public long startedAt;
        public long durationMs;
        public boolean durationLocked;

        ActiveTimer(StructureType type, Vec3d pos, Box box, long startedAt, long durationMs, boolean durationLocked) {
            this.type = type;
            this.pos = pos;
            this.box = box;
            this.startedAt = startedAt;
            this.durationMs = durationMs;
            this.durationLocked = durationLocked;
        }

        public Kind kind() {
            return this.type.kind();
        }

        public long remainingMs(long now) {
            return Math.max(0L, this.durationMs - (now - this.startedAt));
        }

        public double remainingSeconds(long now) {
            return (double)this.remainingMs(now) / 1000.0;
        }

        public double progress(long now) {
            return this.durationMs <= 0L ? 0.0 : Math.max(0.0, Math.min(1.0, (double)this.remainingMs(now) / (double)this.durationMs));
        }

        public Vec3d labelPos() {
            return this.box != null ? new Vec3d((this.box.minX + this.box.maxX) * 0.5, (this.box.minY + this.box.maxY) * 0.5, (this.box.minZ + this.box.maxZ) * 0.5) : this.pos.add(0.0, 0.5, 0.0);
        }
    }
}
