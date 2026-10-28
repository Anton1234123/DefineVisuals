package moscow.definevisuals.systems.commands.commands;

import java.util.ArrayList;
import java.util.List;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.commands.Command;
import moscow.definevisuals.systems.commands.CommandBuilder;
import moscow.definevisuals.systems.commands.CommandContext;
import moscow.definevisuals.systems.commands.ValidationResult;
import moscow.definevisuals.systems.commands.commands.hubswap.AnReg;
import moscow.definevisuals.systems.commands.commands.hubswap.TpMan;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.network.ReceivePacketEvent;
import moscow.definevisuals.systems.event.impl.player.ClientPlayerTickEvent;
import moscow.definevisuals.utility.game.MessageUtility;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;

public class ReHubCommand implements IMinecraft {
   private final TpMan tp = new TpMan();

   private final EventListener<ClientPlayerTickEvent> onTickEvent = event -> {
      this.tp.tick();
   };

   private final EventListener<ReceivePacketEvent> onReceivePacketEvent = event -> {
      if (event.getPacket() instanceof GameMessageS2CPacket packet) {
         String text = packet.content().getString();
         this.tp.onMsg(text);
      }
   };

   public ReHubCommand() {
      DefineVisuals.getInstance().getEventManager().subscribe(this);
   }

   public Command command() {
      return CommandBuilder.begin("rct")
         .aliases("reconnect", "anarchy")
         .desc("Быстрый переход на анархию HolyWorld")
         .param("number", p -> {
            p.validator(text -> {
               try {
                  int num = Integer.parseInt(text);
                  if (num >= 1 && num <= AnReg.getMaxNumber()) {
                     return ValidationResult.ok(num);
                  } else {
                     return ValidationResult.error("Номер должен быть от 1 до " + AnReg.getMaxNumber());
                  }
               } catch (NumberFormatException e) {
                  return ValidationResult.error("Укажите номер анархии числом");
               }
            }).optional();
            List<String> nums = new ArrayList<>();
            for (int i = 1; i <= AnReg.getMaxNumber(); i++) {
               nums.add(String.valueOf(i));
            }
            p.suggests(nums);
         })
         .handler(this::handle)
         .build();
   }

   private void handle(CommandContext ctx) {
      if (mc.player != null && mc.world != null) {
         if (!ctx.arguments().isEmpty() && ctx.arguments().getFirst() != null) {
            int number = (Integer) ctx.arguments().getFirst();
            String error = this.tp.start(number);
            if (error != null) {
               MessageUtility.error(Text.of(error));
            } else {
               MessageUtility.info(Text.of("Переход на анархию #" + number + "..."));
            }
         } else {
            mc.player.networkHandler.sendChatCommand("hub");
            MessageUtility.info(Text.of("Переход в хаб..."));
         }
      }
   }
}
