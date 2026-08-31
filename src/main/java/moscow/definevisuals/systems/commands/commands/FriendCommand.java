package moscow.definevisuals.systems.commands.commands;

import java.util.List;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.commands.Command;
import moscow.definevisuals.systems.commands.CommandBuilder;
import moscow.definevisuals.systems.commands.CommandContext;
import moscow.definevisuals.systems.commands.ValidationResult;
import moscow.definevisuals.systems.friends.FriendManager;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.utility.game.MessageUtility;
import net.minecraft.text.Text;

public class FriendCommand {
   public Command command() {
      return CommandBuilder.begin(
            "friend",
            b -> b.aliases("friends")
               .desc("commands.friends.description")
               .param("action", p -> p.literal("add", "remove", "del", "delete", "clear", "list"))
               .param("id", p -> p.optional().validator(ValidationResult::ok))
               .handler(this::handle)
         )
         .build();
   }
   private void handle(CommandContext ctx) {
      String action = (String)ctx.arguments().get(0);
      String id = (String)ctx.arguments().get(1);
      FriendManager fm = DefineVisuals.getInstance().getFriendManager();
      String var5 = action.toLowerCase();
      switch (var5) {
         case "add":
            fm.add(id);
            break;
         case "remove":
         case "del":
         case "delete":
            fm.remove(id);
            break;
         case "clear":
            fm.clear();
            break;
         case "list":
            this.printList();
      }
   }
   private void printList() {
      List<String> friends = DefineVisuals.getInstance().getFriendManager().listFriends();
      if (friends.isEmpty()) {
         MessageUtility.info(Text.of(Localizator.translate("commands.friends.empty")));
      } else {
         MessageUtility.info(Text.of(Localizator.translate("commands.friends.list")));

         for (int i = 0; i < friends.size(); i++) {
            MessageUtility.info(Text.of("[" + (i + 1) + "] " + friends.get(i)));
         }
      }
   }
}
