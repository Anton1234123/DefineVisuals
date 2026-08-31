package moscow.definevisuals.systems.modules.modules.other;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.network.ReceivePacketEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@ModuleInfo(name = "Death Coords", category = ModuleCategory.OTHER, desc = "Показывает координаты смерти с возможностью копирования")
public class DeathCoords extends BaseModule {
   private final EventListener<ReceivePacketEvent> onPacketReceive = event -> {
      if (event.getPacket() instanceof DeathMessageS2CPacket) {
         if (mc.player == null || mc.world == null) {
            return;
         }
         String coords = String.format("%.0f %.0f %.0f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
         MutableText message = Text.literal(coords).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, coords)));
         mc.player.sendMessage(message, false);
      }
   };

   @Generated
   public EventListener<ReceivePacketEvent> getOnPacketReceive() {
      return this.onPacketReceive;
   }
}
