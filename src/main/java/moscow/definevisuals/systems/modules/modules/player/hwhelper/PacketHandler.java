package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.math.BlockPos;
import moscow.definevisuals.systems.event.impl.network.ReceivePacketEvent;
import moscow.definevisuals.systems.event.EventListener;

public final class PacketHandler {
    private PacketHandler() {
    }

    public static EventListener<ReceivePacketEvent> createPacketListener() {
        return event -> {
            Object packet = event.getPacket();
            
            if (packet instanceof BlockUpdateS2CPacket blockUpdate) {
                handleBlockUpdate(blockUpdate);
            } else if (packet instanceof ChunkDeltaUpdateS2CPacket chunkDelta) {
                handleChunkDelta(chunkDelta);
            } else if (packet instanceof PlaySoundS2CPacket soundPacket) {
                handleSoundPacket(soundPacket);
            }
        };
    }

    private static void handleBlockUpdate(BlockUpdateS2CPacket packet) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            BlockPos pos = packet.getPos();
            BlockState newState = packet.getState();
            BlockState oldState = client.world.getBlockState(pos);
            StructureDetector.onBlockChange(pos, oldState.getBlock(), newState.getBlock());
        }
    }

    private static void handleChunkDelta(ChunkDeltaUpdateS2CPacket packet) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            packet.visitUpdates((pos, state) -> {
                BlockState oldState = client.world.getBlockState(pos);
                StructureDetector.onBlockChange(pos, oldState.getBlock(), state.getBlock());
            });
        }
    }

    private static void handleSoundPacket(PlaySoundS2CPacket packet) {
        String soundId = packet.getSound().value().id().toString();
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        float volume = packet.getVolume();
        float pitch = packet.getPitch();
        StructureSounds.onSound(soundId, x, y, z, volume, pitch);
    }
}
