package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public final class PlacementShapes {
    private PlacementShapes() {
    }

    public static List<Box> forHeld(PlayerEntity player) {
        Kind kind = ItemNames.fromHeld(player);
        boolean dragon = ItemNames.isDragonHeld(player);
        if (kind == Kind.TRAP) {
            return trapFrame(player.getBlockPos(), dragon);
        }
        return List.of();
    }

    public static Box trap(BlockPos pos, boolean dragon) {
        return dragon ? inset(pos.getX() - 3, pos.getY(), pos.getZ() - 3, pos.getX() + 4, pos.getY() + 6, pos.getZ() + 4) : inset(pos.getX() - 2, pos.getY(), pos.getZ() - 2, pos.getX() + 3, pos.getY() + 4, pos.getZ() + 3);
    }

    private static Box inset(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return new Box((double)minX + 0.01, (double)minY + 0.01, (double)minZ + 0.01, (double)maxX - 0.01, (double)maxY - 0.01, (double)maxZ - 0.01);
    }

    private static List<Box> trapFrame(BlockPos pos, boolean dragon) {
        List<Box> boxes = new ArrayList<>();
        int outerMinX = pos.getX() - (dragon ? 3 : 2);
        int outerMaxX = pos.getX() + (dragon ? 3 : 2);
        int outerMinZ = pos.getZ() - (dragon ? 3 : 2);
        int outerMaxZ = pos.getZ() + (dragon ? 3 : 2);
        int outerMinY = pos.getY();
        int outerMaxY = pos.getY() + (dragon ? 5 : 3);

        int innerMinX = pos.getX() - 1;
        int innerMaxX = pos.getX() + 1;
        int innerMinZ = pos.getZ() - 1;
        int innerMaxZ = pos.getZ() + 1;

        // Floor - outer ring only (4 strips covering full perimeter)
        boxes.add(inset(outerMinX, outerMinY, outerMinZ, innerMinX, outerMinY + 1, outerMaxZ + 1)); // Left strip (full Z)
        boxes.add(inset(innerMaxX + 1, outerMinY, outerMinZ, outerMaxX + 1, outerMinY + 1, outerMaxZ + 1)); // Right strip (full Z)
        boxes.add(inset(innerMinX, outerMinY, outerMinZ, innerMaxX + 1, outerMinY + 1, innerMinZ)); // Front strip (between inner X)
        boxes.add(inset(innerMinX, outerMinY, innerMaxZ + 1, innerMaxX + 1, outerMinY + 1, outerMaxZ + 1)); // Back strip (between inner X)

        // Ceiling - outer ring only (4 strips covering full perimeter)
        boxes.add(inset(outerMinX, outerMaxY, outerMinZ, innerMinX, outerMaxY + 1, outerMaxZ + 1));
        boxes.add(inset(innerMaxX + 1, outerMaxY, outerMinZ, outerMaxX + 1, outerMaxY + 1, outerMaxZ + 1));
        boxes.add(inset(innerMinX, outerMaxY, outerMinZ, innerMaxX + 1, outerMaxY + 1, innerMinZ));
        boxes.add(inset(innerMinX, outerMaxY, innerMaxZ + 1, innerMaxX + 1, outerMaxY + 1, outerMaxZ + 1));

        // Walls - outer ring only
        // Front wall (Z = outerMinZ)
        boxes.add(inset(outerMinX, outerMinY + 1, outerMinZ, innerMinX, outerMaxY, outerMinZ + 1)); // Left part
        boxes.add(inset(innerMaxX + 1, outerMinY + 1, outerMinZ, outerMaxX + 1, outerMaxY, outerMinZ + 1)); // Right part

        // Back wall (Z = outerMaxZ)
        boxes.add(inset(outerMinX, outerMinY + 1, outerMaxZ, innerMinX, outerMaxY, outerMaxZ + 1));
        boxes.add(inset(innerMaxX + 1, outerMinY + 1, outerMaxZ, outerMaxX + 1, outerMaxY, outerMaxZ + 1));

        // Left wall (X = outerMinX) - middle part between inner Z
        boxes.add(inset(outerMinX, outerMinY + 1, innerMinZ, outerMinX + 1, outerMaxY, innerMaxZ + 1));

        // Right wall (X = outerMaxX) - middle part between inner Z
        boxes.add(inset(outerMaxX, outerMinY + 1, innerMinZ, outerMaxX + 1, outerMaxY, innerMaxZ + 1));

        return boxes;
    }
}
