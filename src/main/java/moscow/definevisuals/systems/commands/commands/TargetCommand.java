package moscow.definevisuals.systems.commands.commands;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.commands.Command;
import moscow.definevisuals.systems.commands.CommandBuilder;
import moscow.definevisuals.systems.commands.CommandContext;
import moscow.definevisuals.systems.commands.ValidationResult;
import moscow.definevisuals.systems.target.TargetManager;

public class TargetCommand {
   public Command command() {
      return CommandBuilder.begin(
            "target",
            b -> b.aliases("targets")
               .desc("Управление списком таргетов")
               .param("action", p -> p.literal("add", "remove", "del", "delete", "clear", "list"))
               .param("id", p -> p.optional().validator(ValidationResult::ok))
               .handler(this::handle)
         )
         .build();
   }
   private void handle(CommandContext ctx) {
      String action = (String)ctx.arguments().get(0);
      String id = (String)ctx.arguments().get(1);
      TargetManager tm = DefineVisuals.getInstance().getTargetManager();
      String var5 = action.toLowerCase();
      switch (var5) {
         case "add":
            tm.addTarget(id);
            break;
         case "remove":
         case "del":
         case "delete":
            tm.removeTarget(id);
            break;
         case "clear":
            tm.clearTarget();
            break;
         case "list":
            tm.listTarget();
      }
   }
}
