package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.*;

public final class VoxelMesh {
    private static final float INSET = 0.01F;
    public final List<Quad> quads = new ArrayList();
    public final List<Line> lines = new ArrayList();

    private VoxelMesh() {
    }

    public static VoxelMesh of(List<Box> boxes) {
        VoxelMesh mesh = new VoxelMesh();
        if (boxes != null && !boxes.isEmpty()) {
            Set<BlockPos> voxels = new HashSet();

            for (Box box : boxes) {
                int minX = (int)Math.floor(box.minX + 1.0E-4);
                int minY = (int)Math.floor(box.minY + 1.0E-4);
                int minZ = (int)Math.floor(box.minZ + 1.0E-4);
                int maxX = (int)Math.ceil(box.maxX - 1.0E-4);
                int maxY = (int)Math.ceil(box.maxY - 1.0E-4);
                int maxZ = (int)Math.ceil(box.maxZ - 1.0E-4);

                for (int x = minX; x < maxX; ++x) {
                    for (int y = minY; y < maxY; ++y) {
                        for (int z = minZ; z < maxZ; ++z) {
                            voxels.add(new BlockPos(x, y, z));
                        }
                    }
                }
            }

            for (BlockPos pos : voxels) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                float x0 = (float)x + 0.01F;
                float y0 = (float)y + 0.01F;
                float z0 = (float)z + 0.01F;
                float x1 = (float)x + 1.0F - 0.01F;
                float y1 = (float)y + 1.0F - 0.01F;
                float z1 = (float)z + 1.0F - 0.01F;
                if (!has(voxels, x + 1, y, z)) {
                    mesh.quads.add(new Quad(x1, y0, z0, x1, y0, z1, x1, y1, z1, x1, y1, z0));
                }

                if (!has(voxels, x - 1, y, z)) {
                    mesh.quads.add(new Quad(x0, y0, z0, x0, y1, z0, x0, y1, z1, x0, y0, z1));
                }

                if (!has(voxels, x, y + 1, z)) {
                    mesh.quads.add(new Quad(x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1));
                }

                if (!has(voxels, x, y - 1, z)) {
                    mesh.quads.add(new Quad(x0, y0, z0, x0, y0, z1, x1, y0, z1, x1, y0, z0));
                }

                if (!has(voxels, x, y, z + 1)) {
                    mesh.quads.add(new Quad(x0, y0, z1, x0, y1, z1, x1, y1, z1, x1, y0, z1));
                }

                if (!has(voxels, x, y, z - 1)) {
                    mesh.quads.add(new Quad(x0, y0, z0, x1, y0, z0, x1, y1, z0, x0, y1, z0));
                }
            }

            Set<EdgeKey> seen = new HashSet();

            for (BlockPos pos : voxels) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                consider(voxels, seen, mesh.lines, 0, x, y, z);
                consider(voxels, seen, mesh.lines, 0, x, y + 1, z);
                consider(voxels, seen, mesh.lines, 0, x, y, z + 1);
                consider(voxels, seen, mesh.lines, 0, x, y + 1, z + 1);
                consider(voxels, seen, mesh.lines, 1, x, y, z);
                consider(voxels, seen, mesh.lines, 1, x + 1, y, z);
                consider(voxels, seen, mesh.lines, 1, x, y, z + 1);
                consider(voxels, seen, mesh.lines, 1, x + 1, y, z + 1);
                consider(voxels, seen, mesh.lines, 2, x, y, z);
                consider(voxels, seen, mesh.lines, 2, x + 1, y, z);
                consider(voxels, seen, mesh.lines, 2, x, y + 1, z);
                consider(voxels, seen, mesh.lines, 2, x + 1, y + 1, z);
            }

            return mesh;
        } else {
            return mesh;
        }
    }

    private static void consider(Set<BlockPos> voxels, Set<EdgeKey> seen, List<Line> lines, int axis, int x, int y, int z) {
        EdgeKey key = new EdgeKey(axis, x, y, z);
        if (seen.add(key)) {
            boolean a;
            boolean b;
            boolean c;
            boolean d;
            if (axis == 0) {
                a = has(voxels, x, y, z);
                b = has(voxels, x, y - 1, z);
                c = has(voxels, x, y, z - 1);
                d = has(voxels, x, y - 1, z - 1);
            } else if (axis == 1) {
                a = has(voxels, x, y, z);
                b = has(voxels, x - 1, y, z);
                c = has(voxels, x, y, z - 1);
                d = has(voxels, x - 1, y, z - 1);
            } else {
                a = has(voxels, x, y, z);
                b = has(voxels, x - 1, y, z);
                c = has(voxels, x, y - 1, z);
                d = has(voxels, x - 1, y - 1, z);
            }

            int n = (a ? 1 : 0) + (b ? 1 : 0) + (c ? 1 : 0) + (d ? 1 : 0);
            if (n != 0 && n != 4) {
                if (n != 2 || (!a || !b) && (!a || !c) && (!b || !d) && (!c || !d)) {
                    if (axis == 0) {
                        lines.add(new Line((float)x, (float)y, (float)z, (float)(x + 1), (float)y, (float)z));
                    } else if (axis == 1) {
                        lines.add(new Line((float)x, (float)y, (float)z, (float)x, (float)(y + 1), (float)z));
                    } else {
                        lines.add(new Line((float)x, (float)y, (float)z, (float)x, (float)y, (float)(z + 1)));
                    }

                }
            }
        }
    }

    private static boolean has(Set<BlockPos> voxels, int x, int y, int z) {
        return voxels.contains(new BlockPos(x, y, z));
    }

    public static record Quad(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
    }

    public static record Line(float x1, float y1, float z1, float x2, float y2, float z2) {
    }

    private static record EdgeKey(int axis, int x, int y, int z) {
    }
}
