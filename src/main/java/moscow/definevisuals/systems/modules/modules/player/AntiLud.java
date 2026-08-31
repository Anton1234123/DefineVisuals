package moscow.definevisuals.systems.modules.modules.player;

import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.network.SendPacketEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.text.Text;

@ModuleInfo(name = "AntiLud", category = ModuleCategory.PLAYER, desc = "Блокирует команды /casino и /create, предотвращая лудоманию", enabledByDefault = false)
public class AntiLud extends BaseModule {
    private static final Text REASON = Text.of("не лудомань");
    
    private final EventListener<SendPacketEvent> onSendPacket = event -> {
        if (mc.player == null || mc.world == null || !isEnabled()) {
            return;
        }
        
        if (event.getPacket() instanceof CommandExecutionC2SPacket packet) {
            String command = packet.command().trim().toLowerCase();
            if (isBlocked(command)) {
                event.cancel();
                disconnect();
            }
        }
    };
    
    private boolean isBlocked(String command) {
        return command.equals("casino") || command.startsWith("casino ") || 
               command.equals("create") || command.startsWith("create ");
    }
    
    private void disconnect() {
        mc.execute(() -> {
            if (mc.player != null && mc.player.networkHandler != null) {
                mc.player.networkHandler.getConnection().disconnect(REASON);
            }
        });
    }
}
