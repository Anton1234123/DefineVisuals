package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public final class WorldOverlay {
    private WorldOverlay() {
    }

    public static void renderPreview(net.minecraft.client.util.math.MatrixStack matrices, Vec3d camera) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null && client.player != null) {
            Kind kind = ItemNames.fromHeld(client.player);
            if (kind == Kind.TRAP) {
                // Рендерим обычную трапку как структуру с блоками
                BlockPos blockPos = client.player.getBlockPos().down();
                Vec3d structurePos = Vec3d.of(blockPos);
                TrapStructure structure = TrapStructure.createNormalTrap();
                TrapStructureRenderer.render(matrices, camera, structurePos, structure);
            } else if (kind == Kind.EXPLOSIVE_TRAP) {
                // Спавним структуру на позиции блока ПОД игроком - всегда SOUTH
                BlockPos blockPos = client.player.getBlockPos().down();
                Vec3d structurePos = Vec3d.of(blockPos);
                
                // Трапка всегда спавнится в направлении SOUTH
                TrapStructure structure = TrapStructure.createExplosiveTrap();
                TrapStructureRenderer.render(matrices, camera, structurePos, structure, net.minecraft.util.math.Direction.SOUTH);
            }
        }
    }

    private static boolean hasOtherPlayer(MinecraftClient client, List<Box> boxes) {
        if (client.world != null && boxes != null && !boxes.isEmpty()) {
            for (PlayerEntity player : client.world.getPlayers()) {
                if (player != client.player && !player.isSpectator()) {
                    Box hitbox = player.getBoundingBox();

                    for (Box box : boxes) {
                        if (box.expand(0.5, 0.5, 0.5).intersects(hitbox)) {
                            return true;
                        }
                    }
                }
            }

            return false;
        } else {
            return false;
        }
    }

    private static void drawColored(net.minecraft.client.util.math.MatrixStack matrices, Vec3d camera, List<Box> boxes, boolean catchable) {
        if (catchable) {
            WorldBoxRenderer.draw(matrices, camera, boxes, 0, 255, 0, 255);
        } else {
            WorldBoxRenderer.draw(matrices, camera, boxes, 255, 61, 61, 255);
        }
    }
}
