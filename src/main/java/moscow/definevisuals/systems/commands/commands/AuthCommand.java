package moscow.definevisuals.systems.commands.commands;

import java.util.Map;
import java.util.Map.Entry;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.commands.Command;
import moscow.definevisuals.systems.commands.CommandBuilder;
import moscow.definevisuals.systems.commands.CommandContext;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.modules.player.AutoAuth;
import moscow.definevisuals.utility.game.MessageUtility;
import net.minecraft.text.Text;

public class AuthCommand {
   public Command command() {
      return CommandBuilder.begin("auth", b -> b.aliases("autoAuth", "пароли", "passwords").desc("commands.auth.description").handler(this::handle)).build();
   }
   private void handle(CommandContext ctx) {
      Map<String, String> map = DefineVisuals.getInstance().getModuleManager().getModule(AutoAuth.class).listPassword();
      int counter = 1;
      if (map.isEmpty()) {
         MessageUtility.error(Text.of(Localizator.translate("commands.auth.empty")));
      } else {
         MessageUtility.info(Text.of(Localizator.translate("commands.auth.passwords")));

         for (Entry<String, String> entry : map.entrySet()) {
            String nickname = entry.getKey();
            String password = entry.getValue();
            MessageUtility.info(Text.of(counter++ + ") Ник: " + nickname + " | Пароль: " + password));
         }
      }
   }
}
