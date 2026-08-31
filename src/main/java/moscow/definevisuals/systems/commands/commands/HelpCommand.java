package moscow.definevisuals.systems.commands.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.commands.Command;
import moscow.definevisuals.systems.commands.CommandBuilder;
import moscow.definevisuals.systems.commands.CommandContext;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.utility.game.MessageUtility;
import net.minecraft.text.Text;

public class HelpCommand {
   public Command command() {
      return CommandBuilder.begin("help", b -> b.aliases("помощь", "команды", "commands", "helpme").desc("commands.help.description").handler(this::handle))
         .build();
   }
   private void handle(CommandContext ctx) {
      List<Command> list = new ArrayList<>(DefineVisuals.getInstance().getCommandManager().commands());
      list.sort(Comparator.comparing(c -> c.names().getFirst(), String.CASE_INSENSITIVE_ORDER));
      List<String> infos = new ArrayList<>();
      int counter = 1;

      for (Command command : list) {
         infos.add(
            String.format(
               "%d) %s%s - %s",
               counter++,
               DefineVisuals.getInstance().getCommandManager().getPrefix(),
               command.names().getFirst(),
               Localizator.translate(command.description())
            )
         );
      }

      MessageUtility.info(Text.of("Доступные команды:\n" + String.join("\n", infos)));
   }
}
