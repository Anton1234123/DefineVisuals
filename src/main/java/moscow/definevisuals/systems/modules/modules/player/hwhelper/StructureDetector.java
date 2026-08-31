package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

public final class StructureDetector {
    private static final double RANGE = 48.0;
    private static final int SETTLE_TICKS = 6;
    private static final int MAX_RETRY_TICKS = 30;
    private static final int MAX_FLOOD = 343;
    private static final Set<Block> STRUCTURE_BLOCKS;
    private static final Set<BlockPos> PENDING = new HashSet<>();
    private static int settleTicks;
    private static int retryTicks;
    private static Kind pendingSoundKind;

    private StructureDetector() {
    }

    public static void reset() {
        PENDING.clear();
        settleTicks = 0;
        retryTicks = 0;
        pendingSoundKind = null;
    }

    public static boolean isStructureBlock(Block block) {
        return block != null && STRUCTURE_BLOCKS.contains(block);
    }

    public static void onSoundPlacement(Kind kind, BlockPos pos) {
        if (kind != null && pos != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                if (!ItemNames.isDistractorHeld(client.player)) {
                    if (!(client.player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) > 2304.0)) {
                        pendingSoundKind = kind;
                        PENDING.add(pos.toImmutable());
                        settleTicks = 6;
                        retryTicks = 0;
                    }
                }
            }
        }
    }

    public static void onBlockChange(BlockPos pos, Block oldBlock, Block newBlock) {
        if (isStructureBlock(newBlock) && !isStructureBlock(oldBlock)) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                if (!ItemNames.isDistractorHeld(client.player)) {
                    if (!(client.player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) > 2304.0)) {
                        PENDING.add(pos.toImmutable());
                        settleTicks = 6;
                        retryTicks = 0;
                    }
                }
            }
        }
    }

    public static void tick(MinecraftClient client) {
        if (client.world != null && client.player != null) {
            if (settleTicks > 0) {
                --settleTicks;
                if (settleTicks == 0 && !PENDING.isEmpty()) {
                    Set<BlockPos> expanded = expand(client.world, PENDING);
                    if (!classify(expanded)) {
                        ++retryTicks;
                        if (retryTicks < 30) {
                            settleTicks = 2;
                            return;
                        }
                    }

                    PENDING.clear();
                    retryTicks = 0;
                    pendingSoundKind = null;
                }
            }
        } else {
            reset();
        }
    }

    private static Set<BlockPos> expand(World world, Set<BlockPos> seeds) {
        Set<BlockPos> found = new HashSet();
        ArrayDeque<BlockPos> queue = new ArrayDeque();

        for (BlockPos seed : seeds) {
            if (isStructureBlock(world.getBlockState(seed).getBlock()) && found.add(seed.toImmutable())) {
                queue.add(seed.toImmutable());
            }
        }

        BlockPos.Mutable cursor = new BlockPos.Mutable();

        while (!queue.isEmpty() && found.size() < 343) {
            BlockPos current = queue.poll();

            for (Direction direction : Direction.values()) {
                cursor.set(current, direction);
                if (!found.contains(cursor) && isStructureBlock(world.getBlockState(cursor).getBlock())) {
                    BlockPos next = cursor.toImmutable();
                    if (found.add(next)) {
                        queue.add(next);
                    }
                }
            }
        }

        return found.isEmpty() ? seeds : found;
    }

    private static boolean classify(Set<BlockPos> positions) {
        if (positions.isEmpty()) {
            return false;
        } else {
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            int maxZ = Integer.MIN_VALUE;

            for (BlockPos pos : positions) {
                minX = Math.min(minX, pos.getX());
                minY = Math.min(minY, pos.getY());
                minZ = Math.min(minZ, pos.getZ());
                maxX = Math.max(maxX, pos.getX());
                maxY = Math.max(maxY, pos.getY());
                maxZ = Math.max(maxZ, pos.getZ());
            }

            int spanX = maxX - minX + 1;
            int spanY = maxY - minY + 1;
            int spanZ = maxZ - minZ + 1;
            StructureType trap = matchKind(positions, minX, minY, minZ, maxX, maxY, maxZ, spanX, spanY, spanZ, Kind.TRAP);
            StructureType type = trap != null ? trap : inferTrap(spanX, spanY, spanZ);

            if (type == null) {
                return false;
            } else {
                commit(type, minX, minY, minZ, maxX, maxY, maxZ, spanX, spanY, spanZ);
                return true;
            }
        }
    }

    private static StructureType inferTrap(int spanX, int spanY, int spanZ) {
        int[] size = new int[]{spanX, spanY, spanZ};
        Arrays.sort(size);
        if (size[0] >= 4 && size[1] >= 5 && size[2] >= 5 && size[2] <= 6 && size[0] <= 6) {
            return StructureType.TRAPKA;
        } else {
            return size[0] >= 6 && size[1] >= 6 && size[2] >= 6 && size[2] <= 8 ? StructureType.DRAGON_TRAPKA : null;
        }
    }

    private static StructureType matchKind(Set<BlockPos> positions, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int spanX, int spanY, int spanZ, Kind kind) {
        for (StructureType type : StructureType.values()) {
            if (type.kind() == kind && type.matches(spanX, spanY, spanZ, positions, minX, minY, minZ, maxX, maxY, maxZ)) {
                return type;
            }
        }

        return null;
    }

    private static void commit(StructureType type, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int spanX, int spanY, int spanZ) {
        Box box = new Box((double)minX, (double)minY, (double)minZ, (double)maxX + 1.0, (double)maxY + 1.0, (double)maxZ + 1.0);
        net.minecraft.util.math.Vec3d center = new net.minecraft.util.math.Vec3d((double)(minX + maxX + 1) * 0.5, (double)(minY + maxY + 1) * 0.5, (double)(minZ + maxZ + 1) * 0.5);
        long duration = type.durationMs();
        TimerTracker.refine(type, center, box, duration);
    }

    static {
        STRUCTURE_BLOCKS = Set.of(
            Blocks.PISTON,
            Blocks.STICKY_PISTON,
            Blocks.PISTON_HEAD,
            Blocks.MOVING_PISTON,
            Blocks.SLIME_BLOCK,
            Blocks.HONEY_BLOCK,
            Blocks.MAGMA_BLOCK,
            Blocks.OBSIDIAN,
            Blocks.CRYING_OBSIDIAN,
            Blocks.ANVIL,
            Blocks.CHIPPED_ANVIL,
            Blocks.DAMAGED_ANVIL,
            Blocks.NETHERITE_BLOCK,
            Blocks.ANCIENT_DEBRIS,
            Blocks.RESPAWN_ANCHOR
        );
    }
}
