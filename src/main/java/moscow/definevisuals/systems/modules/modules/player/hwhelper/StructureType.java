package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public enum StructureType {
    DRAGON_TRAPKA("Драконья трапка", "Dragon trap", Kind.TRAP, 30000L, Items.PISTON, true, new int[][]{{7, 7, 7}, {7, 7, 6}}),
    TRAPKA("Трапка", "Trap", Kind.TRAP, 15000L, Items.POPPED_CHORUS_FRUIT, true, new int[][]{{5, 5, 5}, {5, 5, 4}, {5, 6, 5}, {5, 6, 4}});

    private final String russianName;
    private final String englishName;
    private final Kind kind;
    private final long durationMs;
    private final Item icon;
    private final boolean hollow;
    private final int[][] dimensions;

    StructureType(String russianName, String englishName, Kind kind, long durationMs, Item icon, boolean hollow, int[][] dimensions) {
        this.russianName = russianName;
        this.englishName = englishName;
        this.kind = kind;
        this.durationMs = durationMs;
        this.icon = icon;
        this.hollow = hollow;
        this.dimensions = dimensions;
    }

    public String displayName(boolean russian) {
        return russian ? this.russianName : this.englishName;
    }

    public Kind kind() {
        return this.kind;
    }

    public long durationMs() {
        return this.durationMs;
    }

    public Item icon() {
        return this.icon;
    }

    public boolean matches(int spanX, int spanY, int spanZ, Set<net.minecraft.util.math.BlockPos> positions, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        int[] size = new int[]{spanX, spanY, spanZ};
        Arrays.sort(size);

        for (int[] dimension : this.dimensions) {
            int[] sorted = dimension.clone();
            Arrays.sort(sorted);
            if (Arrays.equals(size, sorted)) {
                return this.hollow == noneInside(positions, minX, minY, minZ, maxX, maxY, maxZ);
            }
        }

        return false;
    }

    private static boolean noneInside(Set<net.minecraft.util.math.BlockPos> positions, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (net.minecraft.util.math.BlockPos pos : positions) {
            if (pos.getX() > minX && pos.getX() < maxX && pos.getY() > minY && pos.getY() < maxY && pos.getZ() > minZ && pos.getZ() < maxZ) {
                return false;
            }
        }

        return true;
    }
}
