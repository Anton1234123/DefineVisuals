package moscow.definevisuals.systems.modules.modules.other;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.network.ReceivePacketEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.utility.sounds.UiSound;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
// cracked by t.me/pasterendnew
@ModuleInfo(name = "Sounds", category = ModuleCategory.OTHER, enabledByDefault = true, desc = "Добавляет звуки клиента")
public class Sounds extends BaseModule {
   private final SliderSetting volume = new SliderSetting(this, "Громкость звука").step(0.1F).min(0.1F).max(1.0F).currentValue(1.0F);
   private final EventListener<ReceivePacketEvent> receivePacket = event -> {
      if (event.getPacket() instanceof GameMessageS2CPacket packet) {
         String msg = packet.content().getString();
         if (msg.contains("Вы успешно купили") || msg.contains("отправлено игроку")) {
             UiSound.PAYMENT.play();
         }
      }
   };

   @Generated
   public SliderSetting getVolume() {
      return this.volume;
   }

   @Generated
   public EventListener<ReceivePacketEvent> getReceivePacket() {
      return this.receivePacket;
   }
}
