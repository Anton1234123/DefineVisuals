package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class TrapStructureRenderer {
    private static final float ALPHA = 0.7f;
    private static final Random RANDOM = Random.create();

    public static void render(MatrixStack matrices, Vec3d cameraPos, Vec3d center, TrapStructure structure) {
        render(matrices, cameraPos, center, structure, null);
    }

    public static void render(MatrixStack matrices, Vec3d cameraPos, Vec3d center, TrapStructure structure, net.minecraft.util.math.Direction facing) {
        if (structure == null || structure.getBlocks().isEmpty()) {
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, ALPHA);

        for (TrapStructure.BlockDefinition blockDef : structure.getBlocks()) {
            Vec3d blockPos = center.add(blockDef.x, blockDef.y, blockDef.z);
            
            // Применяем вращение если задано направление
            if (facing != null) {
                blockPos = rotatePosition(blockPos, center, facing);
            }
            
            BlockPos renderPos = BlockPos.ofFloored(blockPos);
            
            matrices.push();
            matrices.translate(
                blockPos.x - cameraPos.x,
                blockPos.y - cameraPos.y,
                blockPos.z - cameraPos.z
            );

            BlockState blockState = blockDef.block.getDefaultState();
            
            // Рендерим блок с текстурами через встроенный рендерер
            VertexConsumer consumer = immediate.getBuffer(RenderLayer.getTranslucent());
            mc.getBlockRenderManager().renderBlock(blockState, renderPos, mc.world, matrices, consumer, false, RANDOM);
            
            matrices.pop();
        }

        immediate.draw();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    private static Vec3d rotatePosition(Vec3d pos, Vec3d center, net.minecraft.util.math.Direction facing) {
        double dx = pos.x - center.x;
        double dz = pos.z - center.z;
        
        double newDx = dx;
        double newDz = dz;
        
        // Структура определена для направления SOUTH (без вращения)
        switch (facing) {
            case SOUTH:
                // Базовая ориентация - без вращения
                newDx = dx;
                newDz = dz;
                break;
            case WEST:
                // Поворот на 90 градусов против часовой
                newDx = -dz;
                newDz = dx;
                break;
            case NORTH:
                // Поворот на 180 градусов
                newDx = -dx;
                newDz = -dz;
                break;
            case EAST:
                // Поворот на 270 градусов против часовой (или 90 по часовой)
                newDx = dz;
                newDz = -dx;
                break;
            default:
                newDx = dx;
                newDz = dz;
                break;
        }
        
        return new Vec3d(center.x + newDx, pos.y, center.z + newDz);
    }
}
